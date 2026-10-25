package net.pm_equips.client;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.pm_equips.ItemInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.FixerColorRolandWeapon6;
import net.pm_equips.items.LCDWeaponMook;
import net.pm_equips.util.ItemStackData;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KatanaClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ItemInit.FIXER_ROLAND_MOOK.get(),
                    ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "drawn"),
                    (stack, level, entity, seed) -> stack.getItem() instanceof FixerColorRolandWeapon6 mook && mook.isDrawn(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ItemInit.LCD_WEAPON_MOOK.get(),
                    ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "drawn"),
                    (stack, level, entity, seed) -> LCDWeaponMook.isDrawn(stack) ? 1.0F : 0.0F);
        });
    }
    public static void appendCooldownTooltip(ItemStack stack, String key, List<Component> tooltip) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        long remaining = ItemStackData.getLong(stack, key) - level.getGameTime();
        if (remaining > 0) tooltip.add(Component.literal("§8必殺技冷却中: " + String.format("%.1f", remaining / 20.0) + "秒"));
    }
}
