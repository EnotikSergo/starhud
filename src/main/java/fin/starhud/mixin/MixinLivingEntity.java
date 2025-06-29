package fin.starhud.mixin;

import fin.starhud.helper.StatusEffectAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    // this is the way to ENSURE that for every change in status effect, the old value is removed.
    // I don't know if this is safe tho...

    @Inject(method = "setStatusEffect", at = @At("HEAD"))
    private void onSetStatusEffect(StatusEffectInstance effect, Entity source, CallbackInfo ci) {
        StatusEffectAttribute statusEffectAttribute = StatusEffectAttribute.getStatusEffectAttribute(effect);

        if (StatusEffectAttribute.shouldUpdate(effect, statusEffectAttribute))
            StatusEffectAttribute.updateStatusEffectAttribute(effect.getEffectType(), effect.getDuration(), effect.getAmplifier(), effect.isAmbient());
    }

    // using removeStatusEffectInternal() instead of removeStatusEffect() because the former worked and the latter didn't, I don't know why.
    // remove status effect from the player status effect list. Reason is just to delete unused effect from the map.
    @Inject(method = "removeStatusEffectInternal", at = @At("RETURN"))
    private void onStatusEffectRemoved(StatusEffect type, CallbackInfoReturnable<StatusEffectInstance> cir) {
        StatusEffectAttribute.removeStatusEffectAttribute(type);
    }
}
