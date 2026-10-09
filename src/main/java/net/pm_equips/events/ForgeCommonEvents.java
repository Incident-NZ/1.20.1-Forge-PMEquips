package net.pm_equips.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.pm_equips.network.NetworkPacketInit;
import net.pm_equips.network.SyncEGORecipesPacket;
import net.pm_equips.recipe.LobotomyEGORecipe;
import net.pm_equips.recipe.LobotomyEGORecipeManager;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "pm_equips", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommonEvents {

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        List<LobotomyEGORecipe> list = new ArrayList<>(LobotomyEGORecipeManager.getAll());
        NetworkPacketInit.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncEGORecipesPacket(list)
        );
    }
}