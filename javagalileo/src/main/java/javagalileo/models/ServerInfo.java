package javagalileo.models;

public class ServerInfo {

    public enum GALILEO_RETURN_CODE {
        OK, NOT_CONNECTED, INVALIDE_STATE, NO_SERVER_FOUND, MULTI_SERVER_FOUND, NETWORK_ERROR, ALREADY_CONNECTED,
        TIMEOUT, SERVER_ERROR, GOAL_CANCELLED, INVALIDE_GOAL, INVALIDE_PARAMS
    };

    private String ID;
    private Integer port;
    private Long timestamp;
    private String ip = "";
    private String password = "xiaoqiang";
    private String mac;

    /**
     * @return the iD
     */
    public String getID() {
        return ID;
    }

    /**
     * @return the mac
     */
    public String getMac() {
        return mac;
    }

    /**
     * @param mac the mac to set
     */
    public void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @param iD the iD to set
     */
    public void setID(String iD) {
        this.ID = iD;
    }
}
