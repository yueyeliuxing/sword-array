package com.zq.sword.array.namer.piper.job;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @program: sword-array
 * @description: 任务监控系统
 * @author: zhouqi1
 * @create: 2019-04-28 17:41
 **/
public class MainJobSystem {

    /**
     * key->jobName value->MainJob
     */
    private Map<String, MainJob> jobs;


    /**
     * 得到所有的MainJob
     * @return
     */
    public Collection<MainJob> allMainJobs() {
        return Collections.unmodifiableCollection(jobs.values());
    }

    /**
     * 通过jobName得到MainJob
     * @param jobName
     * @return
     */
    public MainJob getMainJob(String jobName) {
        return jobs.get(jobName);
    }
}
