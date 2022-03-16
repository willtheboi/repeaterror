package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.ScoringArm;

public class ARMFSM extends Mechanism{
    private ScoringArm arm = new ScoringArm();
    private ElapsedTime timer = new ElapsedTime();
    private FreightSensor sensor = new FreightSensor();
    private Telemetry telemetry;
    public enum states {
        down,
        up,
        dump,
        ready,
    };
    public states armStates;
    @Override
    public void init(HardwareMap hwMap) {
        arm.init(hwMap);
        sensor.init(hwMap);
        armStates = states.down;
    }
    public void init(HardwareMap hwMap, Telemetry tele) {
        arm.init(hwMap);
        sensor.init(hwMap);
        telemetry = tele;
    }
    public void loop() {
        switch(armStates) {
            case down:
                if(timer.milliseconds() >= 100) {
                    arm.goToStart();
                    arm.depositReset();
                    if (sensor.hasFreight()) {
                        timer.reset();
                        armStates = states.ready;
                    }
                }
                break;
            case up:
                arm.goToEnd();
                break;
            case dump:
                timer.reset();
                arm.dumpHard();
                armStates = states.down;
                break;
            case ready:
                if(timer.milliseconds() >= 200) {
                    arm.tuckPos();
                }
                break;
        }
    }
    public void ready() {
        armStates = states.up;
    }
    public void down() {
        armStates = states.down;
    }
    public void dump() {
        armStates = states.dump;
    }
}
