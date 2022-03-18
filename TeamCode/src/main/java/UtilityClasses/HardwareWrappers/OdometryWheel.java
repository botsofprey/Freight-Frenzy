package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.JSONReader;

public class OdometryWheel {
    private DcMotorEx odom;

    private double ticksPerRevolution;
    private double wheelCircumference;

    private long prevTicks = 0;

    public OdometryWheel(HardwareMap hw, MotorController encoder, String configFile) {
        odom = encoder.getMotor();

        initFromConfigFile(hw, configFile);
    }

    private void initFromConfigFile(HardwareMap hw, String configFile) {
        JSONReader reader = new JSONReader(hw, configFile);

        ticksPerRevolution = reader.getDouble("ticksPerRevolution");
        wheelCircumference = reader.getDouble("wheelDiameter") * Math.PI;
    }

    public long getTick() {
        return odom.getCurrentPosition();
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
}
