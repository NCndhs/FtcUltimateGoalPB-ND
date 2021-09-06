package org.firstinspires.ftc.teamcode.auto.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.ftcdevcommon.RobotLogCommon;
import org.firstinspires.ftc.ftcdevcommon.android.WorkingDirectory;
import org.firstinspires.ftc.teamcode.auto.FTCAuto;
import org.firstinspires.ftc.teamcode.auto.FTCAutoBasic;
import org.firstinspires.ftc.teamcode.common.FTCErrorHandling;
import org.firstinspires.ftc.teamcode.common.RobotConstants;
import org.firstinspires.ftc.teamcode.common.RobotConstantsUltimateGoal;

// Use this dispatcher class to place the launching of LCHSAuto and all of the error
// handling in one place.
public class FTCAutoDispatch {

    public void runOpMode(RobotConstantsUltimateGoal.OpMode pOpMode, RobotConstants.Alliance pAlliance, LinearOpMode pLinear) throws InterruptedException {

        final String TAG = pOpMode.toString();
        pLinear.telemetry.setAutoClear(false); // keep our messages on the driver station

        // LCHSAuto, the common class for all autonomous opmodes, needs
        // access to the public data fields and methods in LinearOpMode.
        try {
            RobotLogCommon.initialize(WorkingDirectory.getWorkingDirectory() + RobotConstants.logDir);
            FTCAutoBasic runAuto = new FTCAutoBasic(pOpMode, pAlliance, pLinear);

            pLinear.telemetry.addData(TAG, "Waiting for start ...");
            pLinear.telemetry.update();

            pLinear.waitForStart();

            pLinear.telemetry.addData(TAG, "Running ...");
            pLinear.telemetry.update();

            runAuto.runRobot();
        }
        catch (Exception ex) {
            FTCErrorHandling.handleFtcErrors(ex, TAG, pLinear);
        }
        finally {
            RobotLogCommon.closeLog();
        }
    }
}
