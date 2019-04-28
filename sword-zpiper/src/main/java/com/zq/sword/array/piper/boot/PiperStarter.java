package com.zq.sword.array.piper.boot;

import com.zq.sword.array.piper.Piper;
import com.zq.sword.array.piper.PiperFactory;
import com.zq.sword.array.piper.config.PiperConfig;
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

    @Override
    public void setEnvironment(Environment environment) {
        this.piper = PiperFactory.createPiper(new PiperConfig(environment));
    }

    @Override
    public void run(String... args) throws Exception {
        piper.start();
    }



}
