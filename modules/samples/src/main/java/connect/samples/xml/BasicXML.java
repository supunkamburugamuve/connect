package connect.samples.xml;

import connect.ConnectConstants;
import connect.ModuleName;
import connect.QualifiedName;
import connect.config.Configuration;
import connect.config.SystemConfiguration;
import connect.config.UserConfiguration;
import connect.env.ExecutionEnvironment;
import connect.error.ConnectError;
import connect.error.ErrorFactory;
import connect.executor.MessageExecutor;
import connect.executor.SimpleMessageExecutor;
import connect.flow.Flow;
import connect.function.CoreFunctionConstants;
import connect.function.core.ConnectFunction;
import connect.lang.Module;
import connect.lang.Reference;
import connect.lang.Variable;
import connect.lang.imports.Import;
import connect.lang.imports.ModuleImport;
import connect.lang.mappers.MessageMapper;
import connect.lang.modules.SystemModuleFactory;
import connect.lang.stmts.AssignmentStatement;
import connect.lang.stmts.FunctionCallStatement;
import connect.lang.stmts.NewObjectStatement;
import connect.lang.stmts.ValueStatement;
import connect.message.Message;
import connect.message.MessageListener;
import connect.message.MessageTrace;
import connect.message.TraceRecord;
import connect.queue.InMemoryLinkQueue;
import connect.queue.LinkQueue;
import connect.queue.QueueListener;
import connect.xml.XMLConstants;
import connect.xml.config.XMLConfiguration;
import connect.xml.endpoint.ListeningXMLEndpoint;
import connect.xml.endpoint.SendingXMLEndpoint;
import connect.xml.error.XMLErrorTypes;
import connect.xml.message.XMLMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class BasicXML {
    public static final String MODULE = "test";
    public static final String EXECUTOR = "executor";

    public static final String FLOW = "main-flow";

    public static final String FLOW_IN_QUEUE = "flowIn";
    public static final String FIRST_ENDPOINT_IN_QUEUE = "firstEndPointIn";
    public static final String LAST_ENDPOINT_IN_QUEUE = "lastEndPointIn";
    public static final String RECEIVE_QUEUE = "receive";

    public static final String FIRST_ENDPOINT = "firstEpr";
    public static final String LAST_ENDPOINT = "lastEpr";
    public static final String MAIN_EXECUTOR = "main-executor";

    public static void main(String[] args) throws Exception {
        BasicXML test = new BasicXML();
        test.testInjection();
    }

    public void testInjection() throws Exception {
        SystemConfiguration sysCfg = new SystemConfiguration("connect");
        sysCfg.addModule(SystemModuleFactory.createSystemModule());
        sysCfg.addVariableMapper(new MessageMapper());

        Configuration cfg = new Configuration(sysCfg);
        cfg.addTypeConfiguration(ConnectConstants.MessageTypes.XML, new XMLConfiguration());

        UserConfiguration userCfg = createMainConfiguration();
        cfg.setUserConfiguration(userCfg);

        ExecutionEnvironment environment = new ExecutionEnvironment(cfg);
        environment.init();
        environment.start();

        MessageTrace trace = new MessageTrace("11234");
        trace.addRecord(new TraceRecord("11234"));
        XMLMessage message = new XMLMessage(readXML(), environment, trace);

        LinkQueue outQueue = (LinkQueue) cfg.getModule(new ModuleName(MODULE)).getEntity(new Reference(RECEIVE_QUEUE));
        QueueListener listener = new QueueListener("out", outQueue, new MessageListener() {
            public void onMessage(Message m) {
                System.out.println("message received");
                printXML((Node) m.getPayload());
            }
        });
        listener.start();

        LinkQueue queue =(LinkQueue) cfg.getModule(new ModuleName(MODULE)).getEntity(FIRST_ENDPOINT_IN_QUEUE);

        queue.offer(message);

        Thread.sleep(60000);
    }

    private UserConfiguration createMainConfiguration() {
        Module module = new Module(new ModuleName(MODULE));
        // we will use only one executor
        MessageExecutor executor = new SimpleMessageExecutor(EXECUTOR);

        LinkQueue endPtIn = new InMemoryLinkQueue(FIRST_ENDPOINT_IN_QUEUE, 1000);
        LinkQueue flowIn = new InMemoryLinkQueue(FLOW_IN_QUEUE, 1000);
        LinkQueue outEprIn = new InMemoryLinkQueue(LAST_ENDPOINT_IN_QUEUE, 1000);
        LinkQueue receive = new InMemoryLinkQueue(RECEIVE_QUEUE, 1000);

        // we need to parse the configuration
        ListeningXMLEndpoint listeningEndpoint = new ListeningXMLEndpoint("hello-listener");
        listeningEndpoint.setInputQueue(new Reference(FIRST_ENDPOINT_IN_QUEUE));
        listeningEndpoint.setNextQueue(new Reference(FLOW_IN_QUEUE));


        SendingXMLEndpoint sendingXMLEndpoint = new SendingXMLEndpoint("hello-sender");
        sendingXMLEndpoint.setInputQueue(new Reference(LAST_ENDPOINT_IN_QUEUE));
        sendingXMLEndpoint.setNextQueue(new Reference(RECEIVE_QUEUE));

        Flow flow = createFlow();

        Import moduleImport = new ModuleImport(new ModuleName(XMLConstants.XML_MODULE));
        module.addImport(moduleImport);

        moduleImport = new ModuleImport(new ModuleName("system", "connect"));
        module.addImport(moduleImport);

        module.addEntity(EXECUTOR, executor);

        module.addEntity(FIRST_ENDPOINT_IN_QUEUE, endPtIn);
        module.addEntity(FLOW_IN_QUEUE, flowIn);
        module.addEntity(LAST_ENDPOINT_IN_QUEUE, outEprIn);
        module.addEntity(RECEIVE_QUEUE, receive);

        module.addEntity(FIRST_ENDPOINT, listeningEndpoint);
        module.addEntity(LAST_ENDPOINT, sendingXMLEndpoint);

        module.addEntity(FLOW, flow);

        UserConfiguration configuration = new UserConfiguration();
        configuration.addModule(module);

        return configuration;
    }

    private Flow createFlow() {
        // parse a flow
        Flow flow = new Flow(FLOW, ConnectConstants.MessageTypes.XML);
        flow.setInputQueue(new Reference(FLOW_IN_QUEUE));

        flow.setExecutor(new Reference(EXECUTOR));
        // parse a variable to hold the xslt node
        Variable xsltVariable = new Variable("xslt");
        // parse some statements
        AssignmentStatement assignmentStatement = new AssignmentStatement();

        // in assignment statement we assign the xslt node to the variable
        assignmentStatement.setVariable(xsltVariable);
        // use the new object statement to parse a new XML node
        NewObjectStatement xsltObjectStatement = new NewObjectStatement(new Reference(new QualifiedName(XMLConstants.XML_CREATOR, XMLConstants.XML_MODULE)));
        // get the XSLT String
        ValueStatement valueStatement = new ValueStatement(readXSLT());

        // add it as a parameter to object statement
        xsltObjectStatement.addParameter("element", valueStatement);
        assignmentStatement.setStatement(xsltObjectStatement);


        FunctionCallStatement xsltCall = new FunctionCallStatement(
                new Reference(new QualifiedName(XMLConstants.XSLT_FUNCTION, XMLConstants.XML_MODULE)));
        // we add the element directly as a node for simplicity
        xsltCall.addParameter(XMLConstants.PARAM_ELEMENT, null);
        xsltCall.addParameter(XMLConstants.PARAM_XSLT, new ValueStatement(xsltVariable));


        FunctionCallStatement connectCall = new FunctionCallStatement(new Reference(new QualifiedName(CoreFunctionConstants.CONNECT_FUNCTION, "system", "connect")));
        connectCall.addParameter(ConnectFunction.PARAM_NEXT, new ValueStatement(new Reference(LAST_ENDPOINT_IN_QUEUE)));

        // flow has the XSLT object creation statement and function call statement
        flow.addStatement(assignmentStatement);
        // flow.addStatement(xsltCall);

        // add variable assignment
        AssignmentStatement asignStatement = new AssignmentStatement();
        asignStatement.setVariable(new Variable("msg"));

        asignStatement.setStatement(xsltCall);

        flow.addStatement(asignStatement);
        flow.addStatement(connectCall);


        return flow;
    }

    private Node readXML(){
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        URL url = BasicXML.class.getClassLoader().getResource("hello.xml");
        try {
            File file = new File(url.toURI());
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            Document doc = db.parse(file);
            return doc.getDocumentElement();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readXML2(){
        //get the factory
        String fileContent = "";
        URL url = BasicXML.class.getClassLoader().getResource("hello.xml");
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Scanner sc;
        try {
            sc = new Scanner(file);
            while(sc.hasNextLine()){
                fileContent = fileContent + sc.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    private String readXSLT() {
        String fileContent = "";
        URL url = BasicXML.class.getClassLoader().getResource("hello.xslt");
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Scanner sc;
        try {
            sc = new Scanner(file);
            while(sc.hasNextLine()){
                fileContent = fileContent + sc.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    public void testXSLT() throws ParserConfigurationException {
        TransformerFactory transFact = TransformerFactory.newInstance();
        Node node = readXML();
        Node xslt = null;
        String xsltString = readXSLT();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            xslt = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xsltString.getBytes())).getDocumentElement();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        printXML(node);
        printXML(xslt);

        Source xmlSource = new DOMSource(node);
        Source xsltSource = new DOMSource(xslt);
        Document resultDoc = dbf.newDocumentBuilder().newDocument();
        // apply the xslt to the payload
        DOMResult result = new DOMResult(resultDoc);

        // parse an instance of TransformerFactory
        Transformer trans;
        try {
            trans = transFact.newTransformer(xsltSource);

            trans.transform(xmlSource, result);
            Node r = result.getNode();

            printXML(r);
        } catch (TransformerConfigurationException e) {
            String msg = "Failed to do the XSLT transformation";
            ConnectError error = ErrorFactory.create(e, XMLErrorTypes.XSLT_TRANSFORM_CONFIGURATION_ERROR, msg);
        } catch (TransformerException e) {
            String msg = "Failed to do the XSLT transformation";
            ConnectError error = ErrorFactory.create(e, XMLErrorTypes.XSLT_TRANSFORM_ERROR, msg);
        }
    }

    void printXML(Node node) {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        StringWriter buffer = new StringWriter();
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        try {
            transformer.transform(new DOMSource(node),
                    new StreamResult(buffer));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        String str = buffer.toString();
        System.out.println(str);
    }
}
