package xyz.wendyltanpcy.myapplication.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Wendy on 2017/10/8.
 */

public class ThemeColor extends DataSupport {
    private int Color;

    public void setColor(int color) {
        Color = color;
    }

    public int getColor() {
        return Color;
    }
}
