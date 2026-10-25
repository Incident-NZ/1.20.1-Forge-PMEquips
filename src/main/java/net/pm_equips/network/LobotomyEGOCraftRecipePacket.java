package net.pm_equips.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.menu.LobotomyEGOCraftMenu;
import net.pm_equips.recipe.LobotomyEGORecipe;
import net.pm_equips.recipe.LobotomyEGORecipeManager;

public record LobotomyEGOCraftRecipePacket(ResourceLocation recipeId) implements CustomPacketPayload {
    public static final Type<LobotomyEGOCraftRecipePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "ego_craft_recipe"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LobotomyEGOCraftRecipePacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, LobotomyEGOCraftRecipePacket::recipeId, LobotomyEGOCraftRecipePacket::new);
    @Override public Type<LobotomyEGOCraftRecipePacket> type() { return TYPE; }

    public static void handle(LobotomyEGOCraftRecipePacket packet, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)
                || !(player.containerMenu instanceof LobotomyEGOCraftMenu menu) || !menu.stillValid(player)) return;
        LobotomyEGORecipe recipe = LobotomyEGORecipeManager.get(packet.recipeId).orElse(null);
        if (recipe == null) return;
        if (!recipe.matchesInventory(player.getInventory())) {
            player.displayClientMessage(Component.literal("材料不足 / Missing materials"), true);
            return;
        }
        if (!recipe.consume(player.getInventory())) return;
        ItemStack remaining = recipe.getResult();
        if (!player.getInventory().add(remaining) && !remaining.isEmpty()) player.drop(remaining, false);
        player.containerMenu.broadcastChanges();
    }
}
