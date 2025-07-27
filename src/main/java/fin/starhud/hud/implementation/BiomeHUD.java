package fin.starhud.hud.implementation;

import fin.starhud.Helper;
import fin.starhud.Main;
import fin.starhud.config.hud.BiomeSettings;
import fin.starhud.helper.RenderUtils;
import fin.starhud.hud.AbstractHUD;
import fin.starhud.hud.HUDId;
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

    private static final Identifier DIMENSION_TEXTURE = Identifier.of("starhud", "hud/dimension.png");

    private static final int TEXTURE_WIDTH = 13;
    private static final int TEXTURE_HEIGHT = 13 * 4;

    private static final int ICON_WIDTH = 13;
    private static final int ICON_HEIGHT = 13;

    private static Text cachedBiomeNameText;
    private static RegistryEntry<Biome> cachedBiome;
    private static int cachedTextWidth;

    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    public BiomeHUD() {
        super(BIOME_SETTINGS.base);
    }

    private int width;
    private int height;
    private int color;
    private int dimensionIndex;

    @Override
    public boolean collectHUDInformation() {
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
                    cachedBiomeNameText = Text.of(Helper.idNameFormatter(currentBiome.getIdAsString()));

            } else {
                cachedBiomeNameText = Text.of("Unregistered");
            }

            cachedBiome = currentBiome;
            cachedTextWidth = textRenderer.getWidth(cachedBiomeNameText) - 1;
        }

        dimensionIndex = getDimensionIndex(CLIENT.world.getRegistryKey());
        color = getTextColorFromDimension(dimensionIndex) | 0xFF000000;

        width = ICON_WIDTH + 1 + 5 + cachedTextWidth + 5;
        height = ICON_HEIGHT;

        setWidth(width);
        setHeight(height);

        return true;
    }

    @Override
    public String getName() {
        return "Biome HUD";
    }

    @Override
    public HUDId getId() {
        return HUDId.BIOME;
    }

    @Override
    public boolean renderHUD(DrawContext context, int x, int y) {

        x -= getGrowthDirectionHorizontal(width);
        y -= getGrowthDirectionVertical(height);

        setBoundingBox(x, y, width, height, color);

        RenderUtils.drawSmallHUD(
                context,
                cachedBiomeNameText.asOrderedText(),
                x, y,
                width, height,
                DIMENSION_TEXTURE,
                0.0F, ICON_HEIGHT * dimensionIndex,
                TEXTURE_WIDTH, TEXTURE_HEIGHT,
                ICON_WIDTH, ICON_HEIGHT,
                color
        );

        return true;
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
}
