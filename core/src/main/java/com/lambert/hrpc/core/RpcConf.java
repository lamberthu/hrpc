package com.lambert.hrpc.core;


import com.lambert.hrpc.core.utils.NetUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.rmi.PortableRemoteObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Exchanger;

/**
 * Created by pc on 2015/12/11.
 */
public class RpcConf {

    private static Logger LOGGER = LoggerFactory.getLogger(RpcConf.class);

    private String defaultPropertyFile = "hrpc-default.properties";
    private String userProprtyFile = "hrpc.properties";

    private String zookeeperRegistryPath ;
    private String zookeeperAddress ;
    private int zookeeperTimeout ;

    private List<String> focusServices;

    private String servicePort ; // 服务绑定的端口

    private String serviceAddress;    // 服务绑定的地址

    private static Map<Object, Object> PRPPERTIES = new HashMap<>();

    private static RpcConf INSTANCE = new RpcConf();

    private RpcConf() {

        loadFromFile();

        String localIp = NetUtils.getMachineIp();
        this.serviceAddress = String.format("%s:%s", localIp, servicePort);
    }

    /**
     * 加载配置文件
     */
    private void loadFromFile() {
        InputStream defaultInput = null;
        InputStream userInput = null;
        try {
            // load default properties
            try {
                defaultInput = RpcConf.class.getClassLoader().getResourceAsStream(defaultPropertyFile);
            } catch (Exception e) {
                LOGGER.error("don't find default hrpc configuration file : {}", defaultInput);
            }
            Properties defaultProp = null;
            if (defaultInput != null) {
                defaultProp = new Properties();
                defaultProp.load(defaultInput);
            }
            // load user properties
            try {
                userInput = RpcConf.class.getClassLoader().getResourceAsStream(userProprtyFile);
            } catch (Exception e) {

            }
            Properties userProp = null;

            if (defaultProp != null) {
                userProp = new Properties(defaultProp);
            } else {
                userProp = new Properties();
            }
            if (userInput != null) {
                userProp.load(userInput);
            }

            // 赋值
            this.zookeeperRegistryPath = userProp.getProperty("hrpc.zookeepr.registry.path", "");
            this.zookeeperAddress = userProp.getProperty("hrpc.zookeeper.address", "");
            this.zookeeperTimeout = Integer.parseInt(userProp.getProperty("hrpc.zookeeper.connection.timeou", "5000"));
            this.servicePort = userProp.getProperty("hrpc.service.bind.port", "");

            String fouseServiceString = userProp.getProperty("hrpc.fouse.services");
            if (fouseServiceString != null && !"".equals(fouseServiceString)) {
                String[] items = fouseServiceString.split(",");
                focusServices = Arrays.asList(items);
            }
            PRPPERTIES.putAll(userProp);

        } catch (Exception e) {
            LOGGER.error("load configuration file : ", e);
        } finally {
            if (defaultInput != null) {
                try {
                    defaultInput.close();
                } catch (IOException e) {

                }
            }
            if (userInput != null) {
                try {
                    userInput.close();
                } catch (IOException e) {

                }
            }
        }


    }

    public static RpcConf getINSTANCE() {
        return INSTANCE;
    }

    public String getString(String key){
        Object value = PRPPERTIES.get(key);
        if(value == null){
            return null;
        }
        return value.toString();
    }
    public Integer getInt(String key){
        Object value = PRPPERTIES.get(key);
        if(value == null){
            return null;
        }
        try {
            Integer result = Integer.parseInt(value.toString());
            return result;
        }catch (Exception e){

        }
        return null;
    }

    public void set(Object key , Object value) {
        PRPPERTIES.put(key, value);
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
