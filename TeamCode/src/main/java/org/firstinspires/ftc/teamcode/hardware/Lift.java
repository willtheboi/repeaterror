package org.firstinspires.ftc.teamcode.hardware;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.control.PIDCoefficients;
import com.acmerobotics.roadrunner.control.PIDFController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@Config
public class Lift extends Mechanism{
    public DcMotor liftLeft;
    public DcMotor liftRight;
    private boolean retract;
    public static double retMult;
    public static double HEIGHT_INCREMENT = 1.5;
    // lift positions
    public static double maxPos = 15;
    public static double midPos = 9;
    public static double carriageMarker = 3;
    public static double minPos = 0;

    public static PIDCoefficients coeffs = new PIDCoefficients(0.1, 0, 0);
    public static double kF = 0; //min power to go against g
    PIDFController controller;

    // lift constants
    public static double SPOOL_DIAMETER_IN = 1.81102;
    public static double MOTOR_RATIO = 5.2;
    public static double TICKS_PER_REV = 145.1;
    public static double GEAR_RATIO = 1.0;

    public static double[] positionHistory = new double[]{10};

    private double targetPosition = 0;

    private boolean formerBool = false;
    private boolean inAir = false;
    public void init(HardwareMap hardwareMap) {
        liftLeft = hardwareMap.get(DcMotor.class, "liftLeft");
        liftRight = hardwareMap.get(DcMotor.class, "liftRight");

        // if you need to make sure to reverse as necessary

//        liftLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        liftRight.setDirection(DcMotorSimple.Direction.REVERSE);

        //makes sure the motors don't move at zero power
        liftLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //reset encoders upon startup
        liftLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //for using RR PIDs
        liftLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //PID controller
        controller = new PIDFController(coeffs, 0, 0, kF);

        //damp
        retract = false;
        retMult = 0.2;
    }

    public void update() {
        updatePID(targetPosition);
        positionHistory[0] = getCurrentPosition();

        for (int i = 1; i < positionHistory.length - 1; i ++) {
            positionHistory[i] = positionHistory[i+1];
        }
    }

    public boolean estimatedEqual(double one, double two){
        return Math.abs(one - two) < 0.5;
    }

    public boolean still(){
        boolean still = true;

        for(int i = 0; i < positionHistory.length-1; i++){
            still = still && estimatedEqual(positionHistory[i],positionHistory[i+1]);
        }

        return still;
    }


    public void updatePID(double target) {
        if(target >= maxPos){target = maxPos;}
        if(target <= minPos){target = minPos;}
        controller.setTargetPosition(target);
        //find the error
        double leftPow = controller.update(encoderTicksToInches(liftLeft.getCurrentPosition()));
        double rightPow = controller.update(encoderTicksToInches(liftRight.getCurrentPosition()));
        //compensate error
        if(!retract) {
            liftLeft.setPower(leftPow);
            liftRight.setPower(rightPow);
        }else {
            liftLeft.setPower(leftPow * retMult);
            liftRight.setPower(rightPow * retMult);
        }
    }

    public double getTargetPosition(){ return targetPosition;}

    public double getCurrentPosition(){
        return (
                  encoderTicksToInches(liftLeft.getCurrentPosition())
                + encoderTicksToInches(liftRight.getCurrentPosition())
        ) / 2.0;
    }
    public String getStageLevel(){
        if (getCurrentPosition() >= maxPos - 1) return "maxPos";
        else if(getCurrentPosition() >= midPos -1) return "midPos";
        else if (getCurrentPosition() >= carriageMarker) return "carriageMarker";
        else if (getCurrentPosition() >= minPos) return "grounded";
        else return "IDK";
    }

    public String movementState(){
        int diff = (int)(getCurrentPosition()*10) - (int)(getTargetPosition()*10);
        int marginOffError = 7;

        if(diff > marginOffError) return "RETRACT";
        else if(diff < -marginOffError) return "EXTEND";
        else return "STILL";

    }

    public void setTargetPosition(double target){targetPosition = target;}

    public void raise(){
        setTargetPosition(midPos);
        inAir = true;
    }

    public void lower(){
        setTargetPosition(minPos);
        inAir = false;

    }

    public void toggle(){
        if(inAir){
            lower();
        }
        else{
            raise();
        }
    }

    public void toggleLiftHighGoal(){

    }


    public void keyPress(boolean bool){
        if(bool){
            formerBool = true;
        }
        if(formerBool){
            if(!bool){
                toggle();
                formerBool = false;
            }
        }
    }

    public boolean inAir(){return inAir;}

    public void extend(){
        retracting(false);
        if(this.getTargetPosition()+HEIGHT_INCREMENT >= maxPos){
            setTargetPosition(maxPos);
        } else {
            setTargetPosition(this.getTargetPosition()+HEIGHT_INCREMENT);
        }
    }
    public void retract(){
        retracting(true);
        if(this.getTargetPosition()-HEIGHT_INCREMENT <= minPos) {
            setTargetPosition(minPos);
        } else {
            setTargetPosition(this.getTargetPosition()-HEIGHT_INCREMENT);
        }
    }
    public static double encoderTicksToInches(double ticks) {
        return SPOOL_DIAMETER_IN  * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }
    public boolean getRetract() {
        return retract;
    }
    public void retracting(boolean status) {
        retract = status;
    }

}