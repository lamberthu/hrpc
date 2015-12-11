package com.lambert.hrpc;

import com.lambert.hrpc.utils.NetUtils;

import java.util.List;

/**
 *
 * Created by pc on 2015/12/11.
 */
public class RpcConf {

    private String zookeeperRegistryPath = "/registry" ;
    private String zookeeperAddress = "192.168.1.197:2181";
    private int zookeeperTimeout = 5000;

    private List<String> focusServices;

    private String servicePort = "8000"; // 服务绑定的端口

    private String serviceAddress;    // 服务绑定的地址

    public RpcConf(){
        // TODO load config file;

        String localIp = NetUtils.getMachineIp();
        this.serviceAddress = String.format("%s:%s", localIp, servicePort);
    }

    public String getZookeeperRegistryPath() {
        return zookeeperRegistryPath;
    }

    public void setZookeeperRegistryPath(String zookeeperRegistryPath) {
        this.zookeeperRegistryPath = zookeeperRegistryPath;
    }

    public String getZookeeperAddress() {
        return zookeeperAddress;
    }

    public void setZookeeperAddress(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    public int getZookeeperTimeout() {
        return zookeeperTimeout;
    }

    public void setZookeeperTimeout(int zookeeperTimeout) {
        this.zookeeperTimeout = zookeeperTimeout;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
