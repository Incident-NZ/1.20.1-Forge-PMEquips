package net.pm_equips.network;

import io.netty.handler.codec.DecoderException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.compat.JEIEGORecipeSync;
import net.pm_equips.recipe.LobotomyEGORecipe;
import net.pm_equips.recipe.LobotomyEGORecipeManager;

public record SyncEGORecipesPacket(List<LobotomyEGORecipe> recipes) implements CustomPacketPayload {
    public static final Type<SyncEGORecipesPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "sync_ego_recipes"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncEGORecipesPacket> STREAM_CODEC = StreamCodec.of(
            (buffer, packet) -> packet.write(buffer), SyncEGORecipesPacket::read);

    public SyncEGORecipesPacket { recipes = List.copyOf(recipes); }
    @Override public Type<SyncEGORecipesPacket> type() { return TYPE; }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(recipes.size());
        for (LobotomyEGORecipe recipe : recipes) {
            buffer.writeResourceLocation(recipe.getId());
            ItemStack.STREAM_CODEC.encode(buffer, recipe.getResult());
            buffer.writeVarInt(recipe.getIngredients().size());
            for (var ingredient : recipe.getIngredients()) {
                buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(ingredient.item()));
                buffer.writeVarInt(ingredient.count());
            }
        }
    }

    private static SyncEGORecipesPacket read(RegistryFriendlyByteBuf buffer) {
        int count = boundedSize(buffer, 4096);
        List<LobotomyEGORecipe> recipes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ResourceLocation id = buffer.readResourceLocation();
            ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
            int ingredientCount = boundedSize(buffer, 128);
            List<LobotomyEGORecipe.IngredientEntry> ingredients = new ArrayList<>(ingredientCount);
            for (int j = 0; j < ingredientCount; j++) {
                Item item = BuiltInRegistries.ITEM.get(buffer.readResourceLocation());
                int amount = buffer.readVarInt();
                if (amount <= 0 || amount > 65536) throw new DecoderException("Invalid ingredient count");
                ingredients.add(new LobotomyEGORecipe.IngredientEntry(item, amount));
            }
            recipes.add(new LobotomyEGORecipe(id, result, ingredients));
        }
        return new SyncEGORecipesPacket(recipes);
    }

    private static int boundedSize(RegistryFriendlyByteBuf buffer, int maximum) {
        int size = buffer.readVarInt();
        if (size < 0 || size > maximum) throw new DecoderException("Invalid recipe payload size");
        return size;
    }

    public static void handle(SyncEGORecipesPacket packet, IPayloadContext context) {
        LobotomyEGORecipeManager.replaceAll(packet.recipes);
        if (ModList.get().isLoaded("jei")) JEIEGORecipeSync.syncToJei();
    }
}
