package com.zq.sword.array.config.client;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * @program: sword-array
 * @description: 配置 zk实现
 * @author: zhouqi1
 * @create: 2019-01-21 19:37
 **/
public class ZkArgsConfig extends AbstractArgsConfig implements ArgsConfig{

    private Logger logger = LoggerFactory.getLogger(ZkArgsConfig.class);

    private ZkClient client;

    public ZkArgsConfig(ZkClient client, ApplicationId id) {
        super(id);
        this.client = client;
    }

    public ZkArgsConfig(String connectAddr, ApplicationId id) {
        super(id);
        logger.info("ZkNameCoordinator module starting...");
        client = new ZkClient(new ZkConnection(connectAddr), 500, new ZkSerializer() {
            @Override
            public byte[] serialize(Object data) throws ZkMarshallingError {
                return (data.toString()).getBytes();
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes);
            }
        });
    }

    @Override
    protected void listenArgsConfigChange(ApplicationArgsConfigChangeHandler configChangeHandler) {
        String appConfigPath = ZkTreePathBuilder.buildNodeConfigPath(id);
        client.subscribeDataChanges(appConfigPath, new IZkDataListener(){

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                properties.clear();
                Properties properties = new Properties();
                loadProperties(data.toString(), properties);
                for(String key : properties.stringPropertyNames()){
                    Object value = properties.get(key);
                    configChangeHandler.handle(key, value);
                }
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {

            }
        });
    }

    @Override
    protected void pullConfig() {
        String appConfigPath = ZkTreePathBuilder.buildNodeConfigPath(id);
        String appConfigString = client.readData(appConfigPath);

        //加载参数
        loadProperties(appConfigString, properties);
    }

    /**
     * 参数加载
     * @param appConfigString
     * @param properties
     */
    private void loadProperties(String appConfigString, Properties properties) {
        try{
            properties.load(new ByteArrayInputStream(appConfigString.getBytes(Charset.defaultCharset())));
        }catch (Exception e){
            logger.error("加载服务配置文件转换为Properties出错", e);
        }
    }
}
