package net.pm_equips.network;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.LobEGOWeapon4Aroma;
import net.pm_equips.items.WingKCorpAgentArmor;
import net.pm_equips.items.WingKCorpOfficerArmor;
import net.pm_equips.items.WingRCorp4thRabbitArmor;
import net.pm_equips.items.WingRCorp4thRabbitWeapon1;
import net.pm_equips.items.WingWCorpArmor;
import net.pm_equips.menu.LobotomyEGOExtractionMenu;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class NetworkPacketInit {
    private NetworkPacketInit() {}

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ReloadPacket.TYPE, ReloadPacket.STREAM_CODEC, ReloadPacket::handle);
        registrar.playToServer(LegArmorAbilityActivatePacket.TYPE, LegArmorAbilityActivatePacket.STREAM_CODEC,
                LegArmorAbilityActivatePacket::handle);
        registrar.playToServer(LobotomyEGOExtractPacket.TYPE, LobotomyEGOExtractPacket.STREAM_CODEC,
                LobotomyEGOExtractPacket::handle);
        registrar.playToServer(LobotomyEGOCraftRecipePacket.TYPE, LobotomyEGOCraftRecipePacket.STREAM_CODEC,
                LobotomyEGOCraftRecipePacket::handle);
        registrar.playToServer(RCorpRabbitRiflePacket.TYPE, RCorpRabbitRiflePacket.STREAM_CODEC,
                RCorpRabbitRiflePacket::handle);
        registrar.playToClient(SyncEGORecipesPacket.TYPE, SyncEGORecipesPacket.STREAM_CODEC,
                SyncEGORecipesPacket::handle);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, path);
    }

    public record ReloadPacket() implements CustomPacketPayload {
        public static final Type<ReloadPacket> TYPE = new Type<>(id("reload"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ReloadPacket> STREAM_CODEC = StreamCodec.unit(new ReloadPacket());
        @Override public Type<ReloadPacket> type() { return TYPE; }

        private static void handle(ReloadPacket packet, IPayloadContext context) {
            if (!(context.player() instanceof ServerPlayer player)) return;
            tryStartReload(player, player.getMainHandItem());
            tryStartReload(player, player.getOffhandItem());
        }

        private static void tryStartReload(ServerPlayer player, ItemStack stack) {
            if (stack.getItem() instanceof LobEGOWeapon4Aroma aroma) aroma.startReload(stack, player);
            else if (stack.getItem() instanceof WingRCorp4thRabbitWeapon1 rifle) rifle.startReload(stack, player);
        }
    }

    public record LegArmorAbilityActivatePacket() implements CustomPacketPayload {
        public static final Type<LegArmorAbilityActivatePacket> TYPE = new Type<>(id("leg_armor_ability"));
        public static final StreamCodec<RegistryFriendlyByteBuf, LegArmorAbilityActivatePacket> STREAM_CODEC = StreamCodec.unit(new LegArmorAbilityActivatePacket());
        @Override public Type<LegArmorAbilityActivatePacket> type() { return TYPE; }

        private static void handle(LegArmorAbilityActivatePacket packet, IPayloadContext context) {
            if (!(context.player() instanceof ServerPlayer player)) return;
            ItemStack stack = player.getItemBySlot(EquipmentSlot.LEGS);
            if (stack.getItem() instanceof WingWCorpArmor) activateEffect(player, stack, 1000, MobEffectInit.WCORP_SIN);
            else if (stack.getItem() instanceof WingRCorp4thRabbitArmor) activateEffect(player, stack, 2000, MobEffectInit.RCORP_SIN);
            else if (stack.getItem() instanceof WingKCorpAgentArmor || stack.getItem() instanceof WingKCorpOfficerArmor)
                activateEffect(player, stack, 1500, MobEffectInit.KCORP_SIN);
        }

        private static void activateEffect(ServerPlayer player, ItemStack stack, int cost, Holder<MobEffect> effect) {
            var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if (energy == null || energy.extractEnergy(cost, true) != cost) return;
            energy.extractEnergy(cost, false);
            player.addEffect(new MobEffectInstance(effect, 12000, 0));
        }
    }

    public record LobotomyEGOExtractPacket() implements CustomPacketPayload {
        public static final Type<LobotomyEGOExtractPacket> TYPE = new Type<>(id("ego_extract"));
        public static final StreamCodec<RegistryFriendlyByteBuf, LobotomyEGOExtractPacket> STREAM_CODEC = StreamCodec.unit(new LobotomyEGOExtractPacket());
        @Override public Type<LobotomyEGOExtractPacket> type() { return TYPE; }

        private static void handle(LobotomyEGOExtractPacket packet, IPayloadContext context) {
            if (context.player() instanceof ServerPlayer player
                    && player.containerMenu instanceof LobotomyEGOExtractionMenu menu && menu.stillValid(player)) {
                menu.craft(player);
            }
        }
    }
}
