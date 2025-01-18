package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class direction {

    private static Settings.DirectionSettings direction = Main.settings.directionSettings;

    private static final Identifier DIRECTION_TEXTURE = Identifier.of("starhud", "hud/direction.png");
    private static final Identifier DIRECTION_INCLUDE_ORDINAL_TEXTURE = Identifier.of("starhud", "hud/direction_ordinal.png");

    private static final int height = 13;

    private static boolean LAST_UPDATED_includeOrdinal = direction.includeOrdinal;

    private static int width = LAST_UPDATED_includeOrdinal ? 61 : 55;
    private static int iconAmount = LAST_UPDATED_includeOrdinal ? 8 : 4;
    private static int textX = LAST_UPDATED_includeOrdinal ? 25 : 19;
    private static Identifier texture = LAST_UPDATED_includeOrdinal ? DIRECTION_INCLUDE_ORDINAL_TEXTURE : DIRECTION_TEXTURE;

    public static void renderDirectionHUD(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        Entity playerCamera = client.cameraEntity;

        float yaw = Math.round(MathHelper.wrapDegrees(playerCamera.getYaw()) * 10.0F) / 10.0F;

        boolean includeOrdinal = direction.includeOrdinal;

        // check if user changed this setting
        if (LAST_UPDATED_includeOrdinal != includeOrdinal) {
            LAST_UPDATED_includeOrdinal = includeOrdinal;
            modifyDirectionVariables();
        }

        int icon, color;
        if (includeOrdinal) {
            icon = getOrdinalDirectionIcon(yaw);
            color = getDirectionColor(icon) | 0xFF000000;
        } else {
            icon = getCardinalDirectionIcon(yaw);
            color = getDirectionColor(icon * 2) | 0xFF000000;
        }

        int x = Helper.calculatePositionX(direction.x, direction.originX, client.getWindow(), width, direction.scale);
        int y = Helper.calculatePositionY(direction.y, direction.originY, client.getWindow(), height, direction.scale);

        context.getMatrices().push();
        Helper.setHUDScale(context, client.getWindow(), direction.scale);
        context.drawTexture(RenderLayer::getGuiTextured, texture, x, y, 0.0F, icon * 13, width, height, width, height * iconAmount, color);
        context.drawText(client.textRenderer, Float.toString(yaw), x + textX, y + 3, color, false);
        context.getMatrices().pop();
    }

    private static int getOrdinalDirectionIcon(float yaw) {
        if (-22.5 <= yaw && yaw < 22.5)         return 0;   //south
        else if (22.5 <= yaw && yaw < 67.5)     return 1;   //southwest
        else if (67.5 <= yaw && yaw < 112.5)    return 2;   //west
        else if (112.5 <= yaw && yaw < 157.5)   return 3;   //northwest
        else if (157.5 <= yaw || yaw < -157.5)  return 4;   //north
        else if (-157.5 <= yaw && yaw < -112.5) return 5;   //northeast
        else if (-112.5 <= yaw && yaw < -67.5)  return 6;   //east
        else if (-67.5 <= yaw && yaw < -22.5)   return 7;   //southeast
        else return 0;
    }

    private static int getCardinalDirectionIcon(float yaw) {
        if (-45.0 <= yaw && yaw < 45.0)         return 0;   //south
        else if (45.0 <= yaw && yaw < 135.0)    return 1;   //west
        else if (135.0 <= yaw || yaw < -135.0)  return 2;   //north
        else if (-135.0 <= yaw && yaw < -45.0)  return 3;   //east
        else return 0;
    }

    private static int getDirectionColor(int icon) {
        return switch (icon) {
            case 0 -> direction.directionColor.s;
            case 1 -> direction.directionColor.sw;
            case 2 -> direction.directionColor.w;
            case 3 -> direction.directionColor.nw;
            case 4 -> direction.directionColor.n;
            case 5 -> direction.directionColor.ne;
            case 6 -> direction.directionColor.e;
            case 7 -> direction.directionColor.se;
            default -> 0xFFFFFF;
        };
    }

    private static void modifyDirectionVariables() {
        if (LAST_UPDATED_includeOrdinal) {
            texture = DIRECTION_INCLUDE_ORDINAL_TEXTURE;
            width = 61;
            iconAmount = 8;
            textX = 25;
        } else {
            texture = DIRECTION_TEXTURE;
            width = 55;
            iconAmount = 4;
            textX = 19;
        }
    }
}
