package fin.starhud.init;

import fin.starhud.Main;
import fin.starhud.config.GeneralSettings;
import fin.starhud.hud.HUDComponent;
import fin.starhud.screen.EditHUDScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class EventInit {

    private static final GeneralSettings.InGameHUDSettings SETTINGS = Main.settings.generalSettings.inGameSettings;

    public static void init() {

        // register keybinding event, on openEditHUDKey pressed -> move screen to edit hud screen.
        ClientTickEvents.END_CLIENT_TICK.register(EventInit::onOpenEditHUDKeyPressed);
        ClientTickEvents.END_CLIENT_TICK.register(EventInit::onToggleHUDKeyPressed);

        // register hud element into before hotbar. I hope this was safe enough.
        HudRenderCallback.EVENT.register(EventInit::onHUDRender);
    }

    public static void onOpenEditHUDKeyPressed(MinecraftClient client) {
        while (Main.openEditHUDKey.wasPressed()) {
            client.setScreen(new EditHUDScreen(Text.of("Edit HUD"), client.currentScreen));
        }
    }

    public static void onToggleHUDKeyPressed(MinecraftClient client) {
        while (Main.toggleHUDKey.wasPressed()) {
            Main.settings.generalSettings.inGameSettings.disableHUDRendering = !Main.settings.generalSettings.inGameSettings.disableHUDRendering;
        }
    }

    public static void onHUDRender(DrawContext context, RenderTickCounter tickCounter) {
        if (SETTINGS.disableHUDRendering) return;
        if (MinecraftClient.getInstance().options.hudHidden) return;
        if (MinecraftClient.getInstance().currentScreen instanceof EditHUDScreen) return;

        HUDComponent.getInstance().renderAll(context);
    }


}