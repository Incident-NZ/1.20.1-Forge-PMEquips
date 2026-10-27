package net.pm_equips.network;
import net.pm_equips.util.FingerEquipment;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.pm_equips.items.WingRCorp4thRabbitWeapon1;

import java.util.function.Supplier;

/**
 * クライアントの Shift+C → サーバーで特殊弾装填
 */
public class RCorpRabbitRiflePacket {

    public RCorpRabbitRiflePacket() {
    }

    public static void encode(RCorpRabbitRiflePacket packet, FriendlyByteBuf buf) {

    }

    public static RCorpRabbitRiflePacket decode(FriendlyByteBuf buf) {
        return new RCorpRabbitRiflePacket();
    }

    public static void handle(RCorpRabbitRiflePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) {
                return;
            }

            // メインハンド優先、なければオフハンド
            if (FingerEquipment.isThumbRifle(player.getMainHandItem()) || FingerEquipment.isThumbRifle(player.getOffhandItem())) {
                FingerEquipment.specialShot(player); return;
            }
            ItemStack stack = player.getMainHandItem();
            InteractionHand hand = InteractionHand.MAIN_HAND;

            if (!(stack.getItem() instanceof WingRCorp4thRabbitWeapon1)) {
                stack = player.getOffhandItem();
                hand = InteractionHand.OFF_HAND;
            }

            if (stack.getItem() instanceof WingRCorp4thRabbitWeapon1 rifle) {
                rifle.tryActivateSpecial(stack, player);
            }
        });
        ctx.setPacketHandled(true);
    }
}
