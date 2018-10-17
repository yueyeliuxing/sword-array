package com.zq.sword.array.data.lqueue.bitcask;

import com.zq.sword.array.common.data.Sword;
import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 数据
 * @author: zhouqi1
 * @create: 2018-10-17 19:13
 **/
@Data
@ToString
public class OrderSwordData extends Sword {

    /**
     * 数据ID
     */
    private Long id;

    /**
     * 数据内容
     */
    private String value;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * crc校验值
     */
    private String crc;
}
