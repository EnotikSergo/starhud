package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.hud.BiomeSettings;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Optional;

public class  BiomeHUD extends AbstractHUD {

    private static final BiomeSettings BIOME_SETTINGS = Main.settings.biomeSettings;

    private static final Identifier DIMENSION_TEXTURE = Identifier.of("starhud", "hud/biome.png");

    private static Text cachedBiomeNameText;
    private static RegistryEntry<net.minecraft.world.biome.Biome> cachedBiome;
    private static int cachedTextWidth;

    private static final int TEXTURE_WIDTH = 24;
    private static final int TEXTURE_HEIGHT = 13;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public BiomeHUD() {
        super(BIOME_SETTINGS.base);
    }

    @Override
    public String getName() {
        return "Biome HUD";
    }

    @Override
    public boolean renderHUD(DrawContext context) {
        TextRenderer textRenderer = CLIENT.textRenderer;

        BlockPos blockPos = CLIENT.player.getBlockPos();
        RegistryEntry<Biome> currentBiome = CLIENT.world.getBiome(blockPos);

        if (cachedBiome != currentBiome) {
            Optional<RegistryKey<Biome>> biomeKey = currentBiome.getKey();

            if (biomeKey.isPresent()) {
                Identifier biomeId = biomeKey.get().getValue();
                String translatableKey = "biome." + biomeId.getNamespace() + '.' + biomeId.getPath();

                // if it has translation we get the translation, else we just convert it to Pascal Case manually.
                if (Language.getInstance().hasTranslation(translatableKey))
                    cachedBiomeNameText = Text.translatable(translatableKey);
                else
                    cachedBiomeNameText = Text.of(Helper.idNameFormatter(getBiomeIdAsString(currentBiome)));

            } else {
                cachedBiomeNameText = Text.of("Unregistered");
            }

            cachedBiome = currentBiome;
            cachedTextWidth = textRenderer.getWidth(cachedBiomeNameText);
        }

        int dimensionIndex = getDimensionIndex(CLIENT.world.getRegistryKey());
        int color = getTextColorFromDimension(dimensionIndex) | 0xFF000000;

        int xTemp = x - BIOME_SETTINGS.base.growthDirectionX.getGrowthDirection(cachedTextWidth);

        RenderUtils.drawTextureHUD(context, DIMENSION_TEXTURE, xTemp, y, 0.0F, dimensionIndex * TEXTURE_HEIGHT, 13, TEXTURE_HEIGHT, 13, 52);
        RenderUtils.fillRoundedRightSide(context, xTemp + 14, y, xTemp + 14 + cachedTextWidth + 9, y + TEXTURE_HEIGHT, 0x80000000);
        RenderUtils.drawTextHUD(context, cachedBiomeNameText.asOrderedText(), xTemp + 19, y + 3, color, false);

        setBoundingBox(xTemp, y, 14 + cachedTextWidth + 9, TEXTURE_HEIGHT, color);
        return true;
    }

    private static String getBiomeIdAsString(RegistryEntry<Biome> biome) {
        return biome.getKeyOrValue().map((biomeKey) -> biomeKey.getValue().toString(), (biome_) -> "[unregistered " + biome_ + "]");
    }

    private static int getDimensionIndex(RegistryKey<World> registryKey) {
        if (registryKey == World.OVERWORLD) return 0;
        else if (registryKey == World.NETHER) return 1;
        else if (registryKey == World.END) return 2;
        else return 3;
    }

    private static int getTextColorFromDimension(int dimensionIndex) {
        return switch (dimensionIndex) {
            case 0 -> BIOME_SETTINGS.color.overworld;
            case 1 -> BIOME_SETTINGS.color.nether;
            case 2 -> BIOME_SETTINGS.color.end;
            default -> BIOME_SETTINGS.color.custom;
        };
    }

    @Override
    public int getBaseHUDWidth() {
        return TEXTURE_WIDTH;
    }

    @Override
    public int getBaseHUDHeight() {
        return TEXTURE_HEIGHT;
    }
}
