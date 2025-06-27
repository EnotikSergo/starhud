package fin.starhud.hud.shield;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class Money {

    private static final Settings.MoneySettings moneySettings = Main.settings.moneySettings;

    private static final Identifier MONEY_TEXTURE = Identifier.of("starhud", "hud/money.png");

    private static final int width = 63;
    private static final int height = 13;

    public static Integer privateValue = null;
    public static Integer teamValue = null;

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void renderMoneyHUD(DrawContext context) {
        if ((moneySettings.hideOn.f3 && Helper.isDebugHUDOpen()) || (moneySettings.hideOn.chat && Helper.isChatFocused())) return;
        if (client.isInSingleplayer()) return;
        if (privateValue==null || teamValue==null) return;
        if (!moneySettings.moneyCategories.privateAccount && !moneySettings.moneyCategories.teamAccount) return;

        context.getMatrices().push();
        Helper.setHUDScale(context, moneySettings.scale);
        int yOffset = 0;
        if (moneySettings.moneyCategories.privateAccount) {
            renderLayer(context, 0, privateValue, yOffset);
            yOffset+=height+3;
        }
        if (moneySettings.moneyCategories.teamAccount) renderLayer(context, 1, teamValue, yOffset);

        context.getMatrices().pop();
    }

    private static void renderLayer(DrawContext context, int layer, int value, int yOffset){
        int x = Helper.calculatePositionX(moneySettings.x, moneySettings.originX, width, moneySettings.scale);
        int y = Helper.calculatePositionY(moneySettings.y, moneySettings.originY, height, moneySettings.scale)+yOffset;

        int color = getBalanceColor(value);
        context.drawTexture(RenderLayer::getGuiTextured, MONEY_TEXTURE, x, y, 0.0F, layer * 13, width, height, width, height * 2, layer==0?0xFFFFFFFF:(0x0CE8DB | 0xFF000000));//color |
//        context.drawTexture(RenderLayer::getGuiTextured, PING_TEXTURE, x, y, 0.0F, step * 13, width, height, width, height * 2, color);

        context.drawText(client.textRenderer, formatMoney(value), x + 19, y + 3, color, false);
    }

    public static int getBalanceColor(int balance) {
        if (balance==0) return 0xEA4141;
        return 0x01FC41;
    }

    private static String formatMoney(int money) {
        int ab = money / 9;
        if (ab >= 1000) return ab/1000+"k"+" АБ";
        if (ab >= 10) return ab+" АБ";
        return String.valueOf(money);
    }
}
