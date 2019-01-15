package com.zq.sword.array.data.queue;

import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.data.SwordData;
import com.zq.sword.array.data.SwordDataDeserializer;
import com.zq.sword.array.data.SwordDataSerializer;
import com.zq.sword.array.io.file.FileSystemResourceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Queue;


public class LeftOrderlyQueueTest {

    private DataQueue<SwordData> leftOrderlyQueue;

    @Before
    public void setUp() throws Exception {
        leftOrderlyQueue = new StoredWrapDataQueue(new FileSystemResourceStore("E:\\sword\\left\\data\\sequence.data"), new SwordDataSerializer(), new SwordDataDeserializer());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getLastDataId() {
        Queue queue = null;
    }

    @Test
    public void push() throws IOException {
        for(int i = 1; i< 10; i++){
            SwordData orderSwordData = new SwordData();
            orderSwordData.setId(Long.valueOf(i));
            SwordCommand swordCommand = new SwordCommand();
            swordCommand.setType((byte)1);
            swordCommand.setKey(("user_"+i).getBytes());
            swordCommand.setValue(("7852sff_"+i).getBytes());
            orderSwordData.setValue(swordCommand);
            orderSwordData.setTimestamp(System.currentTimeMillis());
            orderSwordData.setCrc("11212");
            boolean isSucess = false;
            while (!isSucess){
                isSucess = leftOrderlyQueue.offer(orderSwordData);
                if(isSucess){
                    System.out.println(orderSwordData);
                }
            }

            /*SwordData swordData = null;
            while (swordData == null){
                swordData = leftOrderlyQueue.poll();
            }

            System.out.println(swordData);*/
        }

        System.in.read();

    }

    @Test
    public void poll() throws IOException {
        SwordData swordData = null;
        do{
            swordData = leftOrderlyQueue.poll();
            if(swordData != null){
                System.out.println(swordData);
            }
        }while (swordData != null);

        System.in.read();

    }

    @Test
    public void containsConsumed() {
    }

    @Test
    public void pollAfterId() {
    }

    @Test
    public void pollAfterId1() {
    }
}