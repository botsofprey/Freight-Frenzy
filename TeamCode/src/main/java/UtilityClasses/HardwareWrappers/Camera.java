package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvPipeline;

/**
 * This class will initialize a camera from the robot and
 * binds an OpenCvPipeline instance, which actually does the image processing.
 * To use this class, first create a pipeline class in
 *
 * @author Alex Prichard
 */
public class Camera {
	private OpenCvCamera camera;

	private LinearOpMode mode;
	private boolean open = false;
	public volatile boolean isStopped = false;

	public Camera(HardwareMap hw, String name, OpenCvPipeline pipeline, LinearOpMode m) {
		mode = m;
		
		WebcamName webcamName = hw.get(WebcamName.class, name);

		camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName);

		mode.telemetry.addData("Camera", "Opening");
		mode.telemetry.update();
		camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
			@Override
			public void onOpened() {
				camera.startStreaming(1920, 1080);
				camera.setPipeline(pipeline);
				mode.telemetry.addData("Status", "Camera running");
				mode.telemetry.update();
				open = true;
			}

			@Override
			public void onError(int errorCode) {
				mode.telemetry.addData("Error", "Camera failed to open");
				mode.telemetry.update();
			}
		});
	}

	public boolean isOpen() {
		return open;
	}

	public void stop() {
		camera.closeCameraDeviceAsync(()->{});
		isStopped = true;
	}
}
