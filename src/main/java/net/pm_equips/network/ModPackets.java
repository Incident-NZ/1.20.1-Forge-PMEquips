package net.pm_equips.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.pm_equips.menu.LobotomyEGOExtractionMenu;
import net.pm_equips.items.EGOW4MagicBullet;
import net.pm_equips.items.WeaponRolandLogicHG;
import net.pm_equips.items.WeaponRolandLogicSG;

import java.util.UUID;
import java.util.function.Supplier;

public class ModPackets {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new net.minecraft.resources.ResourceLocation("pm_equips", "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void register() {
        INSTANCE.registerMessage(0, ActivateAbilityPacket.class,
                ActivateAbilityPacket::encode, ActivateAbilityPacket::decode, ActivateAbilityPacket::handle);

        INSTANCE.registerMessage(1, ReloadPacket.class,
                ReloadPacket::encode, ReloadPacket::decode, ReloadPacket::handle);

        INSTANCE.registerMessage(2, LobotomyEGOExtractPacket.class,
                LobotomyEGOExtractPacket::encode, LobotomyEGOExtractPacket::decode, LobotomyEGOExtractPacket::handle);

        INSTANCE.registerMessage(3, CorePageActivatePacket.class,
                CorePageActivatePacket::encode, CorePageActivatePacket::decode, CorePageActivatePacket::handle);
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

            if (stack.getItem() instanceof WeaponRolandLogicHG) {
                ((WeaponRolandLogicHG) stack.getItem()).startReload(stack, player);

            } else if (stack.getItem() instanceof WeaponRolandLogicSG) {
                ((WeaponRolandLogicSG) stack.getItem()).startReload(stack, player);

            } else if (stack.getItem() instanceof net.pm_equips.items.EGOW4Aroma) {
                ((net.pm_equips.items.EGOW4Aroma) stack.getItem()).startReload(stack, player);

            } else if (stack.getItem() instanceof net.pm_equips.items.RCorpRabbitRifle) {
                ((net.pm_equips.items.RCorpRabbitRifle) stack.getItem()).startReload(stack, player);
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
