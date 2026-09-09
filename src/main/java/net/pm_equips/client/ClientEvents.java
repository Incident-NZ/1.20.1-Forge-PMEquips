package net.pm_equips.client;

import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.pm_equips.PMERegistryBlockEntity;
import net.pm_equips.PMERegistryEntity;
import net.pm_equips.PMERegistryKeyBind;
import net.pm_equips.PMERegistryMenu;
import net.pm_equips.client.renderer.*;
import net.pm_equips.client.screen.LobotomyEGOExtractionScreen;
import net.pm_equips.PMEquipsMain;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@net.neoforged.fml.common.EventBusSubscriber(
        modid = PMEquipsMain.MOD_ID,
        bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(PMERegistryMenu.LOBOTOMY_EGO_EXTRACTION_TABLE.get(), LobotomyEGOExtractionScreen::new);
    }

    //EntityRenderer
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(PMERegistryEntity.W5_SOUND_OF_A_STAR_PROJECTILE.get(),
                ctx -> new ThrownItemRenderer<>(ctx, 1.0f, true));
        event.registerEntityRenderer(PMERegistryEntity.MAGIC_BULLET.get(),
                AmmoMagicR::new);
        event.registerEntityRenderer(PMERegistryEntity.BULLET_LARV.get(),
                AmmoLARVR::new);
        event.registerEntityRenderer(PMERegistryEntity.BULLET_LASG.get(),
                AmmoLASGR::new);
        event.registerEntityRenderer(PMERegistryEntity.BULLET.get(),
                AmmoGunR::new);
        event.registerEntityRenderer(PMERegistryEntity.HEAVEN_PROJECTILE.get(),
                ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
        event.registerEntityRenderer(PMERegistryEntity.HATRED_MAGIC_PROJECTILE.get(),
                AmmoHatredR::new);
        event.registerEntityRenderer(PMERegistryEntity.BULLET_EX.get(),
                AmmoExpR::new);
        event.registerEntityRenderer(PMERegistryEntity.WHITENIGHT_PROJECTILE.get(),
                PWhiteNightR::new);
        event.registerBlockEntityRenderer(PMERegistryBlockEntity.EBOX_GEN.get(), EBoxGenR::new);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(PMERegistryKeyBind.RELOAD_KEY);
        event.register(PMERegistryKeyBind.SCOPE_KEY);
        event.register(PMERegistryKeyBind.CORE_PAGE_ABILITY_KEY);
    }
}

