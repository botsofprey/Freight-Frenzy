package Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.objdetect.QRCodeDetector;
import org.openftc.easyopencv.OpenCvPipeline;

public class CameraPipelineRed extends OpenCvPipeline {
	private boolean init = true;

	private static final Point[] LOCATIONS = {
			new Point(1165, 500),
			new Point(450, 500)
	};

	private QRCodeDetector detector = new QRCodeDetector();
	private Mat points = new Mat();
	private Mat image = new Mat();

	private LinearOpMode mode;

	private volatile int location;
	public volatile int x;
	public volatile int y;
	public volatile String data;

	public volatile int xPos;

	public CameraPipelineRed(LinearOpMode m) {
		mode = m;
	}

	@Override
	public Mat processFrame(Mat input) {
		Core.rotate(input, image, Core.ROTATE_90_COUNTERCLOCKWISE);
		points = new Mat();
		data = detector.detectAndDecode(image, points);

		if (!points.empty()) {
			Point point = new Point(0, 0);
			for (int i = 0; i < points.cols(); i++) {
				Point pt = new Point(points.get(0, i));
				point.x += pt.x;
				point.y += pt.y;
			}
			x = (int)(point.x / 4.0);
			xPos = x;
			y = (int)(point.y / 4.0);

//			Imgproc.rectangle(image, new Point(x - 50, y - 50),
//					new Point(x + 50, y + 50), new Scalar(0, 0, 255), 3);

			double[] distances = new double[2];
			for (int i = 0; i < 2; i++) {
				distances[i] = Math.abs(x - LOCATIONS[i].x);
			}

			if (Math.abs(point.x - LOCATIONS[0].x) > Math.abs(point.x - LOCATIONS[1].x)) {
				location = 1;
			}
			else {
				location = 2;
			}
		}
		else {
			location = 0;
		}

		return image;
	}

	public int getShippingElementLocation() {
		return location;
	}
}
