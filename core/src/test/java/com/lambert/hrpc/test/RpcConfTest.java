package com.lambert.hrpc.test;

import com.lambert.hrpc.core.RpcConf;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by pc on 2015/12/15.
 */
public class RpcConfTest {

    @Test
    public void test(){

        RpcConf conf = new RpcConf();
        System.out.println(conf.getZookeeperRegistryPath());
        Assert.assertEquals(conf.getZookeeperRegistryPath() , "/registry");

    }

}
