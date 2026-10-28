package net.pm_equips.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.ParticleInit;
import net.pm_equips.items.HeadArbiterArmor;
import net.pm_equips.network.ArbiterArmorPacket;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID, value = Dist.CLIENT)
public final class ArbiterArmorClient {
    private static LocalPlayer chargingPlayer;
    private static ClientLevel chargingLevel;
    private static int heartbeatTicks;
    private static boolean requireRelease;

    private ArbiterArmorClient() {}

    @SubscribeEvent
    public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isUseItem()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (mc.player.getMainHandItem().getItem() instanceof HeadArbiterArmor
                || mc.player.getOffhandItem().getItem() instanceof HeadArbiterArmor) {
            requireRelease = true; // Equipping the item must not become an accidental attack.
        }
        if (requireRelease || !ArbiterArmorPacket.canUse(mc.player)) return;
        if (chargingPlayer == null && mc.hitResult instanceof BlockHitResult blockHit && blockHit.getType() == HitResult.Type.BLOCK) {
            if (mc.gameMode == null) return;
            // Run vanilla interaction once, including modded blocks, before considering an attack.
            InteractionResult result = mc.gameMode.useItemOn(mc.player, event.getHand(), blockHit);
            if (result.consumesAction() || result == InteractionResult.FAIL) {
                event.setCanceled(true);
                event.setSwingHand(result.shouldSwing());
                requireRelease = true;
                return;
            }
        }
        event.setCanceled(true);
        event.setSwingHand(false);
        if (event.getHand() == InteractionHand.MAIN_HAND && chargingPlayer == null) {
            chargingPlayer = mc.player;
            chargingLevel = mc.level;
            heartbeatTicks = 0;
            PacketDistributor.sendToServer(new ArbiterArmorPacket(ArbiterArmorPacket.START));
        }
    }

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.options.keyUse.isDown()) requireRelease = false;
        if (chargingPlayer == null) return;
        if (mc.player != chargingPlayer || mc.level != chargingLevel) { reset(); return; }
        if (mc.screen != null || !mc.isWindowActive() || !ArbiterArmorPacket.canUse(mc.player)) {
            PacketDistributor.sendToServer(new ArbiterArmorPacket(ArbiterArmorPacket.CANCEL));
            reset(); return;
        }
        if (!mc.options.keyUse.isDown()) {
            PacketDistributor.sendToServer(new ArbiterArmorPacket(ArbiterArmorPacket.RELEASE));
            reset(); return;
        }
        if (++heartbeatTicks >= 10) {
            heartbeatTicks = 0;
            PacketDistributor.sendToServer(new ArbiterArmorPacket(ArbiterArmorPacket.KEEP_ALIVE));
        }
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) { reset(); requireRelease = false; }

    private static void reset() { chargingPlayer = null; chargingLevel = null; heartbeatTicks = 0; }

    public static void spawnTrail(Vec3 start, Vec3 end) {
        ClientLevel level = Minecraft.getInstance().level;
        double distance = start.distanceTo(end);
        if (level == null || !Double.isFinite(distance) || distance > 129) return;
        int count = Mth.clamp(Mth.ceil(distance * 2), 1, 256);
        for (int i = 0; i <= count; i++) {
            Vec3 point = start.lerp(end, (double) i / count);
            level.addParticle(ParticleInit.ARBITER_TRAIL.get(), point.x, point.y, point.z, 0, 0, 0);
        }
    }
}
