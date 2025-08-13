package fin.starhud.helper;

import fin.starhud.Main;
import fin.starhud.config.GeneralSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

public class RenderUtils {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final GeneralSettings.HUDSettings HUD_SETTINGS = Main.settings.generalSettings.hudSettings;

    private static final int ITEM_HUD_ICON_WIDTH = 22;
    private static final int ITEM_HUD_ICON_HEIGHT = 22;

    public static void drawSmallHUD(DrawContext context, String infoStr, int x, int y, int width, int height, Identifier iconTexture, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight, int color, int iconColor, HUDDisplayMode displayMode, boolean drawBackground) {
        OrderedText orderedText = OrderedText.styledForwardsVisitedString(infoStr, Style.EMPTY);
        drawSmallHUD(context, orderedText, x, y, width, height, iconTexture, u, v, textureWidth, textureHeight, iconWidth, iconHeight, color, iconColor, displayMode, drawBackground);
    }

    public static void drawSmallHUD(DrawContext context, OrderedText infoText, int x, int y, int width, int height, Identifier iconTexture, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight, int color, int iconColor, HUDDisplayMode displayMode, boolean drawBackground) {
        int padding = HUD_SETTINGS.textPadding;
        int gap = HUD_SETTINGS.iconInfoGap;

        switch (displayMode) {
            case ICON ->  {
                if (drawBackground)
                    fillRounded(context, x, y, x + iconWidth, y + iconHeight, 0x80000000);
                drawTextureHUD(context, iconTexture, x, y, u, v, iconWidth, iconHeight, textureWidth, textureHeight, iconColor);
            }
            case INFO ->  {
                if (drawBackground)
                    fillRounded(context, x, y, x + width, y + height, 0x80000000);
                drawTextHUD(context, infoText, x + padding, y + 3, color, false);
            }
            case BOTH ->  {
                if (drawBackground) {
                    if (gap <= 0)
                        fillRounded(context, x, y, x + width, y + height, 0x80000000);
                    else {
                        fillRoundedLeftSide(context, x, y, x + iconWidth, y + height, 0x80000000);
                        fillRoundedRightSide(context, x + iconWidth + gap, y, x + width, y + height, 0x80000000);
                    }
                }
                drawTextureHUD(context, iconTexture, x, y, u, v, iconWidth, iconHeight, textureWidth, textureHeight, iconColor);
                drawTextHUD(context, infoText, x + iconWidth + gap + padding, y + 3, color, false);
            }
        }
    }

    public static void drawSmallHUD(DrawContext context, String infoStr, int x, int y, int width, int height, Identifier iconTexture, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight, int color, HUDDisplayMode displayMode, boolean drawBackground) {
        OrderedText orderedText = OrderedText.styledForwardsVisitedString(infoStr, Style.EMPTY);
        drawSmallHUD(context, orderedText, x, y, width, height, iconTexture, u, v, textureWidth, textureHeight, iconWidth, iconHeight, color, color, displayMode, drawBackground);
    }

    public static void drawSmallHUD(DrawContext context, OrderedText infoText, int x, int y, int width, int height, Identifier iconTexture, float u, float v, int textureWidth, int textureHeight, int iconWidth, int iconHeight, int color, HUDDisplayMode displayMode, boolean drawBackground) {
        drawSmallHUD(context, infoText, x, y, width, height, iconTexture, u, v, textureWidth, textureHeight, iconWidth, iconHeight, color, color, displayMode, drawBackground);
    }

    public static void drawItemHUD(DrawContext context, String str, int x, int y, int width, int height, ItemStack itemAsIcon, int textColor, HUDDisplayMode displayMode, boolean drawBackground) {
        int padding = HUD_SETTINGS.textPadding;
        int gap = HUD_SETTINGS.iconInfoGap;

        switch (displayMode) {
            case ICON -> {
                if (drawBackground)
                    fillRounded(context, x, y, x + ITEM_HUD_ICON_WIDTH, y + ITEM_HUD_ICON_HEIGHT, 0x80000000);
                context.drawItem(itemAsIcon, x + 3, y + 3);
            }
            case INFO -> {
                if (drawBackground)
                    fillRounded(context, x, y, x + width, y + height, 0x80000000);
                drawTextHUD(context, str, x + padding, y + 7, textColor, false);
            }
            case BOTH -> {
                if (drawBackground) {
                    if (gap <= 0)
                        fillRounded(context, x, y, x + width, y + height, 0x80000000);
                    else {
                        fillRoundedLeftSide(context, x, y, x + ITEM_HUD_ICON_WIDTH, y + height, 0x80000000);
                        fillRoundedRightSide(context, x + ITEM_HUD_ICON_WIDTH + gap, y, x + width, y + height, 0x80000000);
                    }
                }

                context.drawItem(itemAsIcon, x + 3, y + 3);
                drawTextHUD(context, str, x + ITEM_HUD_ICON_WIDTH + gap + padding, y + 7, textColor, false);
            }
        }
    }

    public static void fillRoundedRightSide(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        if (HUD_SETTINGS.drawBackgroundRounded) {
            context.fill(x1, y1, x2 - 1, y2, color);
            context.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
        } else {
            context.fill(x1, y1, x2, y2, color);
        }
    }

    public static void fillRoundedLeftSide(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        if (HUD_SETTINGS.drawBackgroundRounded) {
            context.fill(x1, y1 + 1, x1 + 1, y2 - 1, color);
            context.fill(x1 + 1, y1, x2, y2, color);
        } else {
            context.fill(x1, y1, x2, y2, color);
        }
    }

    public static void fillRoundedUpperSide(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        if (HUD_SETTINGS.drawBackgroundRounded) {
            context.fill(x1 + 1, y1, x2 - 1, y1 + 1, color);
            context.fill(x1, y1 + 1, x2, y2, color);
        } else {
            context.fill(x1, y1, x2, y2, color);
        }
    }

    public static void fillRoundedBottomSide(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        if (HUD_SETTINGS.drawBackgroundRounded) {
            context.fill(x1, y1, x2, y2 - 1, color);
            context.fill(x1 + 1, y2 - 1, x2 - 1, y2, color);
        } else {
            context.fill(x1, y1, x2, y2, color);
        }
    }

    public static void fillRounded(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        if (HUD_SETTINGS.drawBackgroundRounded) {
            context.fill(x1, y1 + 1, x1 + 1, y2 - 1, color);
            context.fill(x1 + 1, y1, x2 - 1, y2, color);
            context.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
        } else {
            context.fill(x1, y1, x2, y2, color);
        }
    }

    public static void drawBorderedFill(DrawContext context, int x1, int y1, int x2, int y2, int fillColor, int borderColor) {
        context.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillColor);
        context.drawBorder(x1, y1, x2 - x1, y2 - y1, borderColor);
    }

    // for easier version porting.

    public static void drawTextureHUD(DrawContext context, Identifier identifier, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, x, y, u, v, width, height, textureWidth, textureHeight, color);
    }

    public static void drawTextureHUD(DrawContext context, Identifier identifier, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, identifier, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public static void drawTextHUD(DrawContext context, String str, int x, int y, int color, boolean shadow) {
        OrderedText orderedText = OrderedText.styledForwardsVisitedString(str, Style.EMPTY);
        context.drawText(CLIENT.textRenderer, orderedText, x , y + HUD_SETTINGS.textYOffset, color, shadow);
    }

    public static void drawTextHUD(DrawContext context, OrderedText text, int x, int y, int color, boolean shadow) {
        context.drawText(CLIENT.textRenderer, text, x, y + HUD_SETTINGS.textYOffset, color, shadow);
    }
}
