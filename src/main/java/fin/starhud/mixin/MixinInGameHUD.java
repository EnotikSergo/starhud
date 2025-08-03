package fin.starhud.mixin;

import fin.starhud.helper.condition.ScoreboardHUD;
import fin.starhud.hud.HUDComponent;
import fin.starhud.hud.HUDId;
import fin.starhud.hud.implementation.NegativeEffectHUD;
import fin.starhud.hud.implementation.PositiveEffectHUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHUD {

    @Unique
    private static final PositiveEffectHUD POSITIVE_EFFECT_HUD = (PositiveEffectHUD) HUDComponent.getInstance().getHUD(HUDId.POSITIVE_EFFECT);

    @Unique
    private static final NegativeEffectHUD NEGATIVE_EFFECT_HUD = (NegativeEffectHUD) HUDComponent.getInstance().getHUD(HUDId.NEGATIVE_EFFECT);

    // Mixin used to override vanilla effect HUD, I'm not sure whether this can be done using HUDElementRegistry
    @Inject(at = @At("HEAD"), method = "renderStatusEffectOverlay", cancellable = true)
    private void renderStatusEffectOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (POSITIVE_EFFECT_HUD.shouldRender() || NEGATIVE_EFFECT_HUD.shouldRender()) ci.cancel();
    }

    @Redirect(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V",
                    ordinal = 1
            )
    )
    private void captureScoreboardFill(DrawContext instance, int x1, int y1, int x2, int y2, int color) {
        ScoreboardHUD.captureBoundingBox(x1, y1 - 9, x2, y2); // -9 due to the first fill call is for header, which has 9 additional offset
        instance.fill(x1, y1, x2 ,y2 , color);
    }
}
