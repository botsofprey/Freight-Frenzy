package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.JSONReader;

public class OdometryWheel {
    private DcMotorEx odom;

    private double ticksPerRevolution;
    private double wheelCircumference;

    private long prevTicks = 0;
    private long direction = 1;

    public OdometryWheel(HardwareMap hw, MotorController encoder, String configFile) {
        odom = encoder.getMotor();

        initFromConfigFile(hw, configFile);
    }
    public OdometryWheel(HardwareMap hw, String motorName, String configFile) {
        odom = hw.get(DcMotorEx.class, motorName);

        initFromConfigFile(hw, configFile);
    }

    private void initFromConfigFile(HardwareMap hw, String configFile) {
        JSONReader reader = new JSONReader(hw, configFile);

        ticksPerRevolution = reader.getDouble("ticksPerRevolution");
        wheelCircumference = reader.getDouble("wheelDiameter") * Math.PI;

        prevTicks = getTick();
    }

    public long getTick() {
        return odom.getCurrentPosition() * direction;
    }
    public double getInch() {
        return getTick() * wheelCircumference / ticksPerRevolution;
    }

    public double getTickDiff() {
        long ticks = getTick();
        ticks -= prevTicks;
        prevTicks += ticks;
        return ticks;
    }
    public double getInchDiff() {
        return getTickDiff() * wheelCircumference / ticksPerRevolution;
    }
    
    public void setDirection(long direction) { this.direction = direction; }
    public long getDirection() { return direction; }
    public void setDirection(String direction) {
        setDirection(direction.equals("forward") ? 1 : -1);
    }
}
