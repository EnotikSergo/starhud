package fin.objhud.mixin;

import fin.objhud.hud.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHUD {

    @Inject(at = @At("TAIL"), method = "renderHotbar")
    private void renderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        armor.renderArmorHUD(context);
        coordinate.renderCoordinateHUD(context);
        fps.renderFPSHUD(context);
        ping.renderPingHUD(context);
        clock.renderInGameTimeHUD(context);
        clock.renderSystemTimeHUD(context);
        direction.renderDirectionHUD(context);
    }
}
