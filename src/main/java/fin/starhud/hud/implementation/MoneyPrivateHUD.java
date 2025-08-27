package fin.starhud.hud.implementation;

import fin.starhud.Main;
import fin.starhud.config.hud.MoneyPrivateSettings;
import fin.starhud.config.hud.MoneyTeamSettings;
import fin.starhud.helper.HUDDisplayMode;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import fin.starhud.hud.HUDId;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class MoneyPrivateHUD extends AbstractHUD {

    private static final MoneyPrivateSettings MONEY_PRIVATE_SETTING = Main.settings.moneySettings.moneyPrivateSetting;
    private static final MoneyTeamSettings MONEY_TEAM_SETTING = Main.settings.moneySettings.moneyTeamSetting;
    private static final Identifier MONEY_TEXTURE = Identifier.of("starhud", "hud/money.png");

    private static final int TEXTURE_WIDTH = 13;
    private static final int TEXTURE_HEIGHT = 13 * 2;
    private static final int ICON_WIDTH = 13;
    private static final int ICON_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    private static String cachedPrivateMoneyString = null;
    private static String cachedTeamMoneyString = null;
    private static long cachedMinute = -1;

    private static int cachedPrivateColor = 0xFFFFFFFF;

    public MoneyPrivateHUD() {
        super(MONEY_PRIVATE_SETTING.base);
    }

    @Override
    public String getName() {
        return "Money HUD";
    }

    @Override
    public String getId() { return HUDId.MONEYPRIVATE.toString(); }

    private HUDDisplayMode displayMode;

    @Override
    public boolean collectHUDInformation() {
        long currentTime = System.currentTimeMillis();
        long minute = currentTime / 60000;

        if (minute != cachedMinute) {
            cachedMinute = minute;

            if (MONEY_PRIVATE_SETTING.privateValue != null && MONEY_TEAM_SETTING.teamValue != null) {
                cachedTeamMoneyString = MoneyTeamHUD.formatMoney(MONEY_TEAM_SETTING.teamValue);
                cachedPrivateMoneyString = formatMoney(MONEY_PRIVATE_SETTING.privateValue);
                cachedPrivateColor = getBalanceColor(MONEY_PRIVATE_SETTING.privateValue);
            }
        }
        displayMode = getSettings().getDisplayMode();

        int width;
        if(CLIENT.textRenderer.getWidth(cachedPrivateMoneyString) > CLIENT.textRenderer.getWidth(cachedTeamMoneyString))
        {
            width = displayMode.calculateWidth(ICON_WIDTH, CLIENT.textRenderer.getWidth(cachedPrivateMoneyString));
        }
        else width = displayMode.calculateWidth(ICON_WIDTH, CLIENT.textRenderer.getWidth(cachedTeamMoneyString));

        setWidthHeightColor(width, ICON_HEIGHT, 0xFF000000);

        return cachedPrivateMoneyString != null;
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y, boolean drawBackground) {
        int w = getWidth();
        int h = getHeight();

        return RenderUtils.drawSmallHUD(
                context,
                cachedPrivateMoneyString,
                x, y,
                w, h,
                MONEY_TEXTURE,
                0.0F, -0.5F,
                TEXTURE_WIDTH, TEXTURE_HEIGHT,
                ICON_WIDTH, ICON_HEIGHT,
                cachedPrivateColor,
                0xFFFFFFFF,
                displayMode,
                drawBackground
        );
    }

    private static int getBalanceColor(int balance) {
        if (balance == 0) return 0xEA4141;
        return 0x01FC41;
    }

    static String formatMoney(int money) {
        int ab = money / 9;
        StringBuilder result = new StringBuilder();

        if (money < 9) {
            return money + "А";
        }
        if(!MONEY_PRIVATE_SETTING.stackFormat) {
            if (MONEY_PRIVATE_SETTING.diamondsDisplay) {
                return ab + "АБ(" + money + "А)";
            } else return ab + "АБ";
        }
        else {
            if (ab > 64) {
                int stack = money / 576;
                int diam = money % 576;

                int kl_ab = diam / 9;
                int kl_a = diam % 9;

                result.append(stack).append("x64");
                if (kl_ab > 0 || kl_a > 0) {
                    result.append("+");
                    result.append(kl_ab).append("АБ");
                } else result.append("АБ");
                if(MONEY_PRIVATE_SETTING.diamondsDisplay) {
                    result.append("(").append(money).append("А)");
                }
            }
            else return ab + "АБ";
        }
        return result.toString();
    }

    @Override
    public void update() {
        super.update();
        cachedMinute = -1;
        cachedPrivateColor = -1;
    }
}
