package com.zq.sword.array.namer.boot;

import com.zq.sword.array.namer.Namer;
import com.zq.sword.array.namer.NamerFactory;
import com.zq.sword.array.namer.config.PiperConfig;
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
public class NamerStarter implements CommandLineRunner, EnvironmentAware{

    private Namer piper;

    @Override
    public void setEnvironment(Environment environment) {
        this.piper = NamerFactory.createPiper(new PiperConfig(environment));
    }

    @Override
    public void run(String... args) throws Exception {
        piper.start();
    }



}
