package fin.starhud;

import fin.starhud.config.Settings;
import fin.starhud.config.hud.MoneyPrivateSettings;
import fin.starhud.config.hud.MoneyTeamSettings;
import fin.starhud.hud.HUDComponent;
import fin.starhud.init.ConfigInit;
import fin.starhud.init.EventInit;
import fin.starhud.init.KeybindInit;
import fin.starhud.network.MoneyDataPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.minecraft.client.option.KeyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("starhud");

    public static Settings settings;

    public static KeyBinding openEditHUDKey;
    public static KeyBinding toggleHUDKey;

    @Override
    public void onInitializeClient() {
        ConfigInit.init();
        KeybindInit.init();
        EventInit.init();
        HUDComponent.getInstance().init();

        ServerLoginConnectionEvents.INIT.register((serverLoginNetworkHandler, minecraftServer) -> {
            MoneyPrivateSettings.privateValue = null;
            MoneyTeamSettings.teamValue = null;
        });
        PayloadTypeRegistry.playS2C().register(MoneyDataPayload.ID, MoneyDataPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(MoneyDataPayload.ID, (payload, context) -> {
            MoneyPrivateSettings.privateValue = payload.privateBalance();
            MoneyTeamSettings.teamValue = payload.teamBalance().orElse(null);
        });
    }

}
