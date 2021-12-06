package Subsystems;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

public class CameraPipeline extends OpenCvPipeline {
	@Override
	public Mat processFrame(Mat input) {
		return input;
	}
}
