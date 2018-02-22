package com.androideasily.customspinner;

/**
 * Created by Anand on 27-06-2017.
 */

class SpinnerData {
    private String iconName;
    private int icon;

    SpinnerData(String iconName, int icon) {
        this.iconName = iconName;
        this.icon = icon;
    }

    public String getIconName() {
        return iconName;
    }

    public int getIcon() {
        return icon;
    }

}
