package com.zq.sword.array.redis.replicator;

import com.moilioncircle.redis.replicator.cmd.Command;
import com.moilioncircle.redis.replicator.cmd.impl.SetCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SetExCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SetNxCommand;
import com.moilioncircle.redis.replicator.cmd.impl.SetRangeCommand;
import com.zq.sword.array.data.SwordCommand;
import com.zq.sword.array.redis.CommandType;

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
            swordCommand.setType(CommandType.SET.getValue());
            swordCommand.setKey(new String((setCommand.getKey())));
            swordCommand.setValue(new String(setCommand.getValue()));
            swordCommand.setEx(setCommand.getEx());
            swordCommand.setPx(setCommand.getPx());
        }else if (command instanceof SetExCommand){
            SetExCommand setCommand = (SetExCommand) command;
            swordCommand.setType(CommandType.SET_EX.getValue());
            swordCommand.setKey(new String((setCommand.getKey())));
            swordCommand.setValue(new String(setCommand.getValue()));
            swordCommand.setEx(setCommand.getEx());
        }else if (command instanceof SetNxCommand){
            SetNxCommand setCommand = (SetNxCommand) command;
            swordCommand.setType(CommandType.SET_NX.getValue());
            swordCommand.setKey(new String((setCommand.getKey())));
            swordCommand.setValue(new String(setCommand.getValue()));
        }else if (command instanceof SetRangeCommand){
            SetRangeCommand setCommand = (SetRangeCommand) command;
            swordCommand.setType(CommandType.SET_NX.getValue());
            swordCommand.setKey(new String((setCommand.getKey())));
            swordCommand.setValue(new String(setCommand.getValue()));
            swordCommand.setIndex(setCommand.getIndex());
        }
        return swordCommand;
    }
}
