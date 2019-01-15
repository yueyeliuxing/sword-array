package com.zq.sword.array.data.rqueue.bitcask;

import com.zq.sword.array.data.Sword;
import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 数据 索引
 * @author: zhouqi1
 * @create: 2018-07-25 20:47
 **/
@Data
@ToString
public class SwordIndex extends Sword {

    /**
     * 数据ID
     */
    private Long dataId;

    /**
     * 文件的ID
     */
    private String fileId;

    /**
     * 数据内容的长度
     */
    private Long valueLength;

    /**
     * 数据内容开始位置
     */
    private Long valuePosition;

    /**
     * 时间戳
     */
    private Long timestamp;

}
