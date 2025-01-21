package fin.starhud.hud;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.Settings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

public class hand {

    private static final Identifier HAND_TEXTURE = Identifier.of("starhud", "hud/hand.png");

    private static final int width_durability = 63;
    private static final int width_count = 47;
    private static final int height = 13;

    private static final Settings.HandSettings.LeftHandSettings leftHand = Main.settings.handSettings.leftHandSettings;

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void renderLeftHandHUD(DrawContext context) {
        if ((leftHand.hideOn.f3 && Helper.isDebugHUDOpen()) || (leftHand.hideOn.chat && Helper.isChatFocused())) return;

        PlayerInventory playerInventory = client.player.getInventory();

        ItemStack item;
        if (client.player.getMainArm() == Arm.LEFT)
            item = playerInventory.getMainHandStack();
        else
            item = playerInventory.offHand.get(0);

        if (item.isEmpty()) return;

        // either draw the durability or the amount of item in the inventory.
        context.getMatrices().push();
        Helper.setHUDScale(context, leftHand.scale);

        if (!leftHand.showCountOnly && item.isDamageable()) {
            int x = Helper.calculatePositionX(leftHand.x, leftHand.originX, width_durability, leftHand.scale);
            int y = Helper.calculatePositionY(leftHand.y, leftHand.originY, height, leftHand.scale);
            Helper.renderItemDurabilityHUD(context, HAND_TEXTURE, item, x, y, 0, width_count, 27, leftHand.color | 0xFF000000);
        } else {
            int x = Helper.calculatePositionX(leftHand.x, leftHand.originX, width_count, leftHand.scale);
            int y = Helper.calculatePositionY(leftHand.y, leftHand.originY, height, leftHand.scale);
            renderItemCountHUD(context, client.textRenderer, playerInventory, item, x, y, 0, leftHand.color | 0xFF000000);
        }

        context.getMatrices().pop();
    }

    private static final Settings.HandSettings.RightHandSettings rightHand = Main.settings.handSettings.rightHandSettings;

    public static void renderRightHandHUD(DrawContext context) {
        if ((rightHand.hideOn.f3 && Helper.isDebugHUDOpen()) || (rightHand.hideOn.chat && Helper.isChatFocused())) return;

        PlayerInventory playerInventory = client.player.getInventory();

        ItemStack item;
        if (client.player.getMainArm() == Arm.LEFT)
            item = playerInventory.offHand.get(0);
        else
            item = playerInventory.getMainHandStack();

        if (item.isEmpty()) return;

        context.getMatrices().push();
        Helper.setHUDScale(context, rightHand.scale);

        // either draw the durability or the amount of item in the inventory.
        if (!rightHand.showCountOnly && item.isDamageable()) {
            int x = Helper.calculatePositionX(rightHand.x, rightHand.originX, width_durability, rightHand.scale);
            int y = Helper.calculatePositionY(rightHand.y, rightHand.originY, height, rightHand.scale);
            Helper.renderItemDurabilityHUD(context, HAND_TEXTURE, item, x, y, 14, width_count, 27, rightHand.color | 0xFF000000);
        } else {
            int x = Helper.calculatePositionX(rightHand.x, rightHand.originX, width_count, rightHand.scale);
            int y = Helper.calculatePositionY(rightHand.y, rightHand.originY, height, rightHand.scale);
            renderItemCountHUD(context, client.textRenderer, playerInventory, item, x, y, 14, rightHand.color | 0xFF000000);
        }

        context.getMatrices().pop();
    }

    private static void renderItemCountHUD(DrawContext context, TextRenderer textRenderer, PlayerInventory playerInventory, ItemStack stack, int x, int y, float v, int color) {

        int stackAmount = getItemCount(playerInventory, stack);

        context.drawTexture(RenderLayer::getGuiTextured, HAND_TEXTURE, x, y, 0.0F, v, width_count, height, width_count, 27, color);
        context.drawText(textRenderer, Integer.toString(stackAmount), x + 19, y + 3, color, false);
    }

    private static int getItemCount(PlayerInventory inventory, ItemStack stack) {
        int stackAmount = 0;

        ItemStack offhand = inventory.offHand.get(0);

        if (offhand.isOf(stack.getItem())) stackAmount += offhand.getCount();
        for (ItemStack item : inventory.main) {
            if (!item.isEmpty() && item.isOf(stack.getItem())) {
                stackAmount += item.getCount();
            }
        }

        return stackAmount;
    }
}
