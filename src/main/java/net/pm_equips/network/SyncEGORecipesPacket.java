package net.pm_equips.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.pm_equips.compat.JEIEGORecipeSync;
import net.pm_equips.recipe.LobotomyEGORecipe;
import net.pm_equips.recipe.LobotomyEGORecipeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SyncEGORecipesPacket {

    private final List<LobotomyEGORecipe> recipes;

    public SyncEGORecipesPacket(List<LobotomyEGORecipe> recipes) {
        this.recipes = recipes;
    }

    public static void encode(SyncEGORecipesPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.recipes.size());
        for (LobotomyEGORecipe r : msg.recipes) {
            buf.writeResourceLocation(r.getId());
            buf.writeItem(r.getResult());
            buf.writeVarInt(r.getIngredients().size());
            for (var ing : r.getIngredients()) {
                buf.writeRegistryId(ForgeRegistries.ITEMS, ing.item());
                buf.writeVarInt(ing.count());
            }
        }
    }

    public static SyncEGORecipesPacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<LobotomyEGORecipe> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ResourceLocation id = buf.readResourceLocation();
            ItemStack result = buf.readItem();
            int ingSize = buf.readVarInt();
            List<LobotomyEGORecipe.IngredientEntry> ings = new ArrayList<>();
            for (int j = 0; j < ingSize; j++) {
                Item item = buf.readRegistryIdSafe(Item.class);
                int count = buf.readVarInt();
                ings.add(new LobotomyEGORecipe.IngredientEntry(item, count));
            }
            list.add(new LobotomyEGORecipe(id, result, ings));
        }
        return new SyncEGORecipesPacket(list);
    }

    public static void handle(SyncEGORecipesPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LobotomyEGORecipeManager.replaceAll(msg.recipes);
            // JEI 再登録（下のヘルパー）
            JEIEGORecipeSync.syncToJei();
        });
        ctx.get().setPacketHandled(true);
    }
}
