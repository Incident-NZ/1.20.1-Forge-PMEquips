package net.pm_equips.events;

import java.util.ArrayList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.network.SyncEGORecipesPacket;
import net.pm_equips.recipe.LobotomyEGORecipeManager;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public final class ForgeCommonEvents {
    private ForgeCommonEvents() {}

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        SyncEGORecipesPacket packet = new SyncEGORecipesPacket(new ArrayList<>(LobotomyEGORecipeManager.getAll()));
        if (event.getPlayer() != null) PacketDistributor.sendToPlayer(event.getPlayer(), packet);
        else for (var player : event.getPlayerList().getPlayers()) PacketDistributor.sendToPlayer(player, packet);
    }
}
