package org.firstinspires.ftc.teamcode.common;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.ftcdevcommon.AutonomousLoggingException;
import org.firstinspires.ftc.ftcdevcommon.AutonomousRobotException;
import org.firstinspires.ftc.ftcdevcommon.RobotLogCommon;

import static android.os.SystemClock.sleep;

// Common error handling for Autonomous and TeleOp.
public class FTCErrorHandling {

    // Catch clauses for FTC Autonomous and TeleOp ---

    // Keep this comment here as a warning!
    // You can't call System.exit(1); because the robot controller then exits and
    // can't be restarted from the driver station. The result is that if you get
    // a fatal error during the autonomous run and exit you can't restart the robot
    // for the driver-controlled run.

    // NOTE: from the ftc documentation - "Please do not swallow the InterruptedException,
    // as it is used in cases where the op mode needs to be terminated early."
    public static void handleFtcErrors(Exception ex, String pTag, LinearOpMode pLinear) throws InterruptedException {
        if (ex instanceof InterruptedException) {
            RobotLogCommon.d(pTag, "Caught InterruptedException; rethrowing");
            throw (InterruptedException) ex;
        }

        // For all other exceptions don't ever do this in Autonomous:
        // throw(arx); // propagate error
        // - rethrowing shuts down the entire application and prevents
        // TeleOp from starting.

        // The method below holds the error message on the DS screen while
        // not triggering the DS to restart the RC because of an absence of
        // communication.
        if (ex instanceof AutonomousRobotException) {
            if (ex instanceof AutonomousLoggingException) // log logging problems to the Android log
                Log.e(pTag + " fatal logging error", ex.getMessage());
            else
                RobotLogCommon.d(((AutonomousRobotException) ex).getTag(), ex.getMessage());

            do {
                pLinear.telemetry.addData(pTag + " fatal error", ex.getMessage());
                pLinear.telemetry.update();
                pLinear.telemetry.clearAll(); // do not repeat the message
                sleep(1000);
            } while (!pLinear.isStopRequested());
        } else {
            // Must be an Exception
            RobotLogCommon.d(pTag, ex.getMessage());
            do {
                pLinear.telemetry.addData(pTag + " fatal Exception", ex.getMessage());
                pLinear.telemetry.update();
                pLinear.telemetry.clearAll(); // do not repeat the message
                sleep(1000);
            } while (!pLinear.isStopRequested());
        }
    }
}