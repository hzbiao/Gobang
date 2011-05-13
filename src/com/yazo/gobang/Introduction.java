package com.yazo.gobang;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;


public class Introduction implements CommandListener
{
    private GobangMIDlet midlet;
    private Form frm;
    private Command cmdOK;
    public Introduction(GobangMIDlet m)
    {
        midlet = m;
        frm = new Form("五子棋介绍");
        frm.append("五子棋是一种两人对弈的纯策略型棋类游戏，" +
        		"是起源于中国古代的传统黑白棋种之一。发展于日本，" +
        		"流行于欧美。容易上手，老少皆宜，而且趣味横生，" +
        		"引人入胜；不仅能增强思维能力，提高智力，而且富含哲理，" +
        		"有助于修身养性。\n");
        cmdOK = new Command("确定", Command.OK, 2);

        frm.addCommand(cmdOK);

        frm.setCommandListener(this);
    }

    public Form getForm()
    {
        return frm;
    }
    public void commandAction(Command c, Displayable d)
    {
        if(c == cmdOK)
        {
            midlet.comeBack();
        } 
    }

}