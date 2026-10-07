package net.pm_equips.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.pm_equips.menu.LobotomyEGOCraftMenu;
import org.jetbrains.annotations.Nullable;

public class LobotomyEGOCraftTable extends Block {

    public LobotomyEGOCraftTable() {
        super(Properties.of()
                .requiresCorrectToolForDrops()
                .strength(5.0F, 5.0F)
                .sound(SoundType.METAL));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("ロボトミーE.G.O製造台");
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
                    return new LobotomyEGOCraftMenu(id, inv);
                }
            });
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
