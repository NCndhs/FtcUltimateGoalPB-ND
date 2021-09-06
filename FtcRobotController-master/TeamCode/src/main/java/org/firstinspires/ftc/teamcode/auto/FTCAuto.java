package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.ftcdevcommon.AutonomousRobotException;
import org.firstinspires.ftc.ftcdevcommon.RobotLogCommon;
import org.firstinspires.ftc.ftcdevcommon.RobotXMLElement;
import org.firstinspires.ftc.ftcdevcommon.XPathAccess;
import org.firstinspires.ftc.ftcdevcommon.android.WorkingDirectory;
import org.firstinspires.ftc.teamcode.ProgrammingBoardHardware;
import org.firstinspires.ftc.teamcode.common.RobotActionXML;
import org.firstinspires.ftc.teamcode.common.RobotConstants;
import org.firstinspires.ftc.teamcode.common.RobotConstantsUltimateGoal;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;

import static android.os.SystemClock.sleep;

public class FTCAuto {

    private static final String TAG = "FTCAuto";

    private final RobotConstantsUltimateGoal.OpMode autoOpMode;
    private final RobotConstants.Alliance alliance;
    private final LinearOpMode linearOpMode;
    private final ProgrammingBoardHardware robot;
    private final String workingDirectory;
    private final RobotActionXML.RobotActionData actionData; // for the selected OpMode

    //    Rev HD Hex Motor 40:1 RPM 150, clicks per revolution 1120
    private static final double CLICKS_PER_MOTOR_REV = 1120;
    private static final double MOTOR_RPM = 150;
    private static final double MAX_VELOCITY = Math.floor((CLICKS_PER_MOTOR_REV * MOTOR_RPM) / 60); // clicks per second
    private static final double WHEEL_DIAMETER_IN = 4.0;
    private static final double CLICKS_PER_INCH = CLICKS_PER_MOTOR_REV / (WHEEL_DIAMETER_IN * 3.1416);

    // Main class for the autonomous run.
    public FTCAuto(RobotConstantsUltimateGoal.OpMode pAutoOpMode, RobotConstants.Alliance pAlliance, LinearOpMode pLinearOpMode)
            throws ParserConfigurationException, SAXException, XPathException, IOException {

        RobotLogCommon.c(TAG, "FTCAuto constructor");

        autoOpMode = pAutoOpMode;
        alliance = pAlliance;
        if (alliance == RobotConstants.Alliance.UNKNOWN)
            throw new AutonomousRobotException(TAG, "Alliance is UNKNOWN");

        workingDirectory = WorkingDirectory.getWorkingDirectory();

        // Get the directory for the various configuration files.
        String xmlDirectory = workingDirectory + RobotConstants.xmlDir;

        // Read the robot action file for all opmodes. Extract data from
        // the parsed XML file for the selected OpMode only.
        RobotActionXML actionXML = new RobotActionXML(xmlDirectory);
        actionData = actionXML.getOpModeData(autoOpMode.toString());

        Level lowestLoggingLevel = actionData.lowestLoggingLevel;
        if (lowestLoggingLevel != null) // null means use the default
            RobotLogCommon.setMinimimLoggingLevel(lowestLoggingLevel);
        RobotLogCommon.c(TAG, "Lowest logging level " + RobotLogCommon.getMinimumLoggingLevel());

        // Initialize the hardware and methods the control motion.
        linearOpMode = pLinearOpMode;
        robot = new ProgrammingBoardHardware(linearOpMode.hardwareMap);

        // NOTE: the order of these two operations is important. If they
        // are reversed, the robot does not move.
        robot.leftFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        RobotLogCommon.c(TAG, "FTCAuto construction complete");
    }

    public void runRobot() throws XPathException, InterruptedException, IOException {

        RobotLogCommon.i(TAG, "At start");
        RobotLogCommon.i(TAG, "OpMode: " + autoOpMode + ", Alliance: " + alliance);

        // Safety check against ftc runtime initialization errors.
        // Make sure the opmode is still active.
        if (!linearOpMode.opModeIsActive())
            throw new AutonomousRobotException(TAG, "OpMode unexpectedly inactive in runRobot()");

        // Follow the choreography specified in the robot action file.
        List<RobotXMLElement> actions = actionData.actions;
        try {
            for (RobotXMLElement action : actions) {

                if (!linearOpMode.opModeIsActive())
                    return; // better to just bail out

                doCommand(action); // no, but doCommand may change that
            }
        } finally {
            RobotLogCommon.i(TAG, "Exiting FTCAuto");
            linearOpMode.telemetry.addData("FTCAuto", "COMPLETE");
            linearOpMode.telemetry.update();
        }
    }

    //===============================================================================================
    //===============================================================================================

    // Using the XML elements and attributes from the configuration file RobotAction.xml,
    // execute the action.
    private void doCommand(RobotXMLElement pAction) throws XPathException {

        // Set up XPath access to the current action command.
        XPathAccess commandXPath = new XPathAccess(pAction);
        String commandName = pAction.getRobotXMLElementName().toUpperCase();
        RobotLogCommon.d(TAG, "Executing FTCAuto command " + commandName);

        switch (commandName) {

            case "FORWARD_BY_TIME": {
                ElapsedTime runtime = new ElapsedTime();
                try {
                    linearOpMode.telemetry.clear();
                    runtime.reset();
                    robot.leftFrontMotor.setPower(0.5);
                    while (linearOpMode.opModeIsActive() && (runtime.seconds() < 3.0)) {
                        // Robot moves
                        linearOpMode.telemetry.addData("Driving", "Left front forward");
                        linearOpMode.telemetry.update();
                    }
                } finally {
                    robot.leftFrontMotor.setPower(0.0);
                }
                break;
            }

            case "REVERSE_BY_TIME": {
                ElapsedTime runtime = new ElapsedTime();
                try {
                    linearOpMode.telemetry.clear();
                    runtime.reset();
                    robot.leftFrontMotor.setPower(-0.5);
                    while (linearOpMode.opModeIsActive() && (runtime.seconds() < 3.0)) {
                        // Robot moves
                        linearOpMode.telemetry.addData("Driving", "Left front in reverse");
                        linearOpMode.telemetry.update();
                    }
                } finally {
                    robot.leftFrontMotor.setPower(0.0);
                }
                break;
            }

            case "SLEEP": { //I want sleep :)
                int sleepValue = commandXPath.getInt("ms");
                RobotLogCommon.d(TAG, "Pause by " + sleepValue + " milliseconds");
                sleep(sleepValue);
                break;
            }

            case "BREAKPOINT": {
                while (!linearOpMode.gamepad1.a) {
                    sleep(1);
                }
                break;
            }

            default: {
                throw new AutonomousRobotException(TAG, "No support for the command " + commandName);
            }
        }
    }

}

