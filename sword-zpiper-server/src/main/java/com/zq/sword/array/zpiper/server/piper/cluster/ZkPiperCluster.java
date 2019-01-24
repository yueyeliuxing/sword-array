package com.zq.sword.array.zpiper.server.piper.cluster;

import com.zq.sword.array.common.event.HotspotEvent;
import com.zq.sword.array.common.event.HotspotEventListener;
import com.zq.sword.array.zpiper.server.piper.cluster.data.NamePiper;
import com.zq.sword.array.zpiper.server.piper.cluster.data.PiperStartState;
import com.zq.sword.array.zpiper.server.piper.cluster.util.ZkClusterNodePathBuilder;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.zq.sword.array.common.event.HotspotEventType.PIPER_MASTER_NODE_DEL;

/**
 * @program: sword-array
 * @description: 主从服务协调器
 * @author: zhouqi1
 * @create: 2018-10-23 16:17
 **/
public class ZkPiperCluster implements PiperCluster {

    private Logger logger = LoggerFactory.getLogger(ZkPiperCluster.class);


    private ZkClient zkClient;

    public ZkPiperCluster(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    public ZkPiperCluster(String connectAddr) {
        logger.info("ZkPiperCluster module starting...");
        zkClient = new ZkClient(new ZkConnection(connectAddr), 500, new ZkSerializer() {
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
    public void setStartState(NamePiper piper, PiperStartState startState) {
        String masterStaterStatePath = ZkClusterNodePathBuilder.buildPiperStartStatePath(piper);
        if(!zkClient.exists(masterStaterStatePath)){
            zkClient.createPersistent(masterStaterStatePath);
        }
        zkClient.writeData(masterStaterStatePath, startState.name());
    }

    @Override
    public boolean register(NamePiper piper, HotspotEventListener<Void> eventListener) {
        String masterRunningPath = ZkClusterNodePathBuilder.buildPiperRunningPath(piper);
        String location = piper.getLocation();
        //master 节点已经存在
        if(zkClient.exists(masterRunningPath)){
            String oldValue = zkClient.readData(masterRunningPath);
            if(location.equals(oldValue)){
                try{
                    zkClient.delete(masterRunningPath);
                }catch (Exception e){
                    logger.error("删除节点失败", e);
                }
                zkClient.createEphemeral(masterRunningPath, location);
                return true;
            }
            zkClient.subscribeDataChanges(masterRunningPath, new IZkDataListener(){

                @Override
                public void handleDataChange(String dataPath, Object data) throws Exception {

                }

                @Override
                public void handleDataDeleted(String dataPath) throws Exception {
                    HotspotEvent<Void> event = new HotspotEvent<>();
                    event.setType(PIPER_MASTER_NODE_DEL);
                    eventListener.listen(event);
                }
            });
            return false;
        }
        zkClient.createEphemeral(masterRunningPath, location);
        logger.info("server 注册成功");
        return true;
    }
}
