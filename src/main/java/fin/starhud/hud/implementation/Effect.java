package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.hud.EffectSettings;
import fin.starhud.helper.RenderUtils;
import fin.starhud.helper.ScreenAlignmentX;
import fin.starhud.helper.ScreenAlignmentY;
import fin.starhud.helper.StatusEffectAttribute;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// WIP
public class Effect extends AbstractHUD {

    private static final EffectSettings effectSettings = Main.settings.effectSettings;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static final Identifier STATUS_EFFECT_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/effect.png");
    private static final Identifier STATUS_EFFECT_BAR_TEXTURE = Identifier.of("starhud", "hud/effect_bar.png");
    private static final Identifier STATUS_EFFECT_AMBIENT_TEXTURE = Identifier.of("starhud", "hud/effect_ambient.png");

    private static final int STATUS_EFFECT_TEXTURE_WIDTH = 24;
    private static final int STATUS_EFFECT_TEXTURE_HEIGHT = 32;
    private static final int STATUS_EFFECT_BAR_TEXTURE_WIDTH = 21;
    private static final int STATUS_EFFECT_BAR_TEXTURE_HEIGHT = 3;

    private static final Map<StatusEffect, Identifier> STATUS_EFFECT_TEXTURE_MAP = new HashMap<>();

    public Effect() {
        super(effectSettings.base);
    }

    @Override
    public void renderHUD(DrawContext context) {

        // straight up copied from minecraft's own status effect rendering system.

        Collection<StatusEffectInstance> collection = CLIENT.player.getStatusEffects();
        if (collection.isEmpty())
            return;

        int beneficialIndex = 0;
        int harmIndex = 0;

        boolean drawVertical = effectSettings.drawVertical;
        int sameTypeGap = effectSettings.sameTypeGap;
        int differentTypeGap = ((drawVertical && effectSettings.base.originX == ScreenAlignmentX.RIGHT) || (!drawVertical && effectSettings.base.originY == ScreenAlignmentY.BOTTOM)) ? -effectSettings.differentTypeGap :effectSettings.differentTypeGap;

        // if originX = right, invert differentTypeGap
        // if originY = down, invert differentTypeGap

        int beneficialSize = getBeneficialSize();
        int harmSize = collection.size() - beneficialSize;

        int xBeneficial = x - effectSettings.growthDirectionX.getGrowthDirection(getDynamicWidth(true, beneficialSize, harmSize));
        int yBeneficial = y - effectSettings.growthDirectionY.getGrowthDirection(getDynamicHeight(true, beneficialSize, harmSize));

        int xHarm = (beneficialSize == 0 && drawVertical) ? xBeneficial : x - effectSettings.growthDirectionX.getGrowthDirection(getDynamicWidth(false, beneficialSize, harmSize));
        int yHarm = (beneficialSize == 0 && !drawVertical) ? yBeneficial : y - effectSettings.growthDirectionY.getGrowthDirection(getDynamicHeight(false, beneficialSize, harmSize));

        for (StatusEffectInstance statusEffectInstance : collection) {
            if (!statusEffectInstance.shouldShowIcon())
                continue;

            StatusEffect statusEffect = statusEffectInstance.getEffectType();
            StatusEffectAttribute statusEffectAttribute = StatusEffectAttribute.getStatusEffectAttribute(statusEffectInstance);

            int x2;
            int y2;

            if (statusEffect.isBeneficial()) {
                x2 = (xBeneficial) + ((drawVertical ? 0 : sameTypeGap) * beneficialIndex);
                y2 = (yBeneficial) + ((drawVertical ? sameTypeGap : 0) * beneficialIndex);
                ++beneficialIndex;
            } else {
                x2 = (xHarm) + (beneficialSize == 0 ? 0 : (drawVertical ? differentTypeGap : 0)) + ((drawVertical ? 0 : sameTypeGap) * harmIndex);
                y2 = (yHarm) + (beneficialSize == 0 ? 0 : (drawVertical ? 0 : differentTypeGap)) + ((drawVertical ? sameTypeGap : 0) * harmIndex);
                ++harmIndex;
            }

            if (statusEffectInstance.isAmbient()) {

                // draw soft blue outlined background...
                RenderUtils.drawTextureHUD(
                        context,
                        STATUS_EFFECT_AMBIENT_TEXTURE,
                        x2, y2,
                        0, 0,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT,
                        effectSettings.ambientColor | 0xFF000000
                );

            } else {

                // draw background
                RenderUtils.drawTextureHUD(
                        context,
                        STATUS_EFFECT_BACKGROUND_TEXTURE,
                        x2, y2,
                        0, 0,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT,
                        STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT
                );
            }

            int duration = statusEffectInstance.getDuration();

            int step, color;
            if (statusEffectInstance.isInfinite()) {
                step = 7;
                color = effectSettings.infiniteColor | 0xFF000000;
            } else {
                int maxDuration = statusEffectAttribute.maxDuration();

                step = Helper.getStep(duration, maxDuration, 7);
                color = RenderUtils.getItemBarColor(step, 7) | 0xFF000000;
            }

            // draw timer bar
            RenderUtils.drawTextureHUD(
                    context,
                    STATUS_EFFECT_BAR_TEXTURE,
                    x2 + 2, y2 + 27,
                    0, 0,
                    3 * step, STATUS_EFFECT_BAR_TEXTURE_HEIGHT,
                    STATUS_EFFECT_BAR_TEXTURE_WIDTH, STATUS_EFFECT_BAR_TEXTURE_HEIGHT,
                    color
            );

            float alpha = 1.0F;
            if (duration <= 200 && !statusEffectInstance.isInfinite()) { // minecraft's status effect blinking.
                int n = 10 - duration / 20;
                alpha = MathHelper.clamp((float)duration / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos((float)duration * (float)Math.PI / 5.0F) * MathHelper.clamp((float)n / 10.0F * 0.25F, 0.0F, 0.25F);
                alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
            }

            // draw effect texture.
            RenderUtils.drawTextureHUD(
                    context,
                    getStatusEffectTexture(statusEffect),
                    x2 + 3, y2 + 3,
                    0,0,
                    18, 18,
                    18,18,
                    (Math.round(alpha * 255) << 24) | 0xFFFFFF
            );

            // draw amplifier text.
            int amplifier = statusEffectAttribute.amplifier() + 1;
            if (amplifier == 1)
                continue;

            String amplifierStr = Helper.toSubscript(Integer.toString(amplifier));

            RenderUtils.drawTextHUD(
                    context,
                    amplifierStr,
                    x2 + 3 + 18 - CLIENT.textRenderer.getWidth(amplifierStr) + 1, y2 + 2 + 18 - 7,
                    0xFFFFFFFF,
                    true
            );

        }
    }

    public int getDynamicWidth(boolean isBeneficial, int beneficialSize, int harmSize) {
         return effectSettings.drawVertical ? STATUS_EFFECT_TEXTURE_WIDTH : ((isBeneficial ? beneficialSize : harmSize) * effectSettings.sameTypeGap);
    }

    public int getDynamicHeight(boolean isBeneficial, int beneficialSize, int harmSize) {
        return effectSettings.drawVertical ? ((isBeneficial ? beneficialSize : harmSize) * effectSettings.sameTypeGap) : STATUS_EFFECT_TEXTURE_HEIGHT;
    }

    public static int getBeneficialSize() {
        int size = 0;
        for (StatusEffectInstance collection : CLIENT.player.getStatusEffects()) {
            if (collection.getEffectType().isBeneficial())
                ++size;
        }
        return size;
    }

    // 0 because the width is dependent to how many status effect are present.

    @Override
    public int getBaseHUDWidth() {
        return 0;
    }

    @Override
    public int getBaseHUDHeight() {
        return 0;
    }

    public static Identifier getStatusEffectTexture(StatusEffect effect) {
        return STATUS_EFFECT_TEXTURE_MAP.computeIfAbsent(effect, e -> {
            Identifier id = Registries.STATUS_EFFECT.getId(e); // e.g. minecraft:fire_resistance
            return new Identifier(id.getNamespace(), "textures/mob_effect/" + id.getPath() + ".png");
        });
    }

}
