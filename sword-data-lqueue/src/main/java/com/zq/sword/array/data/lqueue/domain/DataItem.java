package com.zq.sword.array.data.lqueue.domain;

import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 数据项
 * @author: zhouqi1
 * @create: 2018-07-23 17:43
 **/
@Data
@ToString
public class DataItem {

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
