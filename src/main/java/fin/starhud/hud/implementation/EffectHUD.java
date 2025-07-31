package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.hud.EffectSettings;
import fin.starhud.helper.*;
import fin.starhud.hud.AbstractHUD;
import fin.starhud.hud.HUDId;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EffectHUD extends AbstractHUD {

    private static final EffectSettings effectSettings = Main.settings.effectSettings;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static final Identifier STATUS_EFFECT_BACKGROUND_TEXTURE = Identifier.of("starhud", "hud/effect.png");
    private static final Identifier STATUS_EFFECT_BAR_TEXTURE = Identifier.of("starhud", "hud/effect_bar.png");
    private static final Identifier STATUS_EFFECT_AMBIENT_TEXTURE = Identifier.of("starhud", "hud/effect_ambient.png");

    private static final int STATUS_EFFECT_TEXTURE_WIDTH = 24;
    private static final int STATUS_EFFECT_TEXTURE_HEIGHT = 32;
    private static final int STATUS_EFFECT_BAR_TEXTURE_WIDTH = 21;
    private static final int STATUS_EFFECT_BAR_TEXTURE_HEIGHT = 3;

    private static final Map<RegistryEntry<StatusEffect>, Identifier> STATUS_EFFECT_TEXTURE_MAP = new HashMap<>();

    private static final Box tempBox = new Box(0,0);
    private static int cachedSize = -1;
    private static boolean needBoxUpdate = true;

    public static int beneficialSize;
    public static int harmSize;

    public EffectHUD() {
        super(effectSettings.base);
    }

    @Override
    public String getName() {
        return "Status Effect HUD";
    }

    @Override
    public String getId() {
        return HUDId.EFFECT.getString();
    }

    @Override
    public boolean shouldRender() {
        return super.shouldRender()
                && !CLIENT.player.getStatusEffects().isEmpty();
    }

    @Override
    public void update() {
        super.update();

        needBoxUpdate = true;
        super.boundingBox.setEmpty(true);
    }

    @Override
    public boolean collectHUDInformation() {
        return true; // special case: status effect is silly so data collection is in the rendering logic for now...
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {

        // straight up copied from minecraft's own status effect rendering system.
        // but with 20x more mess!!!!

        Collection<StatusEffectInstance> collection = CLIENT.player.getStatusEffects();

        int beneficialIndex = 0;
        int harmIndex = 0;

        boolean drawVertical = effectSettings.drawVertical;

        // sameTypeGap = the gap between each beneficial / harm effect.
        int sameTypeGap = getSameTypeGap();

        /* differentTypeGap = the gap between beneficial and harm effect.
        * if HUD on the right screen and is drawn Vertically, We change the differentTypeGap from going right, to left.so that the harm effect hud does not go out of screen
        * if HUD on the bottom screen and is drawn horizontally, We change the differentTypeGap from going down, to up. so that the harm effect hud does not go out of screen
        * */
        int differentTypeGap = ((drawVertical && effectSettings.base.originX == ScreenAlignmentX.RIGHT) || (!drawVertical && effectSettings.base.originY == ScreenAlignmentY.BOTTOM)) ? -(getDifferentTypeGap()) : (getDifferentTypeGap());

        int effectSize = collection.size();
        beneficialSize = getBeneficialSize();
        harmSize = effectSize - beneficialSize;

        // xBeneficial, yBeneficial = Starting point for beneficial effect HUD.
        int xBeneficial = x - getGrowthDirectionHorizontal(getDynamicWidth(true, beneficialSize, harmSize));
        int yBeneficial = y - getGrowthDirectionVertical(getDynamicHeight(true, beneficialSize, harmSize));

        // xHarm, yHarm = Starting point for harm effect HUD.
        // this is just a way to say
        // "if the beneficial effect is empty, we place harm effect in the same place as beneficial effect, yes, replacing its position"
        int xHarm = (beneficialSize == 0 && drawVertical) ? xBeneficial :
                x - getGrowthDirectionHorizontal(getDynamicWidth(false, beneficialSize, harmSize));
        int yHarm = (beneficialSize == 0 && !drawVertical) ? yBeneficial :
                y - getGrowthDirectionVertical(getDynamicHeight(false, beneficialSize, harmSize));

        boolean shouldBoxUpdate = (needBoxUpdate || cachedSize != StatusEffectAttribute.getStatusEffectAttributeMap().size());

        if (shouldBoxUpdate)
            cachedSize = StatusEffectAttribute.getStatusEffectAttributeMap().size();

        for (StatusEffectInstance statusEffectInstance : collection) {
            if (!statusEffectInstance.shouldShowIcon())
                continue;

            RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();
            StatusEffectAttribute statusEffectAttribute = StatusEffectAttribute.getStatusEffectAttribute(statusEffectInstance);

            int x2;
            int y2;

            if (registryEntry.value().isBeneficial()) {

                // if the hud is drawn vertically, we definitely do not want to move the beneficial effect horizontally.
                x2 = (xBeneficial) + ((drawVertical ? 0 : sameTypeGap) * beneficialIndex);

                // if the hud is drawn horizontally, we definitely do not want to move the beneficial effect vertically.
                y2 = (yBeneficial) + ((drawVertical ? sameTypeGap : 0) * beneficialIndex);

                ++beneficialIndex;

            } else {

                x2 = (xHarm)
                        // if beneficial is empty, we replace the position to harm effect. else we shift the harm effect hud accordingly.
                        + (beneficialSize == 0 ? 0 : (drawVertical ? differentTypeGap : 0))
                        // if hud is drawn vertically, we do not want to move the effect horizontally.
                        + ((drawVertical ? 0 : sameTypeGap) * harmIndex);

                y2 = (yHarm)
                        // if beneficial is empty, we replace the position to harm effect. else we shift the harm effect hud accordingly.
                        + (beneficialSize == 0 ? 0 : (drawVertical ? 0 : differentTypeGap))
                        // if hud is drawn vertically, we do not want to move the effect horizontally.
                        + ((drawVertical ? sameTypeGap : 0) * harmIndex);

                ++harmIndex;
            }

            // Final State: x2 and y2 contains the correct placement for the effect HUD, ready to be drawn.

            if (shouldBoxUpdate) {
                tempBox.setBoundingBox(x2, y2, STATUS_EFFECT_TEXTURE_WIDTH, STATUS_EFFECT_TEXTURE_HEIGHT);
                if (super.boundingBox.isEmpty())
                    super.boundingBox.setBoundingBox(tempBox.getX(), tempBox.getY(), tempBox.getWidth(), tempBox.getHeight(), effectSettings.ambientColor | 0xFF000000);
                else
                    super.boundingBox.mergeWith(tempBox);
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
                color = (effectSettings.useEffectColor ? registryEntry.value().getColor() : AbstractDurabilityHUD.getItemBarColor(step, 7)) | 0xFF000000;
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
                    getStatusEffectTexture(registryEntry),
                    x2 + 3, y2 + 3,
                    0,0,
                    18, 18,
                    18,18,
                    ColorHelper.getWhite(alpha)
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

        needBoxUpdate = false;
        return true;
    }

    public int getDynamicWidth(boolean isBeneficial, int beneficialSize, int harmSize) {
                 // if we draw the HUD vertically, essentially the width should be the texture width
         return effectSettings.drawVertical ? STATUS_EFFECT_TEXTURE_WIDTH
                 // else, the width should be the whole column of Effect HUDs.
                 : ((isBeneficial ? beneficialSize : harmSize) * getSameTypeGap()) - effectSettings.sameTypeGap;
    }

    public int getDynamicHeight(boolean isBeneficial, int beneficialSize, int harmSize) {
                // if the HUD is drawn Vertically, the Height should be the whole row of Effect HUDs
        return effectSettings.drawVertical ? ((isBeneficial ? beneficialSize : harmSize) * getSameTypeGap()) - effectSettings.sameTypeGap
                // else, the height is just the same as the texture height.
                : STATUS_EFFECT_TEXTURE_HEIGHT;
    }

    public static int getBeneficialSize() {
        int size = 0;
        for (StatusEffectInstance collection : CLIENT.player.getStatusEffects()) {
            if (collection.getEffectType().value().isBeneficial())
                ++size;
        }
        return size;
    }

    public int getSameTypeGap() {
        return (effectSettings.drawVertical ? STATUS_EFFECT_TEXTURE_HEIGHT : STATUS_EFFECT_TEXTURE_WIDTH) + effectSettings.sameTypeGap;
    }

    public int getDifferentTypeGap() {
        return (effectSettings.drawVertical ? STATUS_EFFECT_TEXTURE_WIDTH : STATUS_EFFECT_TEXTURE_HEIGHT) + effectSettings.differentTypeGap;
    }

    public static Identifier getStatusEffectTexture(RegistryEntry<StatusEffect> effect) {
        return STATUS_EFFECT_TEXTURE_MAP.computeIfAbsent(
                effect,
                e -> e.getKey()
                        .map(RegistryKey::getValue)
                        .map(id -> Identifier.of(id.getNamespace(), "textures/mob_effect/" + id.getPath() + ".png"))
                        .orElseGet(MissingSprite::getMissingSpriteId)
        );
    }

}
