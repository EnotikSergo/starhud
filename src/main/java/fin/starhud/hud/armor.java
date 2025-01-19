package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class armor {

    private static Settings.ArmorSettings armor = Main.settings.armorSettings;

    private static final Identifier ARMOR_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/armor.png");
    private static final Identifier ARMOR_DURABILITY_TEXTURE = Identifier.of("starhud", "hud/armor_icons.png");

    private static final int[] X_OFFSETS = new int[4];
    private static final int[] Y_OFFSETS = new int[4];
    private static final boolean[] SHOULD_RENDER = new boolean[4];

    private static final int width = 63;
    private static final int height = 13;

    public static void renderArmorHUD(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if ((armor.hideOn.f3 && Helper.isDebugHUDOpen()) || (armor.hideOn.chat && Helper.isChatFocused())) return;

        initArmorConfiguration();

        int x = Helper.calculatePositionX(armor.x, armor.originX, client.getWindow(), width, armor.scale);
        int y = Helper.calculatePositionY(armor.y, armor.originY, client.getWindow(), height, armor.scale);
        
        int i = 3;

        context.getMatrices().push();
        Helper.setHUDScale(context, client.getWindow(), armor.scale);

        // for each armor pieces
        for (ItemStack armor : client.player.getArmorItems()) {
            if (SHOULD_RENDER[i] && !armor.isEmpty() && armor.isDamageable()) {
                renderArmorPieces(context, armor, x + X_OFFSETS[i], y + Y_OFFSETS[i], width, height, 14 * i);
            }
            --i;
        }

        context.getMatrices().pop();
    }

    public static void renderArmorPieces(DrawContext context, ItemStack armor, int x, int y, int width, int height, int gap) {
        int step = getItemBarStep(armor);
        int color = getItemBarColor(step) | 0xFF000000;
        // draw the background
        context.drawTexture(RenderLayer::getGuiTextured, ARMOR_BACKGROUND_TEXTURE, x, y, 0, gap, width, height, width ,55);
        // draw the information
        context.drawTexture(RenderLayer::getGuiTextured, ARMOR_DURABILITY_TEXTURE, x + 19, y + 3, 0, 0, (4 * step), 7, 40, 7, color);
    }

    // get the durability "steps" or progress.
    public static int getItemBarStep(ItemStack stack) {
        return MathHelper.clamp(Math.round(10 - (float)stack.getDamage() * 10 / (float)stack.getMaxDamage()), 0, 10);
    }

    // color transition from pastel (red to green).
    public static int getItemBarColor(int stackStep) {
        return MathHelper.hsvToRgb(0.35F * stackStep / 10.0F, 0.45F, 0.95F);
    }

    private static void initArmorConfiguration() {
        X_OFFSETS[0] = armor.helmet.xOffset;        Y_OFFSETS[0] = armor.helmet.yOffset;
        X_OFFSETS[1] = armor.chestplate.xOffset;    Y_OFFSETS[1] = armor.chestplate.yOffset;
        X_OFFSETS[2] = armor.leggings.xOffset;      Y_OFFSETS[2] = armor.leggings.yOffset;
        X_OFFSETS[3] = armor.boots.xOffset;         Y_OFFSETS[3] = armor.boots.yOffset;

        SHOULD_RENDER[0] = armor.helmet.shouldRender;
        SHOULD_RENDER[1] = armor.chestplate.shouldRender;
        SHOULD_RENDER[2] = armor.leggings.shouldRender;
        SHOULD_RENDER[3] = armor.boots.shouldRender;
    }

}