package xyz.wendyltanpcy.easytodo.helper;

/**
 * Created by Wendy on 2017/10/7.
 */

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import xyz.wendyltanpcy.easytodo.R;


public class ColorManager {

    public static final int DEFAULT_COLOR = 0xFF525B8D;
    private final List<OnColorChangedListener> listeners = new ArrayList<>();
    private int mCurrentColor = DEFAULT_COLOR;
    private int mStoreColor = DEFAULT_COLOR;
    private static ColorManager instance;
    public static boolean IS_COLOR_CHANGE = false;

    public static ColorManager getInstance() {
        if (instance == null) {
            return instance = new ColorManager();
        }
        return instance;
    }

    public void addListener(OnColorChangedListener listener) {
        if (!this.listeners.contains(listener)) {
            if (listener != null) {
                listener.onColorChanged(mCurrentColor);
                this.listeners.add(listener);
            }
        }
    }

    public void removeListener(OnColorChangedListener listener) {
        this.listeners.remove(listener);
    }

    public void notifyColorChanged(int color) {
        if (mCurrentColor == color) {
            return;
        }
        mCurrentColor = color;
        for (OnColorChangedListener listener : this.listeners) {
            if (listener != null) {
                listener.onColorChanged(color);
                IS_COLOR_CHANGE = true;
            }
        }
    }

    public int[] getSkinColor(Context context) {
        return context.getResources().getIntArray(R.array.default_color_array);
    }

    public int getStoreColor(){
        return mStoreColor;
    }

    public void setSkinColor(Context context, int position) {
        int[] colorArr = context.getResources().getIntArray(
                R.array.default_color_array);
        MyApplication.getInstance(context).setSkinColorValue(colorArr[position]);
        mStoreColor = colorArr[position];
        notifyColorChanged(colorArr[position]);
    }

}