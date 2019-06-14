package com.zq.sword.array.namer;

import com.zq.sword.array.namer.config.PiperConfig;
import com.zq.sword.array.namer.job.MetaJobSupervisor;
import com.zq.sword.array.namer.piper.MetaPiperSupervisor;
import com.zq.sword.array.namer.service.MetaJobHandleService;
import com.zq.sword.array.namer.service.MetaJobSearchService;
import com.zq.sword.array.namer.service.MetaPiperHandleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: sword-array
 * @description: piper
 * @author: zhouqi1
 * @create: 2019-01-23 15:50
 **/
public class DefaultNamer extends AbstractNamer implements Namer {

    private Logger logger = LoggerFactory.getLogger(DefaultNamer.class);

    /**
     * Piper管理器
     */
    private MetaPiperSupervisor metaPiperSupervisor;

    /**
     * Job管理器
     */
    private MetaJobSupervisor metaJobSupervisor;


    public DefaultNamer(PiperConfig config) {
        super(config.namerLocation());
        //创建piper管理器
        metaPiperSupervisor = new MetaPiperSupervisor();

        //创建Job管理器
        metaJobSupervisor = new MetaJobSupervisor(metaPiperSupervisor);

        //设置piper事件处理器
        registerService(new MetaJobHandleService(metaJobSupervisor));
        registerService(new MetaJobSearchService(metaJobSupervisor));
        registerService(new MetaPiperHandleService(metaPiperSupervisor, metaJobSupervisor));
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

}
