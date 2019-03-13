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
public class Application1 {
    public static void main(String[] args) {
        new SpringApplicationBuilder(Application1.class).profiles("dwd-dev").build(args).run(args);
        System.out.println("zpiper start success");
    }
}
