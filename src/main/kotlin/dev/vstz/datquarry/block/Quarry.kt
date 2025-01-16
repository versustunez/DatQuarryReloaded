package dev.vstz.datquarry.block

import dev.vstz.datquarry.entity.QuarryEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties


class Quarry(properties: BlockBehaviour.Properties) : Block(properties), EntityBlock {
    override fun newBlockEntity(p0: BlockPos, p1: BlockState): BlockEntity {
        return QuarryEntity(p0, p1)
    }

    @SuppressWarnings("unchecked")
    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (level.isClientSide) return null
        return BlockEntityTicker { level1: Level, pos: BlockPos?, state1: BlockState?, be: BlockEntity? ->
            if (be is QuarryEntity) {
                be.tick(level1, pos!!, state1!!, be)
            }
        }
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block?, BlockState?>) {
        // this is where the properties are actually added to the state
        pBuilder.add(BlockStateProperties.FACING)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston)
        if (level.isClientSide) return;
        (level.getBlockEntity(pos) as QuarryEntity).onNeighborUpdate()
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue(BlockStateProperties.FACING, rotation.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return super.getStateForPlacement(context)
            ?.setValue(BlockStateProperties.FACING, context.horizontalDirection.opposite);
    }

}