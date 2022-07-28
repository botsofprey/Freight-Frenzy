package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import UtilityClasses.JSONReader;

/**
 * This class allows for all input functionality of the encoders associated with a motor,
 * but does not allow any outputting to the motor.
 * This is intended to be used with dead wheels on the bottom of the robot.
 * It distances the encoders from the motors they are plugged into the ports of,
 * because there is no meaningful connection between them, and it prevents
 * accidentally setting the corresponding motor's power.
 * Feel free to add functionality to this class as needed.
 *
 * @author Alex Prichard
 */
public class OdometryWheel {
    private DcMotorEx odom;

    private double ticksPerRevolution;
    private double wheelCircumference;

    private long prevTicks = 0;
    private long direction = 1;

    public OdometryWheel(HardwareMap hw, MotorController encoder, String configFile) {
        odom = encoder.getMotor();

        initFromConfigFile(hw, configFile);

        zeroEncoder();
    }
    public OdometryWheel(HardwareMap hw, String motorName, String configFile) {
        odom = hw.get(DcMotorEx.class, motorName);

        initFromConfigFile(hw, configFile);

        zeroEncoder();
    }

    private void initFromConfigFile(HardwareMap hw, String configFile) {
        JSONReader reader = new JSONReader(hw, configFile);

        ticksPerRevolution = reader.getDouble("ticksPerRevolution");
        wheelCircumference = reader.getDouble("wheelDiameter") * Math.PI;

        prevTicks = getTick();
    }

    private void zeroEncoder() {
        odom.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        odom.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
