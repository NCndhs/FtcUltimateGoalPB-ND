package org.firstinspires.ftc.teamcode.common;

import org.firstinspires.ftc.ftcdevcommon.AutonomousRobotException;
import org.firstinspires.ftc.ftcdevcommon.RobotLogCommon;
import org.firstinspires.ftc.ftcdevcommon.RobotXMLElement;
import org.firstinspires.ftc.teamcode.auto.vision.VisionParameters;
import org.firstinspires.ftc.teamcode.auto.xml.ImageXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class RobotActionXML {

    public static final String TAG = "RobotActionXML";
    private static final String FILE_NAME = "RobotAction.xml";

    private final Document document;
    private final XPath xpath;

    /*
    // IntelliJ only
    private static final String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String W3C_XML_SCHEMA =
            "http://www.w3.org/2001/XMLSchema";
     */

    public RobotActionXML(String pWorkingDirectory) throws ParserConfigurationException, SAXException, IOException {

/*
// IntelliJ only
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setIgnoringComments(true);
        dbFactory.setNamespaceAware(true);
        dbFactory.setValidating(true);
        dbFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

        //## ONLY works with a validating parser (DTD or schema),
        // which the IntelliJ parser is.
        dbFactory.setIgnoringElementContentWhitespace(true);
// End IntelliJ only
*/

// Android only
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setIgnoringComments(true);
        //## ONLY works with a validating parser (DTD or schema),
        // which the Android Studio parser is not.
        // dbFactory.setIgnoringElementContentWhitespace(true);
        //PY 8/17/2019 Android throws UnsupportedOperationException dbFactory.setXIncludeAware(true);
// End Android only

        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        String actionFilename = pWorkingDirectory + FILE_NAME;
        document = dBuilder.parse(new File(actionFilename));

        XPathFactory xpathFactory = XPathFactory.newInstance();
        xpath = xpathFactory.newXPath();
    }

    // Find the requested opMode in the RobotAction.xml file.
    // Package and return all data associated with the OpMode.
    public RobotActionData getOpModeData(String pOpMode) throws XPathExpressionException {

        Level lowestLoggingLevel = null; // null means use the default lowest logging level
        StartingPositionData startingPositionData = null;
        VisionParameters.FTCRect imageROI = new VisionParameters.FTCRect(0, 0, 0, 0); // default is an empty ROI; use the values from RingParameters.xml.
        List<RobotConstantsUltimateGoal.SupportedVumark> vumarksOfInterest = new ArrayList<>();
        List<RobotXMLElement> actions = new ArrayList<>();

        // Use XPath to locate the desired OpMode.
        String opModePath = "/RobotAction/OpMode[@id=" + "'" + pOpMode + "']";
        Node opModeNode = (Node) xpath.evaluate(opModePath, document, XPathConstants.NODE);
        if (opModeNode == null)
            throw new AutonomousRobotException(TAG, "Missing OpMode " + pOpMode);

        RobotLogCommon.c(TAG, "Extracting data from RobotAction.xml for OpMode " + pOpMode);

        // The next element in the XML is required: <parameters>
        Node parametersNode = getNextElement(opModeNode.getFirstChild());
        if ((parametersNode == null) || !parametersNode.getNodeName().equals("parameters"))
            throw new AutonomousRobotException(TAG, "Missing required <parameters> element");

        // The four possible elements under <parameters> are:
        //   <lowest_logging_level>
        //   <starting_position>
        //   <image_roi>
        //   <vumarks>
        // All are optional.

        // A missing or empty optional lowest_logging_level will eventually return null, which
        // means to use the logger's default.
        Node nextParameterNode = getNextElement(parametersNode.getFirstChild());
        if ((nextParameterNode != null) && (nextParameterNode.getNodeName().equals("lowest_logging_level"))) {
            String lowestLoggingLevelString = nextParameterNode.getTextContent().trim();
            if (!lowestLoggingLevelString.isEmpty()) {
                switch (lowestLoggingLevelString) {
                    case "d": {
                        lowestLoggingLevel = Level.FINE;
                        break;
                    }
                    case "v": {
                        lowestLoggingLevel = Level.FINER;
                        break;
                    }
                    case "vv": {
                        lowestLoggingLevel = Level.FINEST;
                        break;
                    }
                    default: {
                        throw new AutonomousRobotException(TAG, "Invalid lowest logging level");
                    }
                }
            }
            nextParameterNode = getNextElement(nextParameterNode.getNextSibling());
        }

        // The next optional element in the XML is <starting_position>.
        if ((nextParameterNode != null) && nextParameterNode.getNodeName().equals("starting_position")) {
            // Get the value from each child of the starting_position element:
            // <x>79.0</x>
            // <y>188.0</y>
            // <angle>0.0</angle>
            double x;
            double y;
            double angle;
            Node xNode = getNextElement(nextParameterNode.getFirstChild());
            if ((xNode == null) || !xNode.getNodeName().equals("x") || xNode.getTextContent().isEmpty())
                throw new AutonomousRobotException(TAG, "Element 'x' missing or empty");

            try {
                x = Double.parseDouble(xNode.getTextContent());
            } catch (NumberFormatException nex) {
                throw new AutonomousRobotException(TAG, "Invalid number format in element 'x'");
            }

            Node yNode = getNextElement(xNode.getNextSibling());
            if ((yNode == null) || !yNode.getNodeName().equals("y") || yNode.getTextContent().isEmpty())
                throw new AutonomousRobotException(TAG, "Element 'y' missing or empty");

            try {
                y = Double.parseDouble(yNode.getTextContent());
            } catch (NumberFormatException nex) {
                throw new AutonomousRobotException(TAG, "Invalid number format in element 'y'");
            }

            Node angleNode = getNextElement(yNode.getNextSibling());
            if ((angleNode == null) || !angleNode.getNodeName().equals("angle") || angleNode.getTextContent().isEmpty())
                throw new AutonomousRobotException(TAG, "Element 'angle' missing or empty");

            try {
                angle = Double.parseDouble(angleNode.getTextContent());
            } catch (NumberFormatException nex) {
                throw new AutonomousRobotException(TAG, "Invalid number format in element 'angle'");
            }

            startingPositionData = new StartingPositionData(x, y, angle);
            nextParameterNode = getNextElement(nextParameterNode.getNextSibling());
        }

        // The next optional element in the XML is <image_roi>.
        if ((nextParameterNode != null) && nextParameterNode.getNodeName().equals("image_roi")) {
            imageROI = ImageXML.parseROI(nextParameterNode);
            nextParameterNode = getNextElement(nextParameterNode.getNextSibling());
        }

        // The next optional element in the XML is <vumarks>.
        if ((nextParameterNode != null) && nextParameterNode.getNodeName().equals("vumarks")) {
            NodeList vumarkChildren = nextParameterNode.getChildNodes();
            Node oneVumarkNode;
            for (int i = 0; i < vumarkChildren.getLength(); i++) {
                oneVumarkNode = vumarkChildren.item(i);

                if (oneVumarkNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                RobotConstantsUltimateGoal.SupportedVumark oneVumark = RobotConstantsUltimateGoal.SupportedVumark.valueOf(oneVumarkNode.getNodeName());
                vumarksOfInterest.add(oneVumark);
            }
            nextParameterNode = getNextElement(nextParameterNode.getNextSibling());
        }

        // Make sure there are no extraneous elements.
        if (nextParameterNode != null)
                  throw new AutonomousRobotException(TAG, "Unrecognized element under <parameters>");

        // Now proceed to the <actions> element of the selected OpMode.
        String actionsPath = opModePath + "/actions";
        Node actionsNode = (Node) xpath.evaluate(actionsPath, document, XPathConstants.NODE);
        if (actionsNode == null)
            throw new AutonomousRobotException(TAG, "Missing <actions> element");

        // Now iterate through the children of the <actions> element of the selected OpMode.
        NodeList actionChildren = actionsNode.getChildNodes();
        Node actionNode;

        RobotXMLElement actionXMLElement;
        boolean foundOcvChoice = false;
        HashMap<RobotConstantsUltimateGoal.TargetZone, List<RobotXMLElement>> targetZoneActions = null;
        for (int i = 0; i < actionChildren.getLength(); i++) {
            actionNode = actionChildren.item(i);

            if (actionNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            actionXMLElement = new RobotXMLElement((Element) actionNode);
            actions.add(actionXMLElement);

            if (actionNode.getNodeName().equals("OCV_CHOICE")) {
                if (foundOcvChoice)
                    throw new AutonomousRobotException(TAG, "Only one OCV_CHOICE element is allowed");
                foundOcvChoice = true;

                // Collect all of the RobotXMLElement(s) for each target zone.
                // The elements will be fed into the stream of actions at run-time
                // depending on the outcome of the OCV recognition
                targetZoneActions = getTargetZoneActions(actionNode);
            }
        }

        return new RobotActionData(lowestLoggingLevel, imageROI, vumarksOfInterest, startingPositionData,
                actions, targetZoneActions);
    }

    private Node getNextElement(Node pNode) {
        Node nd = pNode;
        while (nd != null) {
            if (nd.getNodeType() == Node.ELEMENT_NODE) {
                return nd;
            }
            nd = nd.getNextSibling();
        }
        return null;
    }

    // Get the target zone actions associated with an OpMode.
    // The key of the return map is the target zone.
    private HashMap<RobotConstantsUltimateGoal.TargetZone, List<RobotXMLElement>> getTargetZoneActions(Node pOcvChoiceNode) {
        HashMap<RobotConstantsUltimateGoal.TargetZone, List<RobotXMLElement>> targetZoneActions = new HashMap<>();
        List<RobotXMLElement> actions;

        RobotLogCommon.i(TAG, "Processing xml for target zones");

        NodeList ocvChoiceChildren = pOcvChoiceNode.getChildNodes();
        if (ocvChoiceChildren == null)
            throw new AutonomousRobotException(TAG, "Missing TARGET_ZONE elements");

        Node targetZoneNode;
        int targetZoneCount = 0;
        for (int i = 0; i < ocvChoiceChildren.getLength(); i++) {
            targetZoneNode = ocvChoiceChildren.item(i);

            if (targetZoneNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            targetZoneCount++;
            RobotConstantsUltimateGoal.TargetZone targetZone = RobotConstantsUltimateGoal.TargetZone.valueOf(targetZoneNode.getNodeName());
            actions = collectActions(targetZoneNode.getChildNodes());
            targetZoneActions.put(targetZone, actions);
        }

        if (targetZoneCount != 3)
          throw new AutonomousRobotException(TAG, "Missing one or more TARGET_ZONE elements");

        return targetZoneActions;
    }

    // Iterate through the children of the selected target zone node
    // and collect the elements. Note: a TARGET_ZONE element with no
    // children is valid.
    private List<RobotXMLElement> collectActions(NodeList pNodeList) {

        List<RobotXMLElement> actions = new ArrayList<>();
        Node oneActionNode;
        RobotXMLElement actionXMLElement;

        for (int i = 0; i < pNodeList.getLength(); i++) {
            oneActionNode = pNodeList.item(i);

            if (oneActionNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            actionXMLElement = new RobotXMLElement((Element) oneActionNode);
            actions.add(actionXMLElement);
        }

        return actions;
    }

    public static class RobotActionData {
        public final Level lowestLoggingLevel;
        public final VisionParameters.FTCRect imageROI;
        public final List<RobotConstantsUltimateGoal.SupportedVumark> vumarksOfInterest;
        public final StartingPositionData startingPositionData;
        public final List<RobotXMLElement> actions;
        public final HashMap<RobotConstantsUltimateGoal.TargetZone, List<RobotXMLElement>> targetZoneActions;


        public RobotActionData(Level pLevel, VisionParameters.FTCRect pROI,
                               List<RobotConstantsUltimateGoal.SupportedVumark> pVumarks,
                               StartingPositionData pStartingPositionData,
                               List<RobotXMLElement> pActions,
                               HashMap<RobotConstantsUltimateGoal.TargetZone, List<RobotXMLElement>> pTargetZoneActions) {
            lowestLoggingLevel = pLevel;
            imageROI = pROI;
            vumarksOfInterest = pVumarks;
            actions = pActions;
            targetZoneActions = pTargetZoneActions;
            startingPositionData = pStartingPositionData;
        }
    }

    public static class StartingPositionData {

        public final double startingX; // FTC field coordinates
        public final double startingY; // FTC field coordinates
        public final double startingAngle; // with repsect to the wall

        public StartingPositionData(double pStartingX, double pStartingY, double pStartingAngle) {
            startingX = pStartingX;
            startingY = pStartingY;
            startingAngle = pStartingAngle;
        }
    }
}