package fin.starhud.helper;

import fin.starhud.Helper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class RenderUtils {

    private static final Identifier DURABILITY_TEXTURE = Identifier.of("starhud", "hud/durability_bar.png");
    private static final Identifier DURABILITY_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/durability_background.png");

    private static final Identifier ITEM_DURABILITY_TEXTURE = Identifier.of("starhud", "hud/big_durability_bar.png");
    private static final Identifier ITEM_DURABILITY_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/big_durability_background.png");

    // 10 bars + 9 gaps.
    private static final int DURABILITY_WIDTH = (3 * 10) + 9;
    private static final int ITEM_DURABILITY_WIDTH = (5 * 10) + (2 * 9);

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public static void fillRoundedRightSide(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2 - 1, y2, color);
        context.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
    }

    // get the durability "steps" or progress.
    public static int getItemBarStep(ItemStack stack, int maxStep) {
        return MathHelper.clamp(Math.round(maxStep - (float) stack.getDamage() * maxStep / (float) stack.getMaxDamage()), 0, maxStep);
    }

    // color transition from pastel (red to green).
    public static int getItemBarColor(int stackStep, int maxStep) {
        return MathHelper.hsvToRgb(0.35F * stackStep / (float) maxStep, 0.45F, 0.95F);
    }

    public static Box renderDurabilityHUD(DrawContext context, Identifier ICON, ItemStack stack, int x, int y, float v, int textureWidth, int textureHeight, int color, boolean drawBar, boolean drawItem, GrowthDirectionX textureGrowth) {
        if (drawItem) {
            return renderItemDurability(context, stack, x , y, drawBar, textureGrowth);
        } else {
            return renderDurability(context, ICON, stack, x, y, v, textureWidth, textureHeight, color, drawBar, textureGrowth);
        }
    }

    public static Box renderItemDurability(DrawContext context, ItemStack stack, int x, int y, boolean drawBar, GrowthDirectionX textureGrowth) {
        if (drawBar) {
            return renderItemDurabilityBar(context, stack, x, y,textureGrowth);
        } else {
            return renderItemDurabilityNumber(context, stack, x, y, textureGrowth);
        }
    }

    public static Box renderDurability(DrawContext context, Identifier ICON, ItemStack stack, int x, int y, float v, int textureWidth, int textureHeight, int color, boolean drawBar, GrowthDirectionX textureGrowth) {
        if (drawBar) {
            return renderDurabilityBar(context, ICON, stack, x, y, v, textureWidth, textureHeight, color, textureGrowth);
        } else {
            return renderDurabilityNumber(context, ICON, stack, x, y, v, textureWidth, textureHeight, color, textureGrowth);
        }
    }

    public static Box renderItemDurabilityBar(DrawContext context, ItemStack stack, int x, int y, GrowthDirectionX textureGrowth) {
        int step = getItemBarStep(stack, 10);
        int durabilityColor = getItemBarColor(step, 10) | 0xFF000000;

        x -= textureGrowth.getGrowthDirection(ITEM_DURABILITY_WIDTH);

        // draw the durability background and item
        RenderUtils.drawTextureHUD(context, ITEM_DURABILITY_BACKGROUND_TEXTURE, x, y, 0.0F, 0.0F, 101, 22, 101, 22);
        context.drawItem(stack, x + 3, y + 3);

        if (step != 0) RenderUtils.drawTextureHUD(context, ITEM_DURABILITY_TEXTURE, x + 28, y + 4, 0, 0, 7 * step, 14, 70, 14, durabilityColor);

        return new Box(x, y, 101, 22, durabilityColor);
    }

    public static Box renderItemDurabilityNumber(DrawContext context, ItemStack stack, int x, int y, GrowthDirectionX textureGrowth) {
        int damage = stack.getDamage();
        int maxDamage = stack.getMaxDamage();
        int remaining = maxDamage - damage;

        String durability = remaining + "/" + maxDamage;

        int durabilityWidth = CLIENT.textRenderer.getWidth(durability) - 1;

        int textColor = getItemBarColor(remaining, maxDamage) | 0xFF000000;

        x -= textureGrowth.getGrowthDirection(durabilityWidth);

        RenderUtils.drawTextureHUD(context,  ITEM_DURABILITY_BACKGROUND_TEXTURE, x, y, 0.0F, 0.0F, 22, 22, 101, 22);
        context.drawItem(stack, x + 3, y + 3);

        fillRoundedRightSide(context, x + 23,  y, x + 22 + 1 + 5 + durabilityWidth + 5, y + 22, 0x80000000);
        RenderUtils.drawTextHUD(context, durability, x + 22 + 1 + 5, y + 7, textColor, false);

        return new Box(x, y, 22 + 1 + 5 + durabilityWidth + 5, 22, textColor);
    }

    public static Box renderDurabilityBar(DrawContext context, Identifier ICON, ItemStack stack, int x, int y, float v, int textureWidth, int textureHeight, int color, GrowthDirectionX textureGrowth) {
        int step = getItemBarStep(stack, 10);
        int durabilityColor = getItemBarColor(step, 10) | 0xFF000000;

        x -= textureGrowth.getGrowthDirection(DURABILITY_WIDTH);

        // draw the icon
        RenderUtils.drawTextureHUD(context, ICON, x, y, 0.0F, v, 13, 13, textureWidth, textureHeight, color);

        // draw the durability background and steps
        RenderUtils.drawTextureHUD(context, DURABILITY_BACKGROUND_TEXTURE, x + 14, y, 0.0F, 0.0F, 49, 13, 49, 13);
        if (step != 0) RenderUtils.drawTextureHUD(context, DURABILITY_TEXTURE, x + 19, y + 3, 0, 0, 4 * step, 7, 40, 7, durabilityColor);

        return new Box(x, y, 13 + 1 + 49, 13, durabilityColor);
    }

    // example render: ¹²³⁴/₅₆₇₈
    public static Box renderDurabilityNumber(DrawContext context, Identifier ICON, ItemStack stack, int x, int y, float v, int textureWidth, int textureHeight, int color, GrowthDirectionX textureGrowth) {
        int damage = stack.getDamage();
        int maxDamage = stack.getMaxDamage();
        int remaining = maxDamage - damage;

        String remainingStr = Helper.toSuperscript(Integer.toString(remaining)) + '/';
        String maxDamageStr = Helper.toSubscript(Integer.toString(maxDamage));

        int remainingTextWidth = CLIENT.textRenderer.getWidth(remainingStr);
        int maxDamageTextWidth = CLIENT.textRenderer.getWidth(maxDamageStr);

        int textColor = getItemBarColor(remaining, maxDamage) | 0xFF000000;

        x -= textureGrowth.getGrowthDirection(remainingTextWidth + maxDamageTextWidth);

        RenderUtils.drawTextureHUD(context, ICON, x, y, 0.0F, v, 13, 13, textureWidth, textureHeight, color);
        fillRoundedRightSide(context, x + 14,  y, x + 14 + remainingTextWidth + maxDamageTextWidth + 10, y + 13, 0x80000000);

        // this is gore of my comfort character, call drawText twice except the second one has a -1 pixel offset.
        // Minecraft default subscript's font is 1 pixel to deep for my liking, so I have to shift them up.
        RenderUtils.drawTextHUD(context, remainingStr, x + 14 + 5, y + 3, textColor, false);
        RenderUtils.drawTextHUD(context, maxDamageStr, x + 14 + 5 + remainingTextWidth, y + 3 - 1, textColor, false);

        return new Box(x, y, 14 + remainingTextWidth + maxDamageTextWidth + 10, 13, textColor);
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
        context.drawText(CLIENT.textRenderer, orderedText, x , y, color, shadow);
    }

    public static void drawTextHUD(DrawContext context, OrderedText text, int x, int y, int color, boolean shadow) {
        context.drawText(CLIENT.textRenderer, text, x, y, color, shadow);
    }
}
