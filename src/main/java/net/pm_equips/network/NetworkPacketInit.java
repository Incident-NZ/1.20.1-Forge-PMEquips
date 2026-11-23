package net.pm_equips.network;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.events.CombatEvents;
import net.pm_equips.items.*;
import net.pm_equips.menu.LobotomyEGOExtractionMenu;
import net.pm_equips.util.FingerEquipment;

public class NetworkPacketInit {
    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(PMEquipsMain.MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int id = 0;
    private static boolean registered;

    public static void register() {
        if (registered) return;
        registered = true;
        ForgeFeaturePackets.register();
        INSTANCE.messageBuilder(ActivateAbilityPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ActivateAbilityPacket::decode)
                .encoder(ActivateAbilityPacket::encode)
                .consumerMainThread(ActivateAbilityPacket::handle)
                .add();

        INSTANCE.messageBuilder(ReloadPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ReloadPacket::decode)
                .encoder(ReloadPacket::encode)
                .consumerMainThread(ReloadPacket::handle)
                .add();

        INSTANCE.messageBuilder(LobotomyEGOExtractPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(LobotomyEGOExtractPacket::decode)
                .encoder(LobotomyEGOExtractPacket::encode)
                .consumerMainThread(LobotomyEGOExtractPacket::handle)
                .add();

        INSTANCE.messageBuilder(LegArmorAbilityActivatePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(LegArmorAbilityActivatePacket::decode)
                .encoder(LegArmorAbilityActivatePacket::encode)
                .consumerMainThread(LegArmorAbilityActivatePacket::handle)
                .add();

        INSTANCE.messageBuilder(LobotomyEGOCraftRecipePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(LobotomyEGOCraftRecipePacket::decode)
                .encoder(LobotomyEGOCraftRecipePacket::encode)
                .consumerMainThread(LobotomyEGOCraftRecipePacket::handle)
                .add();

        INSTANCE.messageBuilder(RCorpRabbitRiflePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(RCorpRabbitRiflePacket::decode)
                .encoder(RCorpRabbitRiflePacket::encode)
                .consumerMainThread(RCorpRabbitRiflePacket::handle)
                .add();

        INSTANCE.messageBuilder(SyncEGORecipesPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncEGORecipesPacket::decode)
                .encoder(SyncEGORecipesPacket::encode)
                .consumerMainThread(SyncEGORecipesPacket::handle)
                .add();
    }

    public static class ActivateAbilityPacket {
        private final UUID targetUUID;

        public ActivateAbilityPacket(UUID targetUUID) {
            this.targetUUID = targetUUID;
        }

        public static void encode(ActivateAbilityPacket msg, FriendlyByteBuf buf) {
            buf.writeUUID(msg.targetUUID);
        }

        public static ActivateAbilityPacket decode(FriendlyByteBuf buf) {
            return new ActivateAbilityPacket(buf.readUUID());
        }

        public static void handle(ActivateAbilityPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    ItemStack gun = player.getMainHandItem();
                    if (gun.getItem() instanceof LobEGOWeapon4MagicBullet && gun.getOrCreateTag().getInt("abilityCharges") > 0) {
                        gun.getOrCreateTag().putBoolean("abilityActive", true);
                        gun.getOrCreateTag().putUUID("targetUUID", msg.targetUUID);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public static class ReloadPacket {
        public ReloadPacket() {}

        public static void encode(ReloadPacket msg, FriendlyByteBuf buf) {}

        public static ReloadPacket decode(FriendlyByteBuf buf) { return new ReloadPacket(); }

        public static void handle(ReloadPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    tryStartReload(player, player.getMainHandItem());
                    tryStartReload(player, player.getOffhandItem());
                }
            });
            ctx.get().setPacketHandled(true);
        }

        private static void tryStartReload(ServerPlayer player, ItemStack stack) {
            if (stack.isEmpty()) {
                return;
            }

            if (stack.getItem() instanceof WingRCorpWeapon1) {
                ((WingRCorpWeapon1) stack.getItem()).startReload(stack, player);
            }
        }
    }

    public static class LegArmorAbilityActivatePacket {
        public LegArmorAbilityActivatePacket() {}

        public static void encode(LegArmorAbilityActivatePacket msg, FriendlyByteBuf buf) {}

        public static LegArmorAbilityActivatePacket decode(FriendlyByteBuf buf) {
            return new LegArmorAbilityActivatePacket();
        }

        public static void handle(LegArmorAbilityActivatePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) {
                    return;
                }
                if (HeadExecutioner.cycleSerum(player)) return;
            if (FingerEquipment.cycleAmmo(player)) return;
                if (FingerEquipment.distributeBuff(player)) return;

                CombatEvents.activateArmor(player);
            });
            ctx.get().setPacketHandled(true);
        }

    }

    public static class LobotomyEGOExtractPacket {
        public LobotomyEGOExtractPacket() {}

        public static void encode(LobotomyEGOExtractPacket msg, FriendlyByteBuf buf) {}

        public static LobotomyEGOExtractPacket decode(FriendlyByteBuf buf) {
            return new LobotomyEGOExtractPacket();
        }

        public static void handle(LobotomyEGOExtractPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player != null && player.containerMenu instanceof LobotomyEGOExtractionMenu menu) {
                    menu.craft(player);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
