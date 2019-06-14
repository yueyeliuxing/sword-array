package com.zq.sword.array.piper.config;

import com.zq.sword.array.rpc.api.namer.dto.NamePiper;
import com.zq.sword.array.utils.IPUtil;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * @program: sword-array
 * @description: piper配置参数
 * @author: zhouqi1
 * @create: 2019-01-23 16:03
 **/
public class PiperConfig {

    private Environment environment;

    public PiperConfig(Environment environment) {
        this.environment = environment;
    }

    public String dataStorePath() {
        return getParam(PiperConfigKey.DATA_STORE_PATH);
    }

    /**
     * piperID 生成
     * @return
     */
    public long piperId() {
        return Math.abs(Objects.hashCode(piperLocation()));
    }

    /**
     * 得到piper地址
     * @return
     */
    public String piperLocation() {
        return IPUtil.getServerIp()+":"+ getParam(PiperConfigKey.PIPER_BIND_PORT);
    }

    private String getParam(String key) {
        return environment.getProperty(key);
    }

    private <T> T getParam(String key, Class<T> valueClazz) {
        return environment.getProperty(key, valueClazz);
    }

    /**
     * piper name
     * @return
     */
    public NamePiper namePiper() {
        return new NamePiper(piperId(),
                getParam(PiperConfigKey.PIPER_GROUP),
                piperLocation());
    }

    /**
     * namer地址
     * @return
     */
    public String namerLocation() {
        return getParam(PiperConfigKey.PIPER_NAMER_LOCATION);
    }
}
