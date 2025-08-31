package fin.starhud;

import fin.starhud.config.Settings;
import fin.starhud.hud.shield.Money;
import fin.starhud.network.MoneyDataPayload;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;

public class Main implements ClientModInitializer {

    public static Settings settings;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(Settings.class, GsonConfigSerializer::new);
        settings = AutoConfig.getConfigHolder(Settings.class).getConfig();
        ServerLoginConnectionEvents.INIT.register((serverLoginNetworkHandler, minecraftServer) -> {
            Money.privateValue = null;
            Money.teamValue = null;
        });
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((minecraftClient, clientWorld) -> {
            Money.privateValue = null;
            Money.teamValue = null;
        });
        PayloadTypeRegistry.playS2C().register(MoneyDataPayload.ID, MoneyDataPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(MoneyDataPayload.ID, (payload, context) -> {
            Money.privateValue = payload.privateBalance();
            Money.teamValue = payload.teamBalance().orElse(null);
        });
    }
}
