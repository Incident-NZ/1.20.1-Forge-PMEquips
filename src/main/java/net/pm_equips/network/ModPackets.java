package net.pm_equips.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.pm_equips.items.LCorpEGOR4MagicBullet;
import net.pm_equips.items.FixerRolandLogicHG;
import net.pm_equips.items.FixerRolandLogicSG;
import net.pm_equips.menu.LobotomyEGOExtractionMenu;
import net.pm_equips.util.ItemStackData;

import java.util.UUID;

public final class ModPackets {
    private ModPackets() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(ActivateAbilityPacket.TYPE, ActivateAbilityPacket.STREAM_CODEC,
                ActivateAbilityPacket::handle);
        registrar.playToServer(ReloadPacket.TYPE, ReloadPacket.STREAM_CODEC, ReloadPacket::handle);
        registrar.playToServer(LobotomyEGOExtractPacket.TYPE, LobotomyEGOExtractPacket.STREAM_CODEC,
                LobotomyEGOExtractPacket::handle);
        registrar.playToServer(CorePageActivatePacket.TYPE, CorePageActivatePacket.STREAM_CODEC,
                CorePageActivatePacket::handle);
    }

    public record ActivateAbilityPacket(UUID targetUUID) implements CustomPacketPayload {
        public static final Type<ActivateAbilityPacket> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath("pm_equips", "activate_ability"));
        public static final StreamCodec<FriendlyByteBuf, ActivateAbilityPacket> STREAM_CODEC =
                StreamCodec.of((buffer, packet) -> buffer.writeUUID(packet.targetUUID),
                        buffer -> new ActivateAbilityPacket(buffer.readUUID()));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void handle(ActivateAbilityPacket packet, IPayloadContext context) {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            ItemStack gun = player.getMainHandItem();
            if (gun.getItem() instanceof LCorpEGOR4MagicBullet
                    && ItemStackData.getInt(gun, "abilityCharges") > 0) {
                ItemStackData.update(gun, tag -> {
                    tag.putBoolean("abilityActive", true);
                    tag.putUUID("targetUUID", packet.targetUUID);
                });
            }
        }
    }

    public record ReloadPacket() implements CustomPacketPayload {
        public static final Type<ReloadPacket> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath("pm_equips", "reload"));
        public static final StreamCodec<FriendlyByteBuf, ReloadPacket> STREAM_CODEC =
                StreamCodec.unit(new ReloadPacket());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void handle(ReloadPacket packet, IPayloadContext context) {
            if (context.player() instanceof ServerPlayer player) {
                tryStartReload(player, player.getMainHandItem());
                tryStartReload(player, player.getOffhandItem());
            }
        }

        private static void tryStartReload(ServerPlayer player, ItemStack stack) {
            if (stack.isEmpty()) {
                return;
            }

            switch (stack.getItem()) {
                case FixerRolandLogicHG weapon -> weapon.startReload(stack, player);
                case FixerRolandLogicSG weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR1Soda weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR2Solitude weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR2Today weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR2Beak weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR2Match weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR3Laetitia weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR3Harmony weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR4LamentR weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR4Hornet weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR4CrimsonScarL weapon -> weapon.startReload(stack, player);
                case LCorpEGOR4MagicBullet weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR4Aroma weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.LCorpEGOR5Pink weapon -> weapon.startReload(stack, player);
                case net.pm_equips.items.WingsRCorp4thRabbitWeapon1 weapon -> weapon.startReload(stack, player);
                default -> {
                }
            }
        }
    }

    public record CorePageActivatePacket() implements CustomPacketPayload {
        public static final Type<CorePageActivatePacket> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath("pm_equips", "activate_core_page"));
        public static final StreamCodec<FriendlyByteBuf, CorePageActivatePacket> STREAM_CODEC =
                StreamCodec.unit(new CorePageActivatePacket());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void handle(CorePageActivatePacket packet, IPayloadContext context) {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            ItemStack stack = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS);
            if (!(stack.getItem() instanceof net.pm_equips.items.GeoArmorItem armor) || !armor.hasActiveAbility()) {
                stack = ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                return;
            }

            if (stack.getItem() instanceof net.pm_equips.items.WingsWCorpArmorTier1) {
                activateEffect(player, stack, 1000, net.pm_equips.PMERegistryMobEffect.WCORP_SIN.getDelegate());
            } else if (stack.getItem() instanceof net.pm_equips.items.WingsRCorp4thRabbitArmorTier1) {
                activateEffect(player, stack, 2000, net.pm_equips.PMERegistryMobEffect.RCORP_SIN.getDelegate());
            } else if (stack.getItem() instanceof net.pm_equips.items.WingsKCorpAgentArmorTier3
                    || stack.getItem() instanceof net.pm_equips.items.WingsKCorpOfficerArmorTier1) {
                activateEffect(player, stack, 1500, net.pm_equips.PMERegistryMobEffect.KCORP_SIN.getDelegate());
            }
        }

        private static void activateEffect(ServerPlayer player, ItemStack stack, int cost,
                                           net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect) {
            var storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (storage != null && storage.getEnergyStored() >= cost) {
                storage.extractEnergy(cost, false);
                player.addEffect(new MobEffectInstance(effect, 12000, 0));
            }
        }
    }

    public record LobotomyEGOExtractPacket() implements CustomPacketPayload {
        public static final Type<LobotomyEGOExtractPacket> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath("pm_equips", "extract_ego"));
        public static final StreamCodec<FriendlyByteBuf, LobotomyEGOExtractPacket> STREAM_CODEC =
                StreamCodec.unit(new LobotomyEGOExtractPacket());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        private static void handle(LobotomyEGOExtractPacket packet, IPayloadContext context) {
            if (context.player() instanceof ServerPlayer player
                    && player.containerMenu instanceof LobotomyEGOExtractionMenu menu) {
                menu.craft(player);
            }
        }
    }
}
