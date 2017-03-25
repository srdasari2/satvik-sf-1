package io.particle.hydroalert;

/**
 * Created by qz2zvk on 3/11/17.
 */

public class DeviceDetails {
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    private String deviceName;
    private boolean isConnected;
}
