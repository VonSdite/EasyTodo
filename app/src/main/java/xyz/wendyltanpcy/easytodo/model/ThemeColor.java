package xyz.wendyltanpcy.easytodo.model;

import org.litepal.crud.DataSupport;

/**
 * 存储主题颜色类
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
