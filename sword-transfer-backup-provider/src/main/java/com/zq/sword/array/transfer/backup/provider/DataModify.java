package com.zq.sword.array.transfer.backup.provider;

import com.zq.sword.array.data.SwordData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: sword-array
 * @description: 数据变更
 * @author: zhouqi1
 * @create: 2018-12-10 16:33
 **/
public class DataModify {

    /**
     * T-Right 队列新增的数据
     */
    private List<SwordData> rightAddDatas;

    /**
     * T-Left 队列新增的数据
     */
    private List<SwordData> leftAddDatas;

    /**
     * T-left队列删除的数据
     */
    private List<SwordData> leftDelDatas;

    public DataModify() {
        rightAddDatas = new ArrayList<>();
        leftAddDatas = new ArrayList<>();
        leftDelDatas = new ArrayList<>();
    }

    public void addRightAddData(SwordData swordData){
        synchronized (rightAddDatas){
            rightAddDatas.add(swordData);
        }

    }

    public void addLeftAddData(SwordData swordData){
        synchronized(leftAddDatas){
            leftAddDatas.add(swordData);
        }
    }
    public void addLeftDelData(SwordData swordData){
        synchronized(leftDelDatas){
            leftDelDatas.add(swordData);
        }
    }

    public List<SwordData> removeAllRightAddDatas(){
        synchronized(rightAddDatas){
            List<SwordData> rightAddDatas = this.rightAddDatas;
            this.rightAddDatas.clear();
            return rightAddDatas;
        }
    }

    public List<SwordData> removeAllLeftAddDatas(){
        synchronized(leftAddDatas){
            List<SwordData> leftAddDatas = this.leftAddDatas;
            this.leftAddDatas.clear();
            return leftAddDatas;
        }
    }

    public List<SwordData> removeAllLeftDelDatas(){
        synchronized(leftDelDatas){
            List<SwordData> leftDelDatas = this.leftDelDatas;
            this.leftDelDatas.clear();
            return leftDelDatas;
        }
    }


}
