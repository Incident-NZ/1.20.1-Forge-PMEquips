package net.pm_equips.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.WingRCorp4thRabbitWeapon1;

public record RCorpRabbitRiflePacket() implements CustomPacketPayload {
    public static final Type<RCorpRabbitRiflePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "rcorp_rifle_special"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RCorpRabbitRiflePacket> STREAM_CODEC = StreamCodec.unit(new RCorpRabbitRiflePacket());
    @Override public Type<RCorpRabbitRiflePacket> type() { return TYPE; }

    public static void handle(RCorpRabbitRiflePacket packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof WingRCorp4thRabbitWeapon1)) stack = player.getOffhandItem();
        if (stack.getItem() instanceof WingRCorp4thRabbitWeapon1 rifle) rifle.tryActivateSpecial(stack, player);
    }
}
