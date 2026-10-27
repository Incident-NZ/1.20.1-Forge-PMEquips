package net.pm_equips.client;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.pm_equips.BlockEntityInit;
import net.pm_equips.EntityInit;
import net.pm_equips.KeyBindInit;
import net.pm_equips.MenuInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.ParticleInit;
import net.pm_equips.client.particle.ArbiterParticle;
import net.pm_equips.client.renderer.*;
import net.pm_equips.client.screen.LobotomyEGOCraftScreen;
import net.pm_equips.client.screen.LobotomyEGOExtractionScreen;
import net.pm_equips.entity.MobNPC2;

@EventBusSubscriber(
        modid = PMEquipsMain.MOD_ID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT)
public class ClientEvents {


    //Menu Registry
    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuInit.LOBOTOMY_EGO_EXTRACTION_TABLE.get(), LobotomyEGOExtractionScreen::new);
        event.register(MenuInit.GUN_WORKBENCH.get(), LobotomyEGOCraftScreen::new);
    }

    //EntityRenderer
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.MOB_NPC_1.get(), MobNPC2::new);
        event.registerEntityRenderer(EntityInit.P_ARBITER_PILLAR.get(), PArbiterPillarR::new);
        event.registerEntityRenderer(EntityInit.W5_SOUND_OF_A_STAR_PROJECTILE.get(),
                ctx -> new ThrownItemRenderer<>(ctx, 1.0f, true));
        event.registerEntityRenderer(EntityInit.HEAVEN_PROJECTILE.get(),
                ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
        event.registerEntityRenderer(EntityInit.WHITENIGHT_PROJECTILE.get(),
                PWhiteNightR::new);
        event.registerBlockEntityRenderer(BlockEntityInit.EBOX_GEN.get(), EBoxGenR::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleInit.ARBITER_TRAIL.get(), sprites ->
                (type, level, x, y, z, dx, dy, dz) -> new ArbiterParticle(level, x, y, z, sprites));
    }

    //KeyBind
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KeyBindInit.RELOAD_KEY);
        event.register(KeyBindInit.SCOPE_KEY);
        event.register(KeyBindInit.LEG_ARMOR_ABILITY_KEY);
        event.register(KeyBindInit.WEAPON_ABILITY_KEY);
    }
}
