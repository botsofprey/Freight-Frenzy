package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvPipeline;

public class Camera {

	private LinearOpMode mode;

	public Camera(HardwareMap hw, String name, OpenCvPipeline pipeline, LinearOpMode m) {
		m.telemetry.addData("Camera", "Initializing");
		m.telemetry.update();
		mode = m;
		
		WebcamName webcamName = hw.get(WebcamName.class, name);

		OpenCvCamera camera =
				OpenCvCameraFactory.getInstance().createWebcam(webcamName);

		mode.telemetry.addData("Camera", "Opening");
		mode.telemetry.update();
		camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
			@Override
			public void onOpened() {
				camera.startStreaming(1920, 1080);
				camera.setPipeline(pipeline);
			}

			@Override
			public void onError(int errorCode) {
				mode.telemetry.addData("Error", "Camera failed to open");
				mode.telemetry.update();
			}
		});
	}
}
