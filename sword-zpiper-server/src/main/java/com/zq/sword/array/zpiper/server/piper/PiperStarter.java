package com.zq.sword.array.zpiper.server.piper;

import com.zq.sword.array.config.client.ApplicationId;
import com.zq.sword.array.config.client.ArgsConfig;
import com.zq.sword.array.config.client.ZkArgsConfig;
import com.zq.sword.array.zpiper.server.piper.cluster.data.NamePiper;
import com.zq.sword.array.zpiper.server.piper.cluster.data.PiperType;
import com.zq.sword.array.zpiper.server.piper.config.PiperConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @program: sword-array
 * @description: piper启动器
 * @author: zhouqi1
 * @create: 2019-01-24 11:56
 **/
@Component
public class PiperStarter implements CommandLineRunner, EnvironmentAware{

    private Piper piper;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws Exception {
        ArgsConfig argsConfig = new ZkArgsConfig(environment.getProperty("piper"), new ApplicationId());
        PiperConfig piperConfig = new PiperConfig(environment, argsConfig);
        NamePiper namePiper = piperConfig.namePiper();
        PiperType type = namePiper.getType();
        switch (type){
            case DC_UNIT_PIPER:
                piper = new RedisPiper(piperConfig);
                break;
            case DC_UNIT_PROXY_PIPER:
                piper = new RedisProxyPiper(piperConfig);
                break;
                default:
                    break;

        }
        piper.start();
    }



}
