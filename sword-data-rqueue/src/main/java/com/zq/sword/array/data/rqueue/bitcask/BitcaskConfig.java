package com.zq.sword.array.data.rqueue.bitcask;

import com.zq.sword.array.common.data.SwordDataDeserializer;
import com.zq.sword.array.common.data.SwordDataSerializer;
import com.zq.sword.array.common.data.SwordDeserializer;
import com.zq.sword.array.common.data.SwordSerializer;
import lombok.Data;
import lombok.ToString;

/**
 * @program: sword-array
 * @description: 配置
 * @author: zhouqi1
 * @create: 2018-10-17 15:25
 **/
@Data
@ToString
public class BitcaskConfig {

    private BitcaskConfig(){}

    public static class BitcaskConfigBuilder {

        private BitcaskConfig bitcaskConfig;

        private BitcaskConfigBuilder(){
            bitcaskConfig = new BitcaskConfig();
        }

        public static BitcaskConfigBuilder create(){
            return new BitcaskConfigBuilder();
        }

        public BitcaskConfigBuilder setDataFilePath(String dataFilePath){
            bitcaskConfig.setDataFilePath(dataFilePath);
            return this;
        }

        public BitcaskConfigBuilder setIndexFilePath(String indexFilePath){
            bitcaskConfig.setIndexFilePath(indexFilePath);
            return this;
        }

        public BitcaskConfigBuilder defaultSwordDataSerializer(){
            bitcaskConfig.setSwordDataSerializer(new SwordDataSerializer());
            return this;
        }


        public BitcaskConfigBuilder setSwordDataSerializer(SwordSerializer swordDataSerializer){
            bitcaskConfig.setSwordDataSerializer(swordDataSerializer);
            return this;
        }

        public BitcaskConfigBuilder defaultSwordDataDeserializer(){
            bitcaskConfig.setSwordDataDeserializer(new SwordDataDeserializer());
            return this;
        }

        public BitcaskConfigBuilder setSwordDataDeserializer(SwordDeserializer swordDataDeserializer){
            bitcaskConfig.setSwordDataDeserializer(swordDataDeserializer);
            return this;
        }

        public BitcaskConfig build(){
            return bitcaskConfig;
        }
    }

    /**
     * 数据文件地址
     */
    private String dataFilePath;

    /**
     * 索引文件的地址
     */
    private String indexFilePath;

    /**
     * 数据反序列化
     */
    private SwordDeserializer swordDataDeserializer;

    /**
     * 数据序列化
     */
    private SwordSerializer swordDataSerializer;
}
