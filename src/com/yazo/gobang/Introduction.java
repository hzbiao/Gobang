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
        frm = new Form("���������");
        frm.append("��������һ�����˶��ĵĴ�������������Ϸ��" +
        		"����Դ���й��Ŵ��Ĵ�ͳ�ڰ�����֮һ����չ���ձ���" +
        		"������ŷ�����������֣����ٽ��ˣ�����Ȥζ������" +
        		"������ʤ����������ǿ˼ά������������������Ҹ�������" +
        		"�������������ԡ�\n");
        cmdOK = new Command("ȷ��", Command.OK, 2);

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