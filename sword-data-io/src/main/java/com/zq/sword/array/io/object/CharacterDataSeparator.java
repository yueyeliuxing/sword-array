package com.zq.sword.array.io.object;

/**
 * @program: sword-array
 * @description: 字符分割器
 * @author: zhouqi1
 * @create: 2018-10-30 12:01
 **/
public class CharacterDataSeparator implements DataSeparator {

    private String character;

    public CharacterDataSeparator() {
        character = System.getProperty("line.separator");
    }

    public CharacterDataSeparator(String character) {
        this.character = character;
    }

    @Override
    public String character() {
        return character;
    }

    @Override
    public int isBoundary(byte[] data) {
        int index = 0;
        if((index =new String(data).indexOf(character)) > -1){
            return index;
        }
        return index;
    }

    @Override
    public byte[] toDataArray(byte[] data) {
        int index = isBoundary(data);
        if(index > -1){
            byte[] item = new byte[index];
            for(int i = 0; i < index; i++){
                item[i] = data[i];
            }
            return item;
        }
        return new byte[0];
    }


}
