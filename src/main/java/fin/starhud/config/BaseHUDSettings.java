package fin.starhud.config;

import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.GrowthDirectionY;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

import java.util.ArrayList;
import java.util.List;

public class BaseHUDSettings implements ConfigData {
    @Comment("Toggle HUD")
    public boolean shouldRender;

    public int x;
    public int y;

    @Comment("HUD default Horizontal location")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public ScreenAlignmentX originX;

    @Comment("HUD default Vertical location")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public ScreenAlignmentY originY;

    @Comment("Which way should the HUD goes when the length increases horizontally? (Recommended to go the opposite way from Alignment)")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public GrowthDirectionX growthDirectionX;

    @Comment("Which way should the HUD goes when the length increases vertically? (Recommended to go the opposite way from Alignment)")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public GrowthDirectionY growthDirectionY;

    @Comment("Set to 0 or below for default GUI Scale")
    public float scale = 0;

    @Comment("Modify HUD Based on Conditions.")
    public List<ConditionalSettings> conditions = new ArrayList<>();

    public BaseHUDSettings(boolean shouldRender, int x, int y, ScreenAlignmentX originX, ScreenAlignmentY originY, GrowthDirectionX growthDirectionX, GrowthDirectionY growthDirectionY) {
        this.shouldRender = shouldRender;
        this.x = x;
        this.y = y;
        this.originX = originX;
        this.originY = originY;
        this.growthDirectionX = growthDirectionX;
        this.growthDirectionY = growthDirectionY;
    }

    public BaseHUDSettings(int x, int y, ScreenAlignmentX originX, ScreenAlignmentY originY, GrowthDirectionX growthDirectionX, GrowthDirectionY growthDirectionY, float scale) {
        this(true, x , y, originX, originY, growthDirectionX, growthDirectionY);
        this.scale = scale;
    }

    public boolean shouldRender() {
        if (!shouldRender) return false;

        for (ConditionalSettings condition : conditions)
            if (!condition.shouldRender())
                return false;

        return true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public GrowthDirectionX getGrowthDirectionX() {
        if (growthDirectionX == null) {
            if (getX() > 0) {
                growthDirectionX = GrowthDirectionX.RIGHT;
            } else if (getX() < 0) {
                growthDirectionX = GrowthDirectionX.LEFT;
            } else {
                growthDirectionX = GrowthDirectionX.CENTER;
            }
        }
        return growthDirectionX;
    }

    public GrowthDirectionY getGrowthDirectionY() {
        if (growthDirectionY == null) {
            if (getY() > 0) {
                growthDirectionY = GrowthDirectionY.DOWN;
            } else if (getY() < 0) {
                growthDirectionY = GrowthDirectionY.UP;
            } else {
                growthDirectionY = GrowthDirectionY.MIDDLE;
            }
        }
        return growthDirectionY;
    }

    public ScreenAlignmentX getOriginX() {
        if (originX == null) {
            if (getX() > 0) {
                originX = ScreenAlignmentX.LEFT;
            } else if (getX() < 0) {
                originX = ScreenAlignmentX.RIGHT;
            } else {
                originX = ScreenAlignmentX.CENTER;
            }
        }
        return originX;
    }

    public ScreenAlignmentY getOriginY() {
        if (originY == null) {
            if (getY() > 0) {
                originY = ScreenAlignmentY.TOP;
            } else if (getY() < 0) {
                originY = ScreenAlignmentY.BOTTOM;
            } else {
                originY = ScreenAlignmentY.MIDDLE;
            }
        }
        return originY;
    }

    public float getScale() {
        if (scale < 0)
            scale = 0;
        return scale;
    }

    public List<ConditionalSettings> getConditions() {
        return conditions;
    }

    @Override
    public String toString() {
        return "BaseHUDSettings{" +
                "shouldRender=" + shouldRender +
                ", x=" + x +
                ", y=" + y +
                ", originX=" + originX +
                ", originY=" + originY +
                ", growthDirectionX=" + growthDirectionX +
                ", growthDirectionY=" + growthDirectionY +
                ", scale=" + scale +
                ", conditions=" + conditions +
                '}';
    }


    public boolean isEqual(BaseHUDSettings b) {
        return (this.x == b.x)
                && (this.y == b.y)
                && (this.originX == b.originX)
                && (this.originY == b.originY)
                && (this.growthDirectionX == b.growthDirectionX)
                && (this.growthDirectionY == b.growthDirectionY)
                && (this.scale == b.scale);
    }

    public void copyFrom(BaseHUDSettings other) {
        this.x = other.x;
        this.y = other.y;
        this.originX = other.originX;
        this.originY = other.originY;
        this.growthDirectionX = other.growthDirectionX;
        this.growthDirectionY = other.growthDirectionY;
        this.scale = other.scale;
        this.conditions = List.copyOf(other.conditions);
    }

    public void copySettings(BaseHUDSettings src) {
        this.x = src.x;
        this.y = src.y;
        this.originX = src.originX;
        this.originY = src.originY;
        this.growthDirectionX = src.growthDirectionX;
        this.growthDirectionY = src.growthDirectionY;
        this.scale = src.scale;
    }

    public int getGrowthDirectionHorizontal(int dynamicWidth) {
        return this.getGrowthDirectionX().getGrowthDirection(dynamicWidth);
    }

    public int getGrowthDirectionVertical(int dynamicHeight) {
        return this.getGrowthDirectionY().getGrowthDirection(dynamicHeight);
    }

    // get the scaled factor
    // this can either make your HUD bigger or smaller.
    public float getScaledFactor() {
        return this.getScale() <= 0 ? 1 : (float) MinecraftClient.getInstance().getWindow().getScaleFactor() / this.getScale();
    }

    // this shifts your HUD based on your x point, and alignment on X axis, and place them accordingly in your screen.
    public int getCalculatedPosX() {
        Window window = MinecraftClient.getInstance().getWindow();
        int scaledWidth = (window == null ? 0 : window.getScaledWidth());

        return this.getX() + (int) (this.getOriginX().getAlignmentPos(scaledWidth) * getScaledFactor());
    }

    // this also shifts your HUD based on your y point, and alignment on Y axis, and place them accordingly in your screen.
    public int getCalculatedPosY() {
        Window window = MinecraftClient.getInstance().getWindow();
        int scaledHeight = (window == null ? 0 : window.getScaledHeight());

        return this.getY() + (int) (this.getOriginY().getAlignmentPos(scaledHeight) * getScaledFactor());
    }
}
