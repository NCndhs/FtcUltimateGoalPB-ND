package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.ftcdevcommon.RobotLogCommon;
import org.firstinspires.ftc.ftcdevcommon.android.WorkingDirectory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.ProgrammingBoardHardware;
import org.firstinspires.ftc.teamcode.common.FTCErrorHandling;
import org.firstinspires.ftc.teamcode.common.RobotConstants;
import org.firstinspires.ftc.teamcode.teleop.common.FTCButton;

@TeleOp(name = "TeleOp Drive", group = "Drive")
//@Disabled
public class TeleOpDrive extends LinearOpMode {

    private static final String TAG = "TeleOpDrive "; // logging tag identifier

    private ProgrammingBoardHardware robot;
    private final FTCButton driveButton = new FTCButton();

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addData("Initializing...", "Please wait until complete");
        telemetry.update();

        try {
            RobotLogCommon.initialize(WorkingDirectory.getWorkingDirectory() + RobotConstants.logDir);
            robot = new ProgrammingBoardHardware(hardwareMap);
            robot.leftFrontMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            telemetry.addData("Initialized!", "Ready to run");
            telemetry.update();

            waitForStart(); // wait for the driver to push "Start"

            while (opModeIsActive()) {
                updateButtons();
                updatePlayerOne();
            }
        } catch (Exception ex) {
            FTCErrorHandling.handleFtcErrors(ex, TAG, this);
        } finally {
            RobotLogCommon.closeLog();
        }
    }

    // Update the state of the active buttons. This method should be
    // called once per cycle.
    private void updateButtons() {
        driveButton.update(gamepad1.right_bumper);
    }

    // Execute the action(s) controlled by Player 1. This method
    // should be called once per cycle.
    private void updatePlayerOne() {
        if (driveButton.is(FTCButton.State.HELD)) {
            robot.leftFrontMotor.setPower(0.5);
            telemetry.addData("Driving", "Left front");
            telemetry.update();
        } else
            robot.leftFrontMotor.setPower(0.0);
    }

}

