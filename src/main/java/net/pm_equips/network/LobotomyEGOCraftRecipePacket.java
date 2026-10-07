package net.pm_equips.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.pm_equips.recipe.LobotomyEGORecipeManager;
import net.pm_equips.recipe.LobotomyEGORecipe;

import java.util.function.Supplier;

public class LobotomyEGOCraftRecipePacket {

    private final ResourceLocation recipeId;

    public LobotomyEGOCraftRecipePacket(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    public static void encode(LobotomyEGOCraftRecipePacket packet, FriendlyByteBuf buf) {
        buf.writeResourceLocation(packet.recipeId);
    }

    public static LobotomyEGOCraftRecipePacket decode(FriendlyByteBuf buf) {
        return new LobotomyEGOCraftRecipePacket(buf.readResourceLocation());
    }

    public static void handle(LobotomyEGOCraftRecipePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            LobotomyEGORecipe recipe = LobotomyEGORecipeManager.get(packet.recipeId).orElse(null);
            if (recipe == null) return;

            // インベントリ参照
            if (!recipe.matchesInventory(player.getInventory())) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("材料不足 / Missing materials"),
                        true
                );
                return;
            }

            // 空きが無いと付与できない場合の簡易チェック
            ItemStack result = recipe.getResult();
            if (!player.getInventory().add(result.copy())) {
                player.drop(result.copy(), false);
            }

            recipe.consume(player.getInventory());
            player.containerMenu.broadcastChanges();
        });
        ctx.setPacketHandled(true);
    }
}
