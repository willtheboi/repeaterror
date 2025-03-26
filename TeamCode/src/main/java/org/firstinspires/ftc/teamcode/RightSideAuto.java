package org.firstinspires.ftc.teamcode;


import androidx.annotation.NonNull;

// RR-specific imports
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import org.firstinspires.ftc.teamcode.MecanumDrive;

// Non-RR imports
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.ArmActions;
@Autonomous(name = "RightSideAuto", group = "Autonomous")
public class RightSideAuto extends LinearOpMode{

    @Override
    public void runOpMode() {

        // instantiate your MecanumDrive at a particular pose.
        Pose2d initialPose = new Pose2d(4.5, -31.75, Math.toRadians(90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
        ArmActions armActions = new ArmActions(hardwareMap);
        // actionBuilder builds from the drive steps passed to it
        TrajectoryActionBuilder tab1 = drive.actionBuilder(initialPose)
                .setTangent(Math.toRadians(90))
                .splineToConstantHeading(new Vector2d(9, -35), Math.toRadians(90));

        //.waitSeconds(3);
        TrajectoryActionBuilder tab2 = drive.actionBuilder(initialPose)
                //.setTangent(Math.toRadians(225))
                .strafeToConstantHeading(new Vector2d(-35, -61))
                .waitSeconds(2);

        waitForStart();

        if (isStopRequested()) return;

        Action trajectory_1;


        trajectory_1 = tab1.build();



        Actions.runBlocking(
                new SequentialAction(
                                trajectory_1
                )


        );
    }
}