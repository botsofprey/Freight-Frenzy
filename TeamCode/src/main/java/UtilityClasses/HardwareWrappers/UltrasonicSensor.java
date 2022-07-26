package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.util.ElapsedTime;

public class UltrasonicSensor {
	private ElapsedTime runtime = new ElapsedTime();

	private byte[] range1Cache; //The read will return an array of bytes. They are stored in this variable

	private I2cAddr RANGE1ADDRESS = new I2cAddr(0x14); //Default I2C address for MR Range (7-bit)
	private static final int RANGE1_REG_START = 0x04; //Register to start reading
	private static final int RANGE1_READ_LENGTH = 2; //Number of byte to read

	private I2cDevice RANGE1;
	private I2cDeviceSynch RANGE1Reader;

	private boolean readUltra;
	private boolean readOptic;

	public UltrasonicSensor(HardwareMap hw, String name) {
		RANGE1 = hw.i2cDevice.get(name);
//		RANGE1Reader = new I2cDeviceSynchImpl(RANGE1, RANGE1ADDRESS, false);
		RANGE1Reader.engage();
		readUltra = true;
		readOptic = true;
	}

	public void refresh() {
		range1Cache = RANGE1Reader.read(RANGE1_REG_START, RANGE1_READ_LENGTH);
		readUltra = false;
		readOptic = false;
	}

	public int readUltrasonic() {
		if (readUltra) {
			refresh();
		}
		readUltra = true;
		return range1Cache[0] & 0xff;
	}

	public double readOptic() {
		if (readOptic) {
			refresh();
		}
		readOptic = true;
		return range1Cache[1] & 0xff;
	}
}
