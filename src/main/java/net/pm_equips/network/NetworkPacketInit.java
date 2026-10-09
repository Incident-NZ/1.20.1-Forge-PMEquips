package net.pm_equips.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.*;
import net.pm_equips.menu.LobotomyEGOExtractionMenu;

import java.util.UUID;
import java.util.function.Supplier;

public class NetworkPacketInit {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(PMEquipsMain.MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int id = 0;

    public static void register() {
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

        INSTANCE.messageBuilder(CorePageActivatePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(CorePageActivatePacket::decode)
                .encoder(CorePageActivatePacket::encode)
                .consumerMainThread(CorePageActivatePacket::handle)
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
                net.minecraft.server.level.ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    net.minecraft.world.item.ItemStack gun = player.getMainHandItem();
                    if (gun.getItem() instanceof EGOW4MagicBullet && gun.getOrCreateTag().getInt("abilityCharges") > 0) {
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
                net.minecraft.server.level.ServerPlayer player = ctx.get().getSender();
                if (player != null) {
                    tryStartReload(player, player.getMainHandItem());
                    tryStartReload(player, player.getOffhandItem());
                }
            });
            ctx.get().setPacketHandled(true);
        }

        private static void tryStartReload(net.minecraft.server.level.ServerPlayer player, ItemStack stack) {
            if (stack.isEmpty()) {
                return;
            }

            if (stack.getItem() instanceof EGOW4Aroma) {
                ((EGOW4Aroma) stack.getItem()).startReload(stack, player);

            } else if (stack.getItem() instanceof RCorpRabbitRifle) {
                ((RCorpRabbitRifle) stack.getItem()).startReload(stack, player);
            }
        }
    }

    public static class CorePageActivatePacket {
        public CorePageActivatePacket() {}

        public static void encode(CorePageActivatePacket msg, FriendlyByteBuf buf) {}

        public static CorePageActivatePacket decode(FriendlyByteBuf buf) {
            return new CorePageActivatePacket();
        }

        public static void handle(CorePageActivatePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                net.minecraft.server.level.ServerPlayer player = ctx.get().getSender();
                if (player == null) {
                    return;
                }

                ItemStack stack = net.pm_equips.items.CorePageItem.findEquippedAbilityItem(player)
                        .orElse(ItemStack.EMPTY);
                if (stack.isEmpty()) {
                    return;
                }

                if (stack.getItem() instanceof net.pm_equips.items.WCorpArmor) {
                    activateEffect(player, stack, 1000, net.pm_equips.MobEffectInit.WCORP_SIN.get());
                } else if (stack.getItem() instanceof net.pm_equips.items.RCorp4thRabbitArmor) {
                    activateEffect(player, stack, 2000, net.pm_equips.MobEffectInit.RCORP_SIN.get());
                } else if (stack.getItem() instanceof net.pm_equips.items.KCorpAgentArmor
                        || stack.getItem() instanceof net.pm_equips.items.KCorpOfficerArmor) {
                    activateEffect(player, stack, 1500, net.pm_equips.MobEffectInit.KCORP_SIN.get());
                }
            });
            ctx.get().setPacketHandled(true);
        }

        private static void activateEffect(
                ServerPlayer player,
                ItemStack stack,
                int cost,
                MobEffect effect
        ) {
            stack.getCapability(ForgeCapabilities.ENERGY)
                    .map(storage -> {
                        if (storage.getEnergyStored() < cost) {
                            return false;
                        }
                        storage.extractEnergy(cost, false);
                        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect, 12000, 0));
                        return true;
                    });
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
                net.minecraft.server.level.ServerPlayer player = ctx.get().getSender();
                if (player != null && player.containerMenu instanceof LobotomyEGOExtractionMenu menu) {
                    menu.craft(player);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
