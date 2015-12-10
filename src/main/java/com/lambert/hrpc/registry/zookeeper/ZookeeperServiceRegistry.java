package com.lambert.hrpc.registry.zookeeper;

import com.lambert.hrpc.conf.Constant;
import com.lambert.hrpc.registry.ServiceRegistry;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.ListUtils;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 基于ZooKeeper的服务发现
 */
public class ZookeeperServiceRegistry implements ServiceRegistry{

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

    private volatile Map<String , List<String>> services = new HashMap<>();

    private String registryAddress;

    private AtomicBoolean isInit = new AtomicBoolean(false);

    /**
     * 感兴趣的服务名
     */
    private List<String> focusServices ;

    public ZookeeperServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;

    }

    /**
     * 服务发现之前初始化
     */
    public void init(){
        if(!isInit.get()){
            synchronized (this) {
                ZkClient client = new ZkClient(registryAddress, Constant.ZK_SESSION_TIMEOUT);

                List<String> serviceNodes;
                // 获取感兴趣的服务地址 , 为空则获取全部服务的地址
                if (focusServices != null && focusServices.size() > 0) {
                    serviceNodes = focusServices;
                } else {
                    serviceNodes = client.getChildren(Constant.ZK_REGISTRY_PATH);
                }

                for (String serviceNode : serviceNodes) {
                    String node = String.format("%s/%s", Constant.ZK_REGISTRY_PATH, serviceNode);
                    getServiceNodeData(client, node, client.getChildren(node));
                }
                // 注册监听
                registerChildrenChangeListener(client);

                isInit.set(true);
            }
        }
    }

    @Override
    public void register(String serviceName,String address) {
        if (address != null) {
            ZkClient client = new ZkClient(registryAddress, Constant.ZK_SESSION_TIMEOUT);
            if (client != null) {
//                String path = client.createEphemeralSequential(String.format("%s/%s/%s",Constant.ZK_REGISTRY_PATH  , serviceName , address), address, ZooDefs.Ids.OPEN_ACL_UNSAFE);
                String nodePath = String.format("%s/%s/%s", Constant.ZK_REGISTRY_PATH, serviceName, address);
                if(!client.exists(nodePath)) {
                    client.createEphemeral(nodePath, address, ZooDefs.Ids.OPEN_ACL_UNSAFE);
                    LOGGER.info("create zookeeper node ({} => {})", nodePath, address);
                }
            }
        }
    }

    @Override
    public String lookup(String serviceName) {
        init();
        String data = null;
        List<String> addressList = services.get(serviceName);
        addressList = ListUtils.emptyIfNull(addressList);
        int size = addressList.size();
        if (size > 0) {
            if (size == 1) {
                data = addressList.get(0);
                LOGGER.debug("using only data: {}", data);
            } else {
                data = addressList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random data: {}", data);
            }
        }
        return data;
    }

    /**
     * 注册监听，监听服务地址的变化
     * @param client
     */
    private void registerChildrenChangeListener(final ZkClient client) {
        List<String> serviceNodes ;
        // 获取感兴趣的服务地址 , 为空则获取全部服务的地址
        if(focusServices != null && focusServices.size() > 0){
            serviceNodes = focusServices;
        }else {
            serviceNodes = client.getChildren(Constant.ZK_REGISTRY_PATH);
        }
        IZkChildListener listener = new IZkChildListener() {
            @Override
            public void handleChildChange(String node, List<String> children) throws Exception {
                getServiceNodeData(client,node,children);
            }
        } ;
        for(String serviceNode : serviceNodes){
            String node = String.format("%s/%s" , Constant.ZK_REGISTRY_PATH , serviceNode);
            client.subscribeChildChanges( node , listener);
            LOGGER.info("register listener to watch : {}" , node);
        }
    }

    /**
     *  获取指定节点下所有子节点的数据
     * @param client
     * @param node   /registry/com.lambert.service.EchoService
     * @param children  127.0.0.1:8000
     */
    private void getServiceNodeData(ZkClient client ,String node, List<String> children){
        List<String> AddressList = new ArrayList<String>();
        for(String child : children){
            String address = client.readData(String.format("%s/%s",node , child),true );
            if(address != null){
                AddressList.add(address);
            }
        }
        LOGGER.info("node data: {}", AddressList);
        ZookeeperServiceRegistry.this.services.put(splitServiceName(node),AddressList);
    }

    /**
     * 得到服务的名称
     * @param node  /registry/com.lambert.service.EchoService
     * @return   com.lambert.service.EchoService
     */
    private String splitServiceName (String node){
        String serviceName = null;
        String[] items = node.split("/");
        if(items.length > 0){
            serviceName = items[items.length - 1];
        }
        return serviceName;
    }

}