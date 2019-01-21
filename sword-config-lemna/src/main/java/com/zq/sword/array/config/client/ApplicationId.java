package com.zq.sword.array.config.client;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 节点服务ID
 * @author: zhouqi1
 * @create: 2018-07-23 16:50
 **/
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class ApplicationId {

    /**
     * 机房名称
     */
    private String dc;

    /**
     * 单元类别名称
     */
    private String unitCategory;

    /**
     * 单元名称
     */
    private String unit;

    /**
     * piper 组名称
     */
    private String group;

    public ApplicationId(String dc, String unitCategory, String unit, String group) {
        this.dc = dc;
        this.unitCategory = unitCategory;
        this.unit = unit;
        this.group = group;
    }
}
