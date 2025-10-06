package fin.starhud.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record MoneyDataPayload(int privateBalance, Optional<Integer> teamBalance) implements CustomPayload {
    public static final Identifier MONEY_DATA_PAYLOAD_ID = Identifier.of("starhud", "money_data");
    public static final CustomPayload.Id<MoneyDataPayload> ID = new CustomPayload.Id<>(MONEY_DATA_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, MoneyDataPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, moneyDataPayload -> moneyDataPayload.privateBalance, PacketCodecs.optional(PacketCodecs.INTEGER), moneyDataPayload -> moneyDataPayload.teamBalance, MoneyDataPayload::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}