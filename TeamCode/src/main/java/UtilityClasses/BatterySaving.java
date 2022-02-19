package UtilityClasses;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class BatterySaving {
    private HardwareMap hardwareMap;
    private static final double low = 9, redBlinkTime = 7.0/4, blackBlinkTime = 1.0/4;
    private double blinkTime = redBlinkTime;
    private boolean batteryLow;

    private RevBlinkinLedDriver led;

    public BatterySaving(HardwareMap hm, RevBlinkinLedDriver l){
        hardwareMap = hm;
        led = l;
        l.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);

        batteryLow = checkBatterVoltage();
        batteryLow = true;
        millisPassed = System.currentTimeMillis();
    }
    public BatterySaving(HardwareMap hm){
        hardwareMap = hm;

        batteryLow = checkBatterVoltage();
        batteryLow = true;
        millisPassed = System.currentTimeMillis();
    }

    public boolean currentStatus(){
        return batteryLow;
    }

    public boolean checkBatterVoltage(){
        batteryLow = getBatteryVoltage() < low;
        return getBatteryVoltage() < low;
    }
    public boolean checkBatterVoltage(double battery){
        batteryLow = getBatteryVoltage() < battery;
        return getBatteryVoltage() < battery;
    }

    public double getBatteryVoltage() {
        double result = Double.POSITIVE_INFINITY;
        for (VoltageSensor sensor : hardwareMap.voltageSensor) {
            double voltage = sensor.getVoltage();
            if (voltage > 0)
                result = Math.min(result, voltage);
        }
        return result;
    }

    private double[] batteryAverage = new double[5];
    public double getAverage(){
        double total = 0;

        for (int i = 0; i < batteryAverage.length; i++)
            total += batteryAverage[i];

        return total/(double)batteryAverage.length;
    }

    public boolean areRed = true;

    double millisPassed;
    public void update(){
        if(areRed){
            if(System.currentTimeMillis() - millisPassed >= redBlinkTime * 1000){
                led.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);
                areRed = false;
                millisPassed = System.currentTimeMillis();
                System.out.println("Led is red: " + areRed);
            }
        }else{
            if(System.currentTimeMillis() - millisPassed >= blackBlinkTime * 1000){
                led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
                areRed = true;
                millisPassed = System.currentTimeMillis();
                System.out.println("Led is red: " + areRed);
            }
        }

        batteryLow = !checkBatterVoltage();
    }
}
