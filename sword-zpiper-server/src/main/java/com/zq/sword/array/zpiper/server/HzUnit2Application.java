package com.zq.sword.array.zpiper.server;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 *
 * @author zhouqi
 */
@EnableAutoConfiguration
@SpringBootApplication
public class HzUnit2Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder(HzUnit2Application.class).profiles("hz-unit2").build(args).run(args);
        System.out.println("zpiper start success");
    }
}
