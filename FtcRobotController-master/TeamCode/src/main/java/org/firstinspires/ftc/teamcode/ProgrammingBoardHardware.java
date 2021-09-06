package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;

// Common hardware definitions for Autonomous and TeleOp.
public class ProgrammingBoardHardware {
      
    public final HardwareMap hwMap;
    public final DcMotor leftFrontMotor;
    //**TODO public final Servo basicServo;
    //**TODO public final ColorSensor colorSensor;

    public ProgrammingBoardHardware(HardwareMap hwm) {
        hwMap = hwm;
   
        // Motor
        leftFrontMotor = hwMap.get(DcMotor.class, "lf");
        leftFrontMotor.setDirection(DcMotor.Direction.REVERSE);
        leftFrontMotor.setPower(0);
        leftFrontMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
 
        // Servo
        //**TODO basicServo = hwMap.get(Servo.class, "basic_servo");
    
        // Color sensor
        //**TODO colorSensor = hwMap.get(ColorSensor.class, "color_sensor");
    }
}
