package fin.starhud.hud.implementation;

import fin.starhud.Main;
import fin.starhud.config.hud.ArmorSettings;
import fin.starhud.helper.GrowthDirectionX;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Armor extends AbstractHUD {

    private static final ArmorSettings ARMOR_SETTINGS = Main.settings.armorSettings;

    private static final Identifier ARMOR_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/armor.png");

    private static final int[] X_OFFSETS = new int[4];
    private static final int[] Y_OFFSETS = new int[4];
    private static final boolean[] SHOULD_RENDER = new boolean[4];
    private static final boolean[] DRAW_BAR = new boolean[4];
    private static final GrowthDirectionX[] TEXTURE_GROWTH = new GrowthDirectionX[4];

    private static final int TEXTURE_WIDTH = 13 + 1 + 5 + 5;
    private static final int TEXTURE_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public Armor() {
        super(ARMOR_SETTINGS.base);
    }

    @Override
    public int getBaseHUDWidth() {
        return TEXTURE_WIDTH;
    }

    @Override
    public int getBaseHUDHeight() {
        return TEXTURE_HEIGHT;
    }

    @Override
    public void renderHUD(DrawContext context) {
        int armorIndex = 3;
        for (ItemStack armor : CLIENT.player.getArmorItems()) {

            if (SHOULD_RENDER[armorIndex] && !armor.isEmpty() && armor.isDamageable()) {
                RenderUtils.renderDurabilityHUD(
                        context,
                        ARMOR_BACKGROUND_TEXTURE,
                        armor,
                        x + X_OFFSETS[armorIndex],
                        y + Y_OFFSETS[armorIndex],
                        14 * armorIndex,
                        13,
                        TEXTURE_HEIGHT * 4 + 3,
                        0xFFFFFFFF,
                        DRAW_BAR[armorIndex],
                        TEXTURE_GROWTH[armorIndex]
                );
            }

            --armorIndex;
        }
    }

    @Override
    public void update() {
        updateX();
        updateY();

        X_OFFSETS[0] = ARMOR_SETTINGS.helmet.xOffset;
        Y_OFFSETS[0] = ARMOR_SETTINGS.helmet.yOffset;
        X_OFFSETS[1] = ARMOR_SETTINGS.chestplate.xOffset;
        Y_OFFSETS[1] = ARMOR_SETTINGS.chestplate.yOffset;

        X_OFFSETS[2] = ARMOR_SETTINGS.leggings.xOffset;
        Y_OFFSETS[2] = ARMOR_SETTINGS.leggings.yOffset;
        X_OFFSETS[3] = ARMOR_SETTINGS.boots.xOffset;
        Y_OFFSETS[3] = ARMOR_SETTINGS.boots.yOffset;

        SHOULD_RENDER[0] = ARMOR_SETTINGS.helmet.shouldRender;
        SHOULD_RENDER[1] = ARMOR_SETTINGS.chestplate.shouldRender;
        SHOULD_RENDER[2] = ARMOR_SETTINGS.leggings.shouldRender;
        SHOULD_RENDER[3] = ARMOR_SETTINGS.boots.shouldRender;

        DRAW_BAR[0] = ARMOR_SETTINGS.helmet.drawBar;
        DRAW_BAR[1] = ARMOR_SETTINGS.chestplate.drawBar;
        DRAW_BAR[2] = ARMOR_SETTINGS.leggings.drawBar;
        DRAW_BAR[3] = ARMOR_SETTINGS.boots.drawBar;

        TEXTURE_GROWTH[0] = ARMOR_SETTINGS.helmet.textureGrowth;
        TEXTURE_GROWTH[1] = ARMOR_SETTINGS.chestplate.textureGrowth;
        TEXTURE_GROWTH[2] = ARMOR_SETTINGS.leggings.textureGrowth;
        TEXTURE_GROWTH[3] = ARMOR_SETTINGS.boots.textureGrowth;

    }
}
