package com.zq.sword.array.admin.boot;

import com.zq.sword.array.client.NamePiperClient;
import com.zq.sword.array.client.PiperClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @author wangbin
 */
@Configuration
@ImportResource(locations = "classpath:spring/*")
public class AppContext {

    @Resource
    private Environment environment;

    @Bean
    public PiperClient piperClient(){
        return new NamePiperClient(environment.getProperty("namer.location"));
    }
}
