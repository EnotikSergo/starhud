package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

import java.text.SimpleDateFormat;
import java.util.Date;

public class clock {

    private static final Settings.ClockSettings.ClockInGameSettings clock_ingame = Main.settings.clockSettings.inGameSettings;
    private static final Settings.BaseSettings base_clock_ingame = clock_ingame.base;

    private static final Settings.ClockSettings.ClockSystemSettings clock_system = Main.settings.clockSettings.systemSettings;
    private static final Settings.BaseSettings base_clock_system = clock_system.base;

    private static final Identifier CLOCK_12 = Identifier.of("starhud", "hud/clock_12.png");
    private static final Identifier CLOCK_24 = Identifier.of("starhud", "hud/clock_24.png");

    private static final int width_use12 = 65;
    private static final int width_use24 = 49;
    private static final int height = 13;

    private static String minecraftTimeStr = "";
    private static int cachedMinecraftMinute = -1;

    private static boolean LAST_UPDATED_INGAME_USE12 = clock_ingame.clock.use12Hour;

    private static final MinecraftClient client = MinecraftClient.getInstance();

    private static int x_ingame;
    private static int y_ingame;

    static {
        initClockInGameConfiguration();
        initClockSystemConfiguration();
    }

    private static void renderClockHUD(DrawContext context, Settings.BaseSettings base, Settings.ClockSettings.BaseClockSettings clock, String timeStr, int x, int y, float v, int color) {

        Helper.renderHUD(context, base.scale, () -> {
            if (clock.use12Hour)
                context.drawTexture(RenderLayer::getGuiTextured, CLOCK_12, x, y, 0.0F, v, width_use12, height, width_use12, height * 5, color);
            else
                context.drawTexture(RenderLayer::getGuiTextured, CLOCK_24, x, y, 0.0F, v, width_use24, height, width_use24, height * 5, color);
            context.drawText(client.textRenderer, timeStr, x + 19, y + 3, color, false);
        });

    }

    public static void renderInGameTimeHUD(DrawContext context) {
        if (Helper.IsHideOn(base_clock_ingame.hideOn)) return;

        ClientWorld world = client.world;

        long time = world.getTimeOfDay() % 24000;

        boolean use12Hour = clock_ingame.clock.use12Hour;

        int minutes = (int) ((time % 1000) * 3 / 50);
        int hours = (int) ((time / 1000) + 6) % 24;
        if (minutes != cachedMinecraftMinute || use12Hour != LAST_UPDATED_INGAME_USE12) {
            cachedMinecraftMinute = minutes;
            LAST_UPDATED_INGAME_USE12 = use12Hour;

            minecraftTimeStr = use12Hour ?
                    buildMinecraftCivilianTimeString(hours, minutes):
                    buildMinecraftMilitaryTimeString(hours, minutes);
        }

        int icon = getWeatherOrTime(world);
        int color = getIconColor(icon) | 0xFF000000;

        renderClockHUD(context, base_clock_ingame, clock_ingame.clock, minecraftTimeStr, x_ingame, y_ingame, icon * 13, color);
    }

    private static int getIconColor(int icon) {
        return switch (icon) {
            case 1 -> clock_ingame.color.day;
            case 2 -> clock_ingame.color.night;
            case 3 -> clock_ingame.color.rain;
            case 4 -> clock_ingame.color.thunder;
            default -> 0xFFFFFF;
        };
    }

    private static int getWeatherOrTime(ClientWorld clientWorld) {
        if (clientWorld.isThundering()) return 4;
        else if (clientWorld.isRaining()) return 3;
        else if (clientWorld.isNight()) return 2;
        else return 1;
    }

    private static String buildMinecraftMilitaryTimeString(int hours, int minutes) {
        StringBuilder timeBuilder = new StringBuilder();

        if (hours < 10) timeBuilder.append('0');
        timeBuilder.append(hours).append(':');

        if (minutes < 10) timeBuilder.append('0');
        timeBuilder.append(minutes);

        return timeBuilder.toString();
    }

    private static String buildMinecraftCivilianTimeString(int hours, int minutes) {
        StringBuilder timeBuilder = new StringBuilder();

        String period = hours >= 12 ? " PM" : " AM";

        // 01.00 until 12.59 AM / PM
        hours %= 12;
        if (hours == 0) hours = 12;

        timeBuilder.append(buildMinecraftMilitaryTimeString(hours, minutes)).append(period);

        return timeBuilder.toString();
    }

    private static final SimpleDateFormat militaryTimeFormat = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat civilianTimeFormat = new SimpleDateFormat("hh:mm a");

    private static String systemTimeStr = buildSystemMilitaryTimeString(System.currentTimeMillis());
    private static long cachedSystemMinute = -1;

    private static boolean LAST_UPDATED_SYSTEM_USE12 = clock_system.clock.use12Hour;

    private static int x_system;
    private static int y_system;

    public static void renderSystemTimeHUD(DrawContext context) {
        if (Helper.IsHideOn(base_clock_system.hideOn)) return;

        // update each minute
        long currentTime = System.currentTimeMillis();
        long minute = currentTime / 60000;

        boolean use12Hour = clock_system.clock.use12Hour;

        // update on either a new minute or user updated the config
        if (minute != cachedSystemMinute || use12Hour != LAST_UPDATED_SYSTEM_USE12) {
            cachedSystemMinute = minute;
            LAST_UPDATED_SYSTEM_USE12 = use12Hour;

            systemTimeStr = use12Hour ?
                    buildSystemCivilianTimeString(currentTime):
                    buildSystemMilitaryTimeString(currentTime);
        }

        int color = clock_system.color | 0xFF000000;

        renderClockHUD(context, base_clock_system, clock_system.clock, systemTimeStr, x_system, y_system, 0.0F, color);
    }

    private static String buildSystemMilitaryTimeString(long time) {
        return militaryTimeFormat.format(new Date(time));
    }
    private static String buildSystemCivilianTimeString(long time) {
        return civilianTimeFormat.format(new Date(time));
    }

    public static void initClockInGameConfiguration() {

        if (clock_ingame.clock.use12Hour)
            x_ingame = Helper.calculatePositionX(base_clock_ingame, width_use12);
        else
            x_ingame = Helper.calculatePositionX(base_clock_ingame, width_use24);

        y_ingame = Helper.calculatePositionY(base_clock_ingame, height);

    }

    public static void initClockSystemConfiguration() {

        if (clock_system.clock.use12Hour)
            x_system = Helper.calculatePositionX(base_clock_system, width_use12);
        else
            x_system = Helper.calculatePositionX(base_clock_system, width_use24);

        y_system = Helper.calculatePositionY(base_clock_system, height);
    }
}