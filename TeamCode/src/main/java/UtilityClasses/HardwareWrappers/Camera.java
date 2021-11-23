package UtilityClasses.HardwareWrappers;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

public class Camera {

	private LinearOpMode mode;

	public Camera(HardwareMap hw, String name, LinearOpMode m) {//todo finish this
		mode = m;

		try {
			int cameraId = hw.appContext.getResources().getIdentifier(
					"cameraMonitorViewId", "id", hw.appContext.getPackageName()
			);

			WebcamName webcamName = hw.get(WebcamName.class, name);

			OpenCvCamera camera = OpenCvCameraFactory.getInstance().createWebcam(webcamName, cameraId);

			camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
				@Override
				public void onOpened() {
					// Usually this is where you'll want to start streaming from the camera (see section 4)
				}

				@Override
				public void onError(int errorCode) {
					/*
					 * This will be called if the camera could not be opened
					 */
				}
			});

			camera.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
		}
		catch (Exception e) {
			e.printStackTrace();
			mode.telemetry.addData("Camera error", e.toString());
		}
	}
}
