package fin.starhud.config.hud;

import fin.starhud.config.BaseHUDSetting;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class PingSetting {

    @ConfigEntry.Gui.TransitiveObject
    public BaseHUDSetting base = new BaseHUDSetting(true, -57, -5, ScreenAlignmentX.RIGHT, ScreenAlignmentY.BOTTOM);

    @Comment("Ping update interval, in seconds.")
    public double updateInterval = 5.0;

    @ConfigEntry.Gui.CollapsibleObject
    public PingColorSetting pingColor = new PingColorSetting();

    public static class PingColorSetting {
        @ConfigEntry.ColorPicker
        public int first = 0x85F290;
        @ConfigEntry.ColorPicker
        public int second = 0xECF285;
        @ConfigEntry.ColorPicker
        public int third = 0xFEBC49;
        @ConfigEntry.ColorPicker
        public int fourth = 0xFF5C71;
    }
}
