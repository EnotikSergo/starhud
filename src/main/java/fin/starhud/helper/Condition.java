package fin.starhud.helper;

import fin.starhud.Helper;

public enum Condition {
    DEBUG_HUD_OPENED,
    CHAT_HUD_OPENED,
    BOSSBAR_SHOWN,
    SCOREBOARD_SHOWN,
    STATUS_EFFECT_SHOWN,
    HARM_EFFECT_SHOWN,
    OFFHAND_SHOWN;

    public boolean isConditionMet() {
        return switch (this) {
            case DEBUG_HUD_OPENED -> Helper.isDebugHUDOpen();
            case CHAT_HUD_OPENED -> Helper.isChatFocused();
            case BOSSBAR_SHOWN -> Helper.isBossBarShown();
            case SCOREBOARD_SHOWN -> Helper.isScoreBoardShown();
            case STATUS_EFFECT_SHOWN -> Helper.isStatusEffectOverlayShown();
            case HARM_EFFECT_SHOWN -> Helper.isHarmEffectOverlayShown();
            case OFFHAND_SHOWN -> Helper.isOffHandOverlayShown();
        };
    }
}
