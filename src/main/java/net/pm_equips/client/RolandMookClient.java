package net.pm_equips.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.pm_equips.ItemInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.FixerColorRolandWeapon6;
import net.pm_equips.items.LCDWeaponMook;

@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RolandMookClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemProperties.register(ItemInit.FIXER_ROLAND_MOOK.get(),
                new ResourceLocation(PMEquipsMain.MOD_ID, "drawn"),
                (stack, level, entity, seed) -> stack.getItem() instanceof FixerColorRolandWeapon6 mook && mook.isDrawn(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ItemInit.LCD_WEAPON_MOOK.get(),
                new ResourceLocation(PMEquipsMain.MOD_ID, "drawn"),
                (stack, level, entity, seed) -> LCDWeaponMook.isDrawn(stack) ? 1.0F : 0.0F);
    }
}
