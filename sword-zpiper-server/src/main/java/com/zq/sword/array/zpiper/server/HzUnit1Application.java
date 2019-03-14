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
public class HzUnit1Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder(HzUnit1Application.class).profiles("hz-unit1").build(args).run(args);
        System.out.println("zpiper start success");
    }
}