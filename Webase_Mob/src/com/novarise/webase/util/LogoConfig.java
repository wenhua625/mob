package com.novarise.webase.util;



import java.awt.Color;

public class LogoConfig {
    // logoĬ�ϱ߿���ɫ
    public static final Color DEFAULT_BORDERCOLOR = Color.WHITE;
    // logoĬ�ϱ߿���
    public static final int DEFAULT_BORDER = 0;
    // logo��СĬ��Ϊ��Ƭ��1/6
    public static final int DEFAULT_LOGOPART = 6;
 
    private final int border = DEFAULT_BORDER;
    private final Color borderColor;
    private final int logoPart;
 
    /**
     * Creates a default config with on color {@link #BLACK} and off color
     * {@link #WHITE}, generating normal black-on-white barcodes.
     */
    public LogoConfig()
    {
        this(DEFAULT_BORDERCOLOR, DEFAULT_LOGOPART);
    }
 
    public LogoConfig(Color borderColor, int logoPart)
    {
        this.borderColor = borderColor;
        this.logoPart = logoPart;
    }
 
    public Color getBorderColor()
    {
        return borderColor;
    }
 
    public int getBorder()
    {
        return border;
    }
 
    public int getLogoPart()
    {
        return logoPart;
    }
}


