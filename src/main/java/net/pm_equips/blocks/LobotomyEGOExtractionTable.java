package net.pm_equips.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.pm_equips.blockentity.LobotomyEGOExtractionTableBlockEntity;
import org.jetbrains.annotations.Nullable;

public class LobotomyEGOExtractionTable extends BaseEntityBlock {
    public static final MapCodec<LobotomyEGOExtractionTable> CODEC = simpleCodec(LobotomyEGOExtractionTable::new);

    @Override
    protected MapCodec<LobotomyEGOExtractionTable> codec() { return CODEC; }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public LobotomyEGOExtractionTable() {
        this(Properties.of()
                .requiresCorrectToolForDrops()
                .strength(5.0F, 5.0F)
                .sound(SoundType.METAL));
    }

    public LobotomyEGOExtractionTable(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof LobotomyEGOExtractionTableBlockEntity blockEntity && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(blockEntity, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof LobotomyEGOExtractionTableBlockEntity blockEntity) {
            blockEntity.dropContents(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LobotomyEGOExtractionTableBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // プレイヤーの水平向きの「反対」＝プレイヤーの正面にブロックの表が向く
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
