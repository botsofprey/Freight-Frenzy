package UtilityClasses;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

public class BatteryVoltage {
    private HardwareMap hardwareMap;
    private double low = 9;

    public BatteryVoltage(HardwareMap hm){
        hardwareMap = hm;
    }

    public boolean checkBatterVoltage(){
        return getBatteryVoltage() < low;
    }
    public boolean checkBatterVoltage(double battery){
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
}
