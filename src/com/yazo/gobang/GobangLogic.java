package com.yazo.gobang;


import java.util.Random;
import java.util.Stack;

public class GobangLogic
{
    private GobangCanvas myCanvas;
    private int boardSize;
//    private boolean isComputerFirst;
//    private int degree;
    public static int PLAYER_NONE = 0;
    public static int PLAYER_COMPUTER = 1;
    public static int PLAYER_MAN = 2;
    private int table[][];
    private Dot lastDot;
    private int playerCounter[];
    private Stack steps;
    private Dot triedDot;
    private boolean isGameOver;
	private boolean isComputerWon;
    private boolean isThinking;
    private Random rndNum;
    public GobangLogic(GobangCanvas canvas, int boardSize, boolean isComputerFirst, int degree)
    {

    	
        this.boardSize = 15;
//        isComputerFirst = true;
//        degree = 1;
        myCanvas = canvas;
        this.boardSize = boardSize;

//        this.isComputerFirst = isComputerFirst;
//        this.degree = degree;
        table = new int[boardSize][boardSize];
        for(int r = 0; r < boardSize; r++)
        {
            for(int c = 0; c < boardSize; c++)
                table[r][c] = 0;

        }

        
        playerCounter = new int[3];
        playerCounter[0] = boardSize * boardSize;
        playerCounter[1] = 0;
        playerCounter[2] = 0;
        lastDot = new Dot(boardSize);
        steps = new Stack();
        triedDot = new Dot(-1, -1);
        isGameOver = false;
        isThinking = false;
        rndNum = new Random();
    }

    public int[][] getTable()
    {
        return table;
    }

    public Dot lastDot()
    {
        return lastDot;
    }

    public Dot triedDot()
    {
        return triedDot;
    }

    public boolean checkGameOver()
    {
        return isGameOver;
    }

    public boolean isComputerWon()
    {
        return isComputerWon;
    }

    public boolean isThinking()
    {
        return isThinking;
    }

    private boolean isGameOver()
    {
        isGameOver = false;
        for(int r = 0; r < boardSize; r++)
        {
            for(int c = 0; c < boardSize; c++)
            {
                if(table[r][c] == 0 || checkFiveInRow(r, c, 5, -1) == -1)
                    continue;
                isGameOver = true;
                isComputerWon = table[r][c] == 1;
                System.out.println("是否是电脑赢："+isComputerWon);
                break;
            }

            if(isGameOver)
                break;
        }

        if(isGameOver)
            myCanvas.notifyGameEnd();
        return isGameOver;
    }

    public void manGo(int row, int col)
    {
        if(row >= 0 && row < boardSize && col >= 0 && col < boardSize && table[row][col] == 0)
        {
            goAt(row, col, 2);
            if(isGameOver())
            {
                if(isComputerWon)
                    myCanvas.setStatus("真遗憾,你输了！", 0xff0000, 2);
                else
                    myCanvas.setStatus("恭喜你，你赢了！", 65280, 1);
            } else
            {
                computerGo();
            }
        }
    }

    private void goAt(int row, int col, int player)
    {
        int lastRow = lastDot.row;
        int lastCol = lastDot.col;
        table[row][col] = player;
        lastDot.setRowCol(row, col);
        myCanvas.repaintAt(lastRow, lastCol);
        myCanvas.repaintAt(row, col);
        switch(player)
        {
        case 1: 
            playerCounter[1]++;
            break;

        case 2: 
            playerCounter[2]++;
            break;
        }
        playerCounter[0]--;
        if(steps.size() > 10)
            steps.removeElementAt(0);
        steps.push(new Dot(row, col));
    }

    public boolean undo()
    {
        if(steps.size() >= 3)
        {
            Dot d = new Dot();
            d.copyFrom((Dot)steps.pop());
            table[d.row][d.col] = 0;
            myCanvas.repaintAt(d.row, d.col);
            d.copyFrom((Dot)steps.pop());
            table[d.row][d.col] = 0;
            myCanvas.repaintAt(d.row, d.col);
            d.copyFrom((Dot)steps.peek());
            lastDot.copyFrom(d);
            myCanvas.repaintAt(d.row, d.col);
            return true;
        } else
        {
            return false;
        }
    }

    public void computerGo()
    {
        myCanvas.setStatus("思考中···", 0, 0);
        myCanvas.serviceRepaints();
        think();
    }

    public void think()
    {
        isThinking = true;
        Dot dc = null;
        if((dc = to5L(1)) == null && (dc = to5L(2)) == null && (dc = to4B(1)) == null && (dc = to4B(2)) == null && (dc = toDouble4S_3B_2N1B(1, true)) == null && (dc = toDouble4S_3B_2N1B(2, true)) == null && (dc = toDouble4S_3B_2N1B(1, false)) == null && (dc = toDouble4S_3B_2N1B(2, false)) == null && (dc = toSingle4S_3B_2N1B(1)) == null)
            dc = toSingle4S_3B_2N1B(2);
        if(dc == null)
            dc = maxGainedDot();
        if(dc == null || playerCounter[0] == 0)
        {
            myCanvas.setStatus("平局！", 255, 3);
        } else
        {
            goAt(dc.row, dc.col, 1);
            if(isGameOver())
            {
                if(isComputerWon)
                    myCanvas.setStatus("真遗憾，你输了！", 0xff0000, 2);
                else
                    myCanvas.setStatus("真厉害！", 65280, 1);
            } else
            {
                myCanvas.setStatus("请落子···");
            }
        }
        isThinking = false;
    }

    private Dot to4B(int player)
    {
        if(playerCounter[player] < 3)
            return null;
        Dot dot = null;
        int maxGain = 0;
        for(int r = 1; r < boardSize - 1; r++)
        {
            for(int c = 1; c < boardSize - 1; c++)
                if(table[r][c] == 0)
                {
                    int cd[] = connectedIn8D(player, r, c);
                    int ed[] = expandedIn8D(player, r, c);
                    for(int i = 0; i < 4; i++)
                        if(ed[i] > cd[i] && ed[i + 4] > cd[i + 4] && cd[i] + cd[i + 4] + 1 >= 4)
                        {
                            int gain = gainAt(r, c);
                            if(gain > maxGain || gain > 0 && gain == maxGain && randomTrue())
                            {
                                maxGain = gain;
                                dot = new Dot(r, c);
                            }
                        }

                }

        }

        return dot;
    }

    private Dot toSingle4S_3B_2N1B(int player)
    {
        if(playerCounter[player] < 2)
            return null;
        Dot dot = null;
        for(int r = 0; r < boardSize; r++)
        {
            for(int c = 0; c < boardSize; c++)
            {
                if(table[r][c] != 0 || find4S_3B_2N1BAt(r, c, player, -1) == -1)
                    continue;
                dot = new Dot(r, c);
                break;
            }

            if(dot != null)
                break;
        }

        return dot;
    }

    private Dot toDouble4S_3B_2N1B(int player, boolean only4S)
    {
        if(playerCounter[player] < 4)
            return null;
        Dot dot = null;
        for(int rTest = 0; rTest < boardSize; rTest++)
        {
            for(int cTest = 0; cTest < boardSize; cTest++)
            {
                if(table[rTest][cTest] != 0)
                    continue;
                int cd[] = connectedIn8D(player, rTest, cTest);
                if(cd[0] + cd[1] + cd[2] + cd[3] + cd[4] + cd[5] + cd[6] + cd[7] <= 0)
                    continue;
                triedDot.setRowCol(rTest, cTest);
                table[rTest][cTest] = player;
                boolean found = false;
                int dFirst = find4S_3B_2N1B(player, -1, rTest, cTest, only4S);
                if(dFirst != -1 && find4S_3B_2N1B(player, dFirst, rTest, cTest, false) != -1)
                    found = true;
                table[rTest][cTest] = 0;
                triedDot.setRowCol(-1, -1);
                if(!found)
                    continue;
                dot = new Dot(rTest, cTest);
                break;
            }

            if(dot != null)
                break;
        }

        return dot;
    }

    private int find4SAt(int row, int col, int player, int exceptDirection)
    {
        int dFond = -1;
        int cd[] = connectedIn8D(player, row, col);
        int ed[] = expandedIn8D(player, row, col);
        for(int d = 0; d < 4; d++)
        {
            if(d == exceptDirection || table[row][col] != player)
                continue;
            int nConnect = cd[d] + cd[d + 4] + 1;
            int nFree1 = ed[d] - cd[d];
            int nFree2 = ed[d + 4] - cd[d + 4];
            boolean b4S = nConnect >= 4 && (nFree1 >= 1 || nFree2 >= 1);
            if(!b4S)
                continue;
            dFond = d;
            break;
        }

        return dFond;
    }

    private int find4S_3B_2N1BAt(int row, int col, int player, int exceptDirection)
    {
        int dFond = -1;
        int cd[] = connectedIn8D(player, row, col);
        int ed[] = expandedIn8D(player, row, col);
        for(int d = 0; d < 4; d++)
        {
            if(d == exceptDirection)
                continue;
            if(table[row][col] == player)
            {
                int nConnect = cd[d] + cd[d + 4] + 1;
                int nFree1 = ed[d] - cd[d];
                int nFree2 = ed[d + 4] - cd[d + 4];
                boolean b4S = nConnect >= 4 && (nFree1 >= 1 || nFree2 >= 1);
                boolean b3B = nConnect >= 3 && nFree1 >= 1 && nFree2 >= 1;
                if(b4S || b3B)
                {
                    dFond = d;
                    break;
                }
            }
            if(table[row][col] != 0)
                continue;
            int nFree1 = ed[d] - cd[d];
            int nFree2 = ed[d + 4] - cd[d + 4];
            boolean b2N1 = cd[d] >= 2 && cd[d + 4] >= 1 || cd[d] >= 1 && cd[d + 4] >= 2;
            boolean bSFree = nFree1 >= 1 && nFree2 >= 1;
            if(!b2N1 || !bSFree)
                continue;
            dFond = d;
            break;
        }

        return dFond;
    }

    private int find4S_3B_2N1B(int player, int exceptDirection, int rTest, int cTest, boolean only4S)
    {
        int dFond = -1;
        int rMin = rTest - 3;
        if(rMin < 0)
            rMin = 0;
        int rMax = rTest + 3;
        if(rMax > boardSize)
            rMax = boardSize;
        int cMin = cTest - 3;
        if(cMin < 0)
            cMin = 0;
        int cMax = cTest + 3;
        if(cMax > boardSize)
            cMax = boardSize;
        for(int r = rMin; r < rMax; r++)
        {
            for(int c = cMin; c < cMax; c++)
            {
                if(table[r][c] != player && table[r][c] != 0)
                    continue;
                if(only4S)
                    dFond = find4SAt(r, c, player, exceptDirection);
                else
                    dFond = find4S_3B_2N1BAt(r, c, player, exceptDirection);
                if(dFond != -1)
                    break;
            }

            if(dFond != -1)
               break;
        }

        return dFond;
    }

    private Dot to5L(int player)
    {
        if(playerCounter[player] < 4)
            return null;
        int maxGain = 0;
        Dot dot = null;
        for(int r = 0; r < boardSize; r++)
        {
            for(int c = 0; c < boardSize; c++)
            {
                int gain = to5LAt(player, r, c);
                if(gain > maxGain || gain > 0 && gain == maxGain && randomTrue())
                {
                    maxGain = gain;
                    dot = new Dot(r, c);
                }
            }

        }

        return dot;
    }

    private int to5LAt(int player, int row, int col)
    {
        int lines = 0;
        int otherGain = 0;
        if(table[row][col] == 0)
        {
            int cd[] = connectedIn8D(player, row, col);
            int ed[] = expandedIn8D(player, row, col);
            for(int i = 0; i < 4; i++)
                if(ed[i] + ed[i + 4] + 1 >= 5)
                {
                    int l = cd[i] + cd[i + 4] + 1;
                    if(l >= 5)
                        lines++;
                    else
                        otherGain += 2 ^ l;
                }

        }
        return lines > 0 ? lines * 32 + otherGain : 0;
    }

    private int[] expandedIn8D(int player, int row, int col)
    {
        int ed[] = new int[8];
        for(int d = 0; d < 8; d++)
            ed[d] = expandedIn1D(player, row, col, d);

        return ed;
    }

    private int expandedIn1D(int player, int row, int col, int direction)
    {
        int n = 0;
        int cn = 0;
        Dot d = new Dot(row, col);
        while(cn < 4) 
        {
            d.copyFrom(moveOneStep(d, direction));
            if(!d.isInBoard(boardSize))
                break;
            int p = table[d.row][d.col];
            if(p == 0)
                cn++;
            if(p != player && p != 0)
                break;
            n++;
        }
        return n;
    }

    private Dot maxGainedDot()
    {
        Dot dotWithMaxGain = null;
        int maxGain = 0;
        for(int r = 0; r < boardSize; r++)
        {
            for(int c = 0; c < boardSize; c++)
            {
                int gain = gainAt(r, c);
                if(gain > maxGain || gain > 0 && gain == maxGain && randomTrue())
                {
                    maxGain = gain;
                    dotWithMaxGain = new Dot(r, c);
                }
            }

        }

        return dotWithMaxGain;
    }

    private int gainAt(int row, int col)
    {
        if(table[row][col] == 0)
        {
            int gain = 0;
            for(int d = 0; d < 8; d++)
            {
                int gd = gainAtDirection(row, col, d);
                if(gd == 0)
                    gain >>= 2;
                else
                    gain += gd;
            }

            if(gain < 1)
                gain = 1;
            return gain;
        } else
        {
            return 0;
        }
    }

    private int gainAtDirection(int row, int col, int direction)
    {
        int gain = 0;
        Dot d = new Dot(row, col);
        int step = 0;
        do
        {
            d.copyFrom(moveOneStep(d, direction));
            step++;
            if(!d.isInBoard(boardSize))
                break;
            int player = table[d.row][d.col];
            if(player == 2)
                break;
            int gainByStone = player == 1 ? 5 : 1;
            gain += gainByStep(step) * gainByStone;
        } while(true);
        return gain;
    }

    private int gainByStep(int step)
    {
        int gain = (boardSize - step) / 2;
        if(gain < 1)
            gain = 1;
        return gain;
    }

 

    private int checkFiveInRow(int row, int col, int n, int exceptDirection)
    {
        int player = table[row][col];
        int cd[] = connectedIn8D(player, row, col);
        int ed[] = expandedIn8D(player, row, col);
        int existDirection = -1;
        for(int i = 0; i < 4;  i++)
        {                
            if(i == exceptDirection || cd[i] + cd[i + 4] + 1 < n || (ed[i] - cd[i]) + (ed[i + 4] - cd[i + 4]) < 0)
                continue;
               

            existDirection = i;
            break;
        }

        return existDirection;
    }

    private int[] connectedIn8D(int player, int row, int col)
    {
        int cd[] = new int[8];
        for(int d = 0; d < 8; d++)
            cd[d] = connectedIn1D(player, row, col, d);

        return cd;
    }

    private int connectedIn1D(int player, int row, int col, int direction)
    {
        int n = 0;
        Dot d = new Dot(row, col);
        do
        {
            d.copyFrom(moveOneStep(d, direction));
            if(d.isInBoard(boardSize) && table[d.row][d.col] == player)
                n++;
            else
                return n;
        } while(true);
    }

    private Dot moveOneStep(Dot d, int direction)
    {
        int r = d.row;
        int c = d.col;
        switch(direction)
        {
        case 0: 
            c++;
            break;

        case 1: 
            r--;
            c++;
            break;

        case 2: 
            r--;
            break;

        case 3: 
            r--;
            c--;
            break;

        case 4: 
            c--;
            break;

        case 5: 
            r++;
            c--;
            break;

        case 6: 
            r++;
            break;

        case 7: 
            r++;
            c++;
            break;
        }
        return new Dot(r, c);
    }

    private boolean randomTrue()
    {
        return rndNum.nextInt() % 2 == 0;
    }


}