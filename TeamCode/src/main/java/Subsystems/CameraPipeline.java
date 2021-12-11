package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;
import org.openftc.easyopencv.OpenCvPipeline;

public class CameraPipeline extends OpenCvPipeline {
	private static final Point[] LOCATIONS = {
			new Point(500, 500),
			new Point(1000, 500),
			new Point(1500, 500)
	};

	private QRCodeDetector detector = new QRCodeDetector();
	private Mat points = new Mat();
	private Mat image = new Mat();

	private LinearOpMode mode;

	private int location;

	public CameraPipeline(LinearOpMode m) {
		mode = m;
	}

	@Override
	public Mat processFrame(Mat input) {
		image = input;
		String data = detector.detectAndDecode(image, points);

		if (!points.empty()) {
			mode.telemetry.addData("Decoded data", data);

			Point point = new Point(0, 0);
			for (int i = 0; i < points.cols(); i++) {
				Point pt = new Point(points.get(0, i));
				point.x += pt.x;
				point.y += pt.y;
			}
			double x = point.x / 4.0;
			double y = point.y / 4.0;

			mode.telemetry.addData("Location", "x: " + x + " y: " + y);

			double[] distances = new double[3];
			for (int i = 0; i < 3; i++) {
				distances[i] = Math.abs(x - LOCATIONS[i].x);
			}

			int index = 0;
			double min = 10000;
			for (int i = 1; i < 3; i++){
				if (distances[i] <= min){
					min = distances[i];
					index = i;
				}
			}
			location = index + 1;
		}
		mode.telemetry.update();

		return image;
	}

	public int getShippingElementLocation() {
		return location;
	}
}
