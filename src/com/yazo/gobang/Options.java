package com.yazo.gobang;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class Options implements CommandListener, ItemStateListener
{
    private GobangMIDlet midlet;
    private Form form;
    private Command cmdOK;
    private Command cmdCancel;
    private Gauge gaugeSize;
    private ChoiceGroup choiceFirst;
    private ChoiceGroup choiceDegree;
    private int boardSize;
    private boolean isComputerFirst;
    private int degree;
    public Options(GobangMIDlet m)
    {
        boardSize = 15;
        isComputerFirst = true;
        degree = 1;
        midlet = m;
        loadOptions();
        form = new Form("��Ϸ����");
        gaugeSize = new Gauge("���̴�С: " + boardSize + " X " + boardSize, true, 10, boardSize - 10);
        form.append(gaugeSize);
        choiceFirst = new ChoiceGroup(null, 2);
        choiceFirst.append("�Է�����", null);
        choiceFirst.setSelectedIndex(0, isComputerFirst);
        form.append(choiceFirst);
        choiceDegree = new ChoiceGroup("�������ܼ���:", 1);
        choiceDegree.append("��ʦѧ��", null);
        choiceDegree.append("����é®", null);
        choiceDegree.append("��������", null);
        choiceDegree.append("˭������", null);
        choiceDegree.setSelectedIndex(degree-1 , true);
        form.append(choiceDegree);
        form.setItemStateListener(this);
        cmdOK = new Command("ȷ��", Command.OK, 2);
        cmdCancel = new Command("ȡ��", Command.CANCEL, 1);
        form.addCommand(cmdOK);
        form.addCommand(cmdCancel);
        form.setCommandListener(this);
    }
    public int getBoardSize()
    {
        return boardSize;
    }
    public boolean isComputerFirst()
    {
        return isComputerFirst;
    }
    public int getDegree()
    {
        return degree;
    }

    public Form getForm()
    {
        return form;
    }

    public void itemStateChanged(Item item)
    {
        if(item == gaugeSize)
        {
            int bs = gaugeSize.getValue() + 10;
            gaugeSize.setLabel("���̴�С: " + bs + " X " + bs);
        }
    }

    public void commandAction(Command c, Displayable s)
    {
        if(c == cmdOK)
        {
            boardSize = gaugeSize.getValue() + 10;
            if(boardSize > 20)
                boardSize = 20;
            if(boardSize < 10)
                boardSize = 10;
            isComputerFirst = choiceFirst.isSelected(0);
            degree = choiceDegree.getSelectedIndex() + 1;
            saveOptions();
            midlet.comeBack();
        } else
        if(c == cmdCancel)
            midlet.comeBack();
    }

    private void loadOptions()
    {
        try
        {
            RecordStore rs = RecordStore.openRecordStore("Options", false);
            if(rs.getNumRecords() > 0)
            {
                byte bs[] = rs.getRecord(1);
                if(bs.length >= 3)
                {
                    boardSize = bs[0];
                    if(boardSize < 10)
                        boardSize = 10;
                    if(boardSize > 20)
                        boardSize = 20;
                    isComputerFirst = bs[1] == 1;
                    degree = bs[2];
                    if(degree < 1)
                        degree = 1;
                    if(degree > 4)
                        degree = 4;
                }
            }
            rs.closeRecordStore();
        }
        catch(RecordStoreException e) { }
    }

    private void saveOptions()
    {
        try
        {
            RecordStore rs = RecordStore.openRecordStore("Options", true);
            byte bs[] = new byte[3];
            bs[0] = (byte)boardSize;
            bs[1] = (byte)(isComputerFirst ? 1 : 0);
            bs[2] = (byte)degree;
            if(rs.getNumRecords() > 0)
                rs.setRecord(1, bs, 0, 3);
            else
                rs.addRecord(bs, 0, 3);
            rs.closeRecordStore();
        }
        catch(RecordStoreException e) { }
    }


}