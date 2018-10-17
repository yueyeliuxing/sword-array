package com.zq.sword.array.data;

import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 默认数据
 * @author: zhouqi1
 * @create: 2018-10-17 15:04
 **/
@Data
@ToString
public class DefaultSwordData extends SwordData {

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
