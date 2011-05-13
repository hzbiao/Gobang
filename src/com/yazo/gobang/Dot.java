package com.yazo.gobang;

public class Dot
{

    public Dot()
    {
        row = 0;
        col = 0;
    }

    public Dot(int r, int c)
    {
        row = r;
        col = c;
    }

    public Dot(int boardSize)
    {
        row = boardSize / 2;
        col = boardSize / 2;
    }

    public void setRowCol(int r, int c)
    {
        row = r;
        col = c;
    }

    public void copyFrom(Dot d)
    {
        row = d.row;
        col = d.col;
    }

    public boolean isInBoard(int boardSize)
    {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    public int row;
    public int col;
}