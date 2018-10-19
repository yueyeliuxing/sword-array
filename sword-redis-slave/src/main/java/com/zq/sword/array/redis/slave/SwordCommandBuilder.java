package com.zq.sword.array.redis.slave;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.zq.sword.array.common.data.SwordCommand;

/**
 * @program: sword-array
 * @description: 命令解析器
 * @author: zhouqi1
 * @create: 2018-10-18 20:39
 **/
public class SwordCommandBuilder {

    /**
     * 解析redis命令
     * @param command
     * @return
     */
    public static SwordCommand buildSwordCommand(Command command){
        SwordCommand swordCommand = new SwordCommand();
        if(command instanceof SetCommand){
            SetCommand setCommand = (SetCommand) command;
            swordCommand.setType((byte)1);
            swordCommand.setKey(new String((setCommand.getKey())));
            swordCommand.setValue(new String(setCommand.getValue()));
            swordCommand.setEx(setCommand.getEx());
            swordCommand.setPx(setCommand.getPx());
        }
        return swordCommand;
    }
}
