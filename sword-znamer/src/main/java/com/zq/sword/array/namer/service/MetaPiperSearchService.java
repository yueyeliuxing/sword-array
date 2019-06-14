package com.zq.sword.array.namer.service;

import com.zq.sword.array.namer.piper.MetaPiperSupervisor;
import com.zq.sword.array.rpc.api.namer.PiperSearchService;
import com.zq.sword.array.rpc.api.namer.dto.NamePiper;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: piper查询
 * @author: zhouqi1
 * @create: 2019-06-14 15:57
 **/
public class MetaPiperSearchService implements PiperSearchService {

    /**
     * Piper管理器
     */
    private MetaPiperSupervisor metaPiperSupervisor;

    public MetaPiperSearchService(MetaPiperSupervisor metaPiperSupervisor) {
        this.metaPiperSupervisor = metaPiperSupervisor;
    }

    @Override
    public List<NamePiper> listPipers() {
        List<NamePiper> pipers = new ArrayList<>();
        pipers.addAll(metaPiperSupervisor.allNamePipers());
        return pipers;
    }
}
