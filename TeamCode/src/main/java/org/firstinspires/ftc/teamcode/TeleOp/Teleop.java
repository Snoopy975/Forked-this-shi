package org.firstinspires.ftc.teamcode.TeleOp;

import static dev.nextftc.bindings.Bindings.button;
import static dev.nextftc.ftc.ActiveOpMode.isStopRequested;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import dev.nextftc.bindings.BindingManager;

import dev.nextftc.hardware.impl.MotorEx;

@TeleOp(name = "tele")
public class Teleop extends LinearOpMode {
    @Override
    public void runOpMode() {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("fl");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("bl");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("fr");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("br");
        DcMotor motor = hardwareMap.dcMotor.get("Intake");
        DcMotor motor2 = hardwareMap.dcMotor.get("shooter");
        Servo servo = hardwareMap.get(Servo.class, "gate");
        Servo servo2 = hardwareMap.get(Servo.class, "adj");

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor2.setDirection(DcMotorSimple.Direction.REVERSE);

        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT));
        imu.initialize(parameters);

        telemetry.addData("servo position", servo.getPosition());
        telemetry.addData("adj position", servo2.getPosition());

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            // This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.y) {
                imu.resetYaw();
            }

            telemetry.update();

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;  // Counteract imperfect strafing

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            if (gamepad2.a) {
                motor.setPower(1);
            } else {
                motor.setPower(0);
            }

            if (gamepad2.b) {
                motor.setPower(-1);
            } else {
                motor.setPower(0);
            }


            if (gamepad2.right_bumper) {
                motor2.setPower(.78);
            } else {
                motor2.setPower(0);
            }
            if (gamepad1.a) {
                servo.setPosition(.6);
            } else {
                servo.setPosition(.75);
            }


            if (gamepad2.dpad_up) {
                servo2.setPosition(.8);
            }
            if (gamepad2.dpad_down) {
                servo2.setPosition(.5);
            }
        }
    }
}
