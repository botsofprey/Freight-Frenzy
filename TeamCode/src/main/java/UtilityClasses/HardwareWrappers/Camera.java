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
 * To use this class, first create a pipeline class in the subsystems directory.
 * This pipeline will be what processes all of the camera data and will be
 * what the main program queries for information from the camera.
 * Initialize the pipeline first and pass it as an argument to the camera during initialization.
 * Call stop on the camera immediately after you have used the camera for the last time.
 * The camera will take several seconds to close, but it will close asynchronously
 * so it will not slow down the main code execution.
 * If your op mode ends while the camera is active or while it is still closing,
 * the app will force a crash to kill the program and will then restart.
 * Restarting the app can take upwards of 30 seconds which
 * can severely decrease the number of points scored in tele-op.
 * In some cases, the app can also fail to reconnect to the robot once it restarts,
 * preventing you from scoring any points in tele-op or endgame.
 * Moral of the story: close the camera once you're done using it or bad things happen.
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
		if (!isStopped) {
			camera.closeCameraDeviceAsync(()->{});
			open = false;
			isStopped = true;
		}
	}
}
