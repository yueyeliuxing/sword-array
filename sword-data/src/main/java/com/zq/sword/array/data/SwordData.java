package com.zq.sword.array.data;

import lombok.Data;
import lombok.ToString;

import java.util.Objects;

/**
 * @program: sword-array
 * @description: 默认数据
 * @author: zhouqi1
 * @create: 2018-10-17 15:04
 **/
@Data
@ToString
public class SwordData extends Sword {

    /**
     * 数据ID
     */
    private Long id;

    /**
     * 数据内容
     */
    private SwordCommand value;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * crc校验值
     */
    private String crc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SwordData swordData = (SwordData) o;
        return Objects.equals(id, swordData.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id);
    }
}
