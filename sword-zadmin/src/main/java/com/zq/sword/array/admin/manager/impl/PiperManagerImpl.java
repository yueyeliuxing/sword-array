package com.zq.sword.array.admin.manager.impl;

import com.zq.sword.array.admin.manager.PiperManager;
import com.zq.sword.array.client.PiperClient;
import com.zq.sword.array.rpc.api.namer.dto.NamePiper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: sword-array
 * @description:
 * @author: zhouqi1
 * @create: 2019-06-11 15:43
 **/
@Component
public class PiperManagerImpl implements PiperManager {

    @Resource
    private PiperClient piperClient;

    @Override
    public List<NamePiper> allPiper() {
        return piperClient.allPiper();
    }
}
