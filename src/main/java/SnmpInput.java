public class SnmpInput {
    private String ip;
    private String status;

    public String getSnmpCommunity() {
        return snmpCommunity;
    }

    public void setSnmpCommunity(String snmpCommunity) {
        this.snmpCommunity = snmpCommunity;
    }

    private String snmpCommunity;

    private String deviceCode;

    public SnmpInput(String deviceCode, String ip,String snmpCommunity,  String status) {
        this.ip = ip;
        this.status = status;
        this.deviceCode = deviceCode;
        this.snmpCommunity = snmpCommunity;
    }
    public SnmpInput(String deviceCode, String ip, String community) {
        this.ip = ip;
//        this.status = status;
        this.deviceCode = deviceCode;
        this.snmpCommunity = community;
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
