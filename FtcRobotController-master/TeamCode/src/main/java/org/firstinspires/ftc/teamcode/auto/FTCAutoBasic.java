package org.firstinspires.ftc.teamcode.auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.ftcdevcommon.AutonomousRobotException;
import org.firstinspires.ftc.ftcdevcommon.RobotLogCommon;
import org.firstinspires.ftc.teamcode.ProgrammingBoardHardware;
import org.firstinspires.ftc.teamcode.common.RobotConstants;
import org.firstinspires.ftc.teamcode.common.RobotConstantsUltimateGoal;

public class FTCAutoBasic {

    private static final String TAG = "FTCAutoBasic";

    private final RobotConstantsUltimateGoal.OpMode autoOpMode;
    private final RobotConstants.Alliance alliance;
    private final LinearOpMode linearOpMode;
    private final ProgrammingBoardHardware robot;

    // Main class for the autonomous run.
    public FTCAutoBasic(RobotConstantsUltimateGoal.OpMode pAutoOpMode, RobotConstants.Alliance pAlliance, LinearOpMode pLinearOpMode) {

        RobotLogCommon.c(TAG, "FTCAutoBasic constructor");

        autoOpMode = pAutoOpMode;
        alliance = pAlliance;
        if (alliance == RobotConstants.Alliance.UNKNOWN)
            throw new AutonomousRobotException(TAG, "Alliance is UNKNOWN");

        linearOpMode = pLinearOpMode;
        robot = new ProgrammingBoardHardware(linearOpMode.hardwareMap);

        // NOTE: the order of these two operations is important. If they
        // are reversed, the robot does not move.
        robot.leftFrontMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftFrontMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        RobotLogCommon.c(TAG, "FTCAutoBasic construction complete");
    }

    public void runRobot() {

        RobotLogCommon.i(TAG, "At start");
        RobotLogCommon.i(TAG, "OpMode: " + autoOpMode + ", Alliance: " + alliance);

        ElapsedTime runtime = new ElapsedTime();
        try {
            RobotLogCommon.d(TAG, "Driving the left front motor");
            runtime.reset();
            robot.leftFrontMotor.setPower(0.5);
            while (linearOpMode.opModeIsActive() && (runtime.seconds() < 3.0)) {
                // Robot moves
                linearOpMode.telemetry.addData("Driving", "Left front");
                linearOpMode.telemetry.update();
            }
            RobotLogCommon.d(TAG, "Finished driving the left front motor");
        } finally {
            robot.leftFrontMotor.setPower(0.0);
            RobotLogCommon.i(TAG, "Exiting FTCAutoBasic");
            linearOpMode.telemetry.addData("FTCAutoBasic", "COMPLETE");
            linearOpMode.telemetry.update();
        }
    }

}

