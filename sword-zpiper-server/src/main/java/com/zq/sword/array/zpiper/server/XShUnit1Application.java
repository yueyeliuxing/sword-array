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
public class XShUnit1Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder(XShUnit1Application.class).profiles("x-sh-unit1").build(args).run(args);
        System.out.println("zpiper start success");
    }
}
