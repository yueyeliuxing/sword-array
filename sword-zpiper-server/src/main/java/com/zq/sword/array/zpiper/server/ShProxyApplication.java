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
public class ShProxyApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ShProxyApplication.class).profiles("sh-proxy").build(args).run(args);
        System.out.println("zpiper start success");
    }
}
