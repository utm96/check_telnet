public class TelnetResult {
    private String ip;
    private String status;
    private String deviceCode;

    public TelnetResult(String deviceCode, String ip, String status) {
        this.ip = ip;
        this.status = status;
        this.deviceCode = deviceCode;
    }
    public TelnetResult(String deviceCode, String ip) {
        this.ip = ip;
//        this.status = status;
        this.deviceCode = deviceCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeviceCode() {
        return deviceCode;
    }
}
