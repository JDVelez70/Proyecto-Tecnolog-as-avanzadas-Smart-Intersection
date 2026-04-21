package model;

/**
 * Camera — OOP: Encapsula cámara de vigilancia de la intersección.
 */
public class Camera {

    private String cameraId;
    private String resolution;
    private boolean isRecording;

    public Camera(String cameraId, String resolution) {
        this.cameraId = cameraId;
        this.resolution = resolution;
        this.isRecording = false;
    }

    public String captureImage() {
        return "[IMAGEN capturada por " + cameraId + " @ " + resolution + "]";
    }

    public void startRecording() { this.isRecording = true; }
    public void stopRecording()  { this.isRecording = false; }

    public String getCameraId()   { return cameraId; }
    public String getResolution() { return resolution; }
    public boolean isRecording()  { return isRecording; }

    @Override
    public String toString() {
        return "Camera [id=" + cameraId + ", recording=" + isRecording + "]";
    }
}
