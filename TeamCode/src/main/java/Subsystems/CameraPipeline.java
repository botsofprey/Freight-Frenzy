package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.opencv.core.Mat;
import org.opencv.objdetect.QRCodeDetector;
import org.openftc.easyopencv.OpenCvPipeline;

public class CameraPipeline extends OpenCvPipeline {
	private QRCodeDetector detector = new QRCodeDetector();
	private Mat points = new Mat();

	private LinearOpMode mode;
	private int count = 0;

	private String qrCodeString = "";
	private String pointsString = "";

	public CameraPipeline(LinearOpMode m) {
		mode = m;
	}

	@Override
	public Mat processFrame(Mat input) {
		mode.telemetry.addData("Pipeline", "Running " + count++);
		mode.telemetry.update();
		String ret = detector.detectAndDecode(input);
		mode.telemetry.addData("Pipeline", "Detected");
		mode.telemetry.addData("Pipeline", ret);
		mode.telemetry.update();

		if (!ret.equals("")) {
			mode.sleep(3000);
		}

		return input;
	}

	public String getQrCodeString() { return qrCodeString; }
	public String getPointsString() { return pointsString; }
}
