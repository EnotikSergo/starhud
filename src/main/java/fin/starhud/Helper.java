package fin.starhud;

import fin.starhud.mixin.accessor.AccessorBossBarHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.MathHelper;


public class Helper {

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static final char[] superscripts = "⁰¹²³⁴⁵⁶⁷⁸⁹".toCharArray();
    private static final char[] subscripts = "₀₁₂₃₄₅₆₇₈₉".toCharArray();

    // only convert numbers.
    public static String toSuperscript(String str) {
        char[] chars = str.toCharArray();

        int len = str.length();
        for (int i = 0; i < len; ++i) {
            char c = chars[i];

            if (c >= '0' && c <= '9')
                chars[i] = superscripts[c - '0'];
        }

        return String.valueOf(chars);
    }

    public static String toSubscript(String str) {
        char[] chars = str.toCharArray();

        int len = str.length();
        for (int i = 0; i < len; ++i) {
            char c = chars[i];

            if (c >= '0' && c <= '9')
                chars[i] = subscripts[c - '0'];
        }

        return String.valueOf(chars);
    }

    public static String idNameFormatter(String oldString) {

        // trim every character from ':' until first index
        oldString = oldString.substring(oldString.indexOf(':') + 1);

        char[] chars = oldString.toCharArray();

        if (chars.length == 0) return "-";

        chars[0] = Character.toUpperCase(chars[0]);
        for (int i = 1; i < chars.length; ++i) {
            if (chars[i] != '_') continue;

            chars[i] = ' ';

            // capitalize the first character after spaces
            if (i + 1 < chars.length) {
                chars[i + 1] = Character.toUpperCase(chars[i + 1]);
            }
        }

        return new String(chars);
    }

    public static int getStep(int curr, int max, int maxStep) {
        return MathHelper.clamp(Math.round((float) curr * maxStep / (float) max), 0, maxStep);
    }

    public static boolean isChatFocused() {
        return CLIENT.currentScreen instanceof ChatScreen;
    }
    public static boolean isDebugHUDOpen() {
        return CLIENT.options.debugEnabled;
    }

    public static boolean isBossBarShown() {
        return !((AccessorBossBarHud) CLIENT.inGameHud.getBossBarHud()).getBossBars().isEmpty();
    }

    public static boolean isScoreBoardShown() {
        return !CLIENT.world.getScoreboard().getObjectives().isEmpty();
    }

    public static boolean isBeneficialEffectOverlayShown() {
        return CLIENT.player.getStatusEffects().stream()
                .anyMatch(effect -> effect.getEffectType().isBeneficial());
    }

    public static boolean isHarmEffectOverlayShown() {
        return CLIENT.player.getStatusEffects().stream()
                .anyMatch(effect -> !effect.getEffectType().isBeneficial());
    }

    public static boolean isOffHandOverlayShown() {
        return !CLIENT.player.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty();
    }

    public static boolean isStatusBarsShown() {
        return CLIENT.interactionManager.hasStatusBars();
    }

    public static boolean isArmorBarShown() {
        return isStatusBarsShown() && CLIENT.player.getArmor() > 0;
    }

    public static boolean isAirBubbleBarShown() {
        return isStatusBarsShown() && CLIENT.player.isSubmergedIn(FluidTags.WATER) || CLIENT.player.getAir() < CLIENT.player.getMaxAir();
    }
}