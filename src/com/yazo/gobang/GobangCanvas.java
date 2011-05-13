package com.yazo.gobang;
import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class GobangCanvas extends Canvas
    implements CommandListener
{

    private GobangMIDlet midlet;
    private Command cmdStop;
    private Command cmdNew;
    private Command cmdUndo;
    private GobangLogic logic;
    private int boardSize;
    private boolean isComputerFirst;
    private int degree;
    private int canvasWidth;
    private int canvasHeight;
    private int cWidth;
    private int cHeight;
    private Font font;
    private int fontWidth;
    private int fontHeight;
    private int boardX;
    private int boardY;
    private int boardLength;
    private int gridLength;
    private int stoneLength;
    private String status;
    private int statusColor;
    private boolean isUpSide;
    private final Image imgStatus[] = new Image[4];
    private int statusImage;
    public static int THINK = 0;
    public static int SMILE = 1;
    public static int CRY = 2;
    public static int NONE = 3;
    private boolean isColor;

 
    public GobangCanvas(GobangMIDlet m)
    {
        midlet = m;
        cmdNew = new Command("重玩", Command.OK, 2);
        cmdStop = new Command("退出", Command.BACK, 1);
        cmdUndo = new Command("悔棋", Command.SCREEN, 3);
        addCommand(cmdNew);
        addCommand(cmdStop);
        setCommandListener(this);
        canvasWidth = getWidth();
        canvasHeight = getHeight();
        status = "";
        statusColor = 0;
        isUpSide = true;
        for(int i = 0; i < 4; i++)
            imgStatus[i] = Image.createImage(1, 1);

        try
        {
            imgStatus[0] = Image.createImage("/thinking.png");
        }
        catch(IOException _ex) { }
        try
        {
            imgStatus[1] = Image.createImage("/win.png");
        }
        catch(IOException _ex) { }
        try
        {
            imgStatus[2] = Image.createImage("/lose.png");
        }
        catch(IOException _ex) { }
        statusImage = 3;
        isColor = Display.getDisplay(midlet).numColors() > 2;
      
    }

    private void calcSize()
    {
        font = Font.getFont(Font.FACE_SYSTEM , 
			       Font.STYLE_BOLD, 
			       Font.SIZE_MEDIUM);
        fontWidth = font.charWidth('棋');
        fontHeight = font.getHeight();
        isUpSide = canvasHeight > canvasWidth;
        if(isUpSide)
        {
            cWidth = canvasWidth;
            cHeight = canvasHeight - fontHeight;
        } else
        {
            cWidth = canvasWidth - fontWidth;
            cHeight = canvasHeight;
        }
        boardLength = cWidth > cHeight ? cHeight : cWidth;
        gridLength = boardLength / boardSize;
        boardLength = gridLength * boardSize;
        boardX = (cWidth - boardLength) / 2;
        boardY = (cHeight - boardLength) / 2;
        if(isUpSide)
            boardY += fontHeight;
        stoneLength = gridLength - 2;
    }

    public void paint(Graphics g)
    {
        g.setColor(0xffffff);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        if(isColor)
        {
        	g.setColor(216,170,136);
            g.fillRect(boardX, boardY, boardLength, boardLength);
        }
        g.setColor(isColor ? 255 : 0);
        int y;
        for(int r = 0; r < boardSize; r++)
        {
            int x1 = boardX + gridLength / 2;
            int x2 = (x1 + boardLength) - gridLength;
            y = boardY + r * gridLength + gridLength / 2;
            g.drawLine(x1, y, x2, y);
        }

        int x;
        for(int c = 0; c < boardSize; c++)
        {
            x = boardX + c * gridLength + gridLength / 2;
            int y1 = boardY + gridLength / 2;
            int y2 = (y1 + boardLength) - gridLength;
            g.drawLine(x, y1, x, y2);
        }
        

        int computerColor;
        int manColor;
        if(isComputerFirst)
        {
            computerColor = 0;
            manColor = 0xffffff;
        } else
        {
            computerColor = 0xffffff;
            manColor = 0;
        }
        Dot triedDot = logic.triedDot();
        int triedRow = triedDot.row;
        int triedCol = triedDot.col;
        for(int r = 0; r < boardSize; r++)
        {
            for(int c = 0; c < boardSize; c++)
                if(r != triedRow || c != triedCol)
                {
                    int stone = logic.getTable()[r][c];
                    if(stone != 0)
                    {
                        x = xByCol(c) - stoneLength / 2;
                        y = yByRow(r) - stoneLength / 2;
                        g.setColor(stone == 1 ? computerColor : manColor);
                        g.fillArc(x, y, stoneLength, stoneLength, 0, 360);
                        g.setColor(0);
                        g.drawArc(x, y, stoneLength, stoneLength, 0, 360);
                    }
                }

        }

        Dot lastDot = logic.lastDot();
        int lastRow = lastDot.row;
        int lastCol = lastDot.col;
        int cLast;
        if(isColor)
        {
            cLast = 0xff0000;
        } else
        {
            cLast = 0;
            switch(logic.getTable()[lastRow][lastCol])
            {
            case 1: 
                cLast = manColor;
                break;

            case 2: 
                cLast = computerColor;
                break;
            }
        }
        g.setColor(cLast);
        x = xByCol(lastCol) - 3;
        y = yByRow(lastRow) - 3;
        g.drawRect(x, y, 6, 6);
        g.setFont(font);
        g.setColor(isColor ? statusColor : 0);
        if(isUpSide)
        {
            g.drawImage(imgStatus[statusImage], 0, 0, 20);
            x = imgStatus[statusImage].getWidth();
            g.drawString(status, x, 0, 20);
        } else
        {
            x = cWidth + fontWidth;
            g.drawImage(imgStatus[statusImage], x, 0, 24);
            x = cWidth + fontWidth / 2;
            y = imgStatus[statusImage].getHeight();
            for(int i = 0; i < status.length(); i++)
            {
                char c = status.charAt(i);
                g.drawChar(c, x, y, 17);
                y += fontHeight;
            }

        }
    }

    private int xByCol(int col)
    {
        return boardX + col * gridLength + gridLength / 2;
    }

    private int yByRow(int row)
    {
        return boardY + row * gridLength + gridLength / 2;
    }

    protected void keyPressed(int keyCode)
    {
        if(!logic.checkGameOver() && !logic.isThinking())
        {
            int bs = boardSize;
            Dot lastDot = logic.lastDot();
            int r = lastDot.row;
            int c = lastDot.col;
            repaintAt(r, c);
            int input = getGameAction(keyCode);
            if(input == Canvas.LEFT || keyCode == 52)
            {
                if(--c < 0)
                    c = bs - 1;
                lastDot.setRowCol(r, c);
                repaintAt(r, c);
            } else
            if(input == Canvas.RIGHT || keyCode == 54)
            {
                if(++c >= bs)
                    c = 0;
                lastDot.setRowCol(r, c);
                repaintAt(r, c);
            } else
            if(input == Canvas.UP || keyCode == 50)
            {
                if(--r < 0)
                    r = bs - 1;
                lastDot.setRowCol(r, c);
                repaintAt(r, c);
            } else
            if(input == Canvas.DOWN || keyCode == 56)
            {
                if(++r >= bs)
                    r = 0;
                lastDot.setRowCol(r, c);
                repaintAt(r, c);
            } else
            if(input == Canvas.FIRE || keyCode == 53)
                logic.manGo(r, c);
        }
    }

    protected void keyRepeated(int keyCode)
    {
        keyPressed(keyCode);
    }

    protected void pointerPressed(int x, int y)
    {
        if(!logic.checkGameOver() && !logic.isThinking())
        {
            int row = (y - boardY) / gridLength;
            int col = (x - boardX) / gridLength;
            logic.manGo(row, col);
        }
    }

    public void commandAction(Command c, Displayable s)
    {
        if(!logic.isThinking())
            if(c == cmdStop)
                midlet.comeBack();
            else
            if(c == cmdNew)
                newStage();
            else
            if(c == cmdUndo && !logic.undo())
                setStatus("不能悔棋!");
    }

    public void setOptions(int boardSize, boolean isComputerFirst, int degree)
    {
        this.boardSize = boardSize;
        this.isComputerFirst = isComputerFirst;
        this.degree = degree;
    }

    public boolean newStage()
    {
        addCommand(cmdUndo);
        addCommand(cmdStop);
        removeCommand(cmdNew);
        calcSize();

        logic = new GobangLogic(this, boardSize, isComputerFirst, degree);
        if(isComputerFirst)
            logic.computerGo();
        else
            setStatus("请下子···");
        repaint();
        return true;
    }

    public void notifyGameEnd()
    {
        addCommand(cmdNew);
        addCommand(cmdStop);
        removeCommand(cmdUndo);
    }

    public void setStatus(String s)
    {
        setStatus(s, 0, 3);
    }

    public void setStatus(String s, int color, int image)
    {
        status = s;
        statusColor = color;
        statusImage = image;
        int x;
        int y;
        int w;
        int h;
        if(isUpSide)
        {
            x = 0;
            y = 0;
            w = canvasWidth;
            h = fontHeight;
        } else
        {
            x = cWidth;
            y = 0;
            w = fontWidth;
            h = canvasHeight;
        }
        repaint(x, y, w, h);
    }

    public void repaintAt(int row, int col)
    {
        int pX = boardX + (col - 1) * gridLength;
        int pY = boardY + (row - 1) * gridLength;
        int pW = gridLength * 2;
        int pH = pW;
        repaint(pX, pY, pW, pH);
    }

}