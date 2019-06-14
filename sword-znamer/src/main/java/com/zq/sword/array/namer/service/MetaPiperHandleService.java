package com.zq.sword.array.namer.service;

import com.zq.sword.array.namer.job.MetaJobSupervisor;
import com.zq.sword.array.namer.piper.MetaPiperSupervisor;
import com.zq.sword.array.rpc.api.namer.PiperHandleService;
import com.zq.sword.array.rpc.api.namer.dto.JobCommand;
import com.zq.sword.array.rpc.api.namer.dto.NamePiper;

/**
 * @program: sword-array
 * @description: piper处理
 * @author: zhouqi1
 * @create: 2019-06-14 15:54
 **/
public class MetaPiperHandleService implements PiperHandleService {

    /**
     * Piper管理器
     */
    private MetaPiperSupervisor metaPiperSupervisor;

    /**
     * Job管理器
     */
    private MetaJobSupervisor metaJobSupervisor;

    public MetaPiperHandleService(MetaPiperSupervisor metaPiperSupervisor, MetaJobSupervisor metaJobSupervisor) {
        this.metaPiperSupervisor = metaPiperSupervisor;
        this.metaJobSupervisor = metaJobSupervisor;
    }

    @Override
    public void registerPiper(NamePiper namePiper) {
        metaPiperSupervisor.registerPiper(namePiper);
    }

    @Override
    public JobCommand requestJobCommand(NamePiper namePiper) {
        return metaJobSupervisor.getJobCommand(namePiper);
    }
}
