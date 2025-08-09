package fin.starhud.hud.implementation;

import fin.starhud.Main;
import fin.starhud.config.hud.EffectSettings;
import fin.starhud.hud.HUDId;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public class NegativeEffectHUD extends AbstractEffectHUD {

    private static final EffectSettings SETTINGS = Main.settings.effectSettings.negativeSettings;

    public NegativeEffectHUD() {
        super(SETTINGS);
    }

    @Override
    public boolean isEffectAllowedToRender(StatusEffect registryEntry) {
        return !registryEntry.isBeneficial();
    }

    @Override
    public String getName() {
        return "Negative Effect HUD";
    }

    @Override
    public String getId() {
        return HUDId.NEGATIVE_EFFECT.toString();
    }
}
