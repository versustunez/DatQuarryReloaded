package dev.vstz.datquarry.entity

import appeng.api.config.Actionable
import appeng.api.networking.security.IActionSource
import appeng.api.stacks.AEItemKey
import appeng.api.stacks.AEKey
import appeng.api.storage.MEStorage
import appeng.block.misc.InterfaceBlock
import appeng.blockentity.misc.InterfaceBlockEntity
import dev.vstz.datquarry.DatQuarryMod
import dev.vstz.datquarry.block.Silk
import dev.vstz.datquarry.block.Speedup
import dev.vstz.datquarry.storage.FakeItemStack
import dev.vstz.datquarry.utils.LootUtils
import dev.vstz.datquarry.world.QuarryChunk
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.DiggerItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.items.IItemHandler
import kotlin.math.max


open class QuarryEntity(pos: BlockPos, state: BlockState) : IEnergyStorage,
    BlockEntity(ModEntities.QUARRY_ENTITY.get(), pos, state) {
    private var energyLevel: Int = 0
    private val maxStorage: Int = 5_000_000
    private val chunk: QuarryChunk = QuarryChunk()


    private var isInit = false
    private var useSilk = false
    private var tickRate = 1
    private var skippingTicks = 0

    private var pickaxe: ItemStack? = null;
    private var shovel: ItemStack? = null;

    /** ENERGY GROUP **/

    override fun receiveEnergy(maxReceive: Int, simulated: Boolean): Int {
        energyLevel = max(0, energyLevel)
        val maxNeeded = maxStorage - energyLevel

        if (maxNeeded <= 0) {
            return 0
        }

        val quantity = minOf(maxNeeded, maxReceive)
        if (!simulated) {
            energyLevel += quantity
        }

        return quantity
    }

    override fun extractEnergy(maxExtract: Int, simulated: Boolean): Int {
        return 0
    }

    override fun getEnergyStored(): Int {
        return energyLevel
    }

    override fun getMaxEnergyStored(): Int {
        return maxStorage
    }

    override fun canExtract(): Boolean {
        return false
    }

    override fun canReceive(): Boolean {
        return energyLevel < maxStorage
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
        if (level.isClientSide) return

        if (!isInit) {
            updateNeighborBlocks()
            lateInit()
        }

        if (interfaceEntity == null || !interfaceEntity!!.mainNode.isOnline) return;

        if (skippingTicks < getTickSkipNeeded()) {
            skippingTicks++
            return
        }

        skippingTicks = 0
        val powerNeeded = getPowerNeeded()
        if (energyLevel < powerNeeded) {
            return
        }

        val storage = interfaceEntity!!.interfaceLogic.inventory

        energyLevel -= powerNeeded
        energyLevel = max(0, energyLevel)

        for (i in 0 until tickRate) {
            val blockState = chunk.nextBlockState
            if (blockState.isAir) {
                return
            }

            if (pickaxe != null) {
                // farm with pickaxe
                if (useSilk) {
                    silkBlock(blockState, storage)
                } else {
                    farmBlock(blockState, pickaxe!!, storage)
                }
            }
        }
    }


    private fun silkBlock(blockState: BlockState, storage: MEStorage) {
        val item = AEItemKey.of(blockState.block.asItem())
        storage.insert(item, 1, Actionable.MODULATE, IActionSource.empty())
    }


    private fun farmBlock(blockState: BlockState, tool: ItemStack, storage: MEStorage) {
        val loot = LootUtils.getLoot(level as ServerLevel, blockState, BlockPos(0, 0, 0), tool)
        loot.forEach {
            val item = AEItemKey.of(it)
            storage.insert(item, it.count.toLong(), Actionable.MODULATE, IActionSource.empty())
        }
    }


    // NEIGHBORS
    fun onNeighborUpdate() {
        updateNeighborBlocks()
    }

    private fun updateNeighborBlocks() {
        val directNeighbor = arrayOf(
            BlockPos(blockPos.x + 1, blockPos.y, blockPos.z),
            BlockPos(blockPos.x - 1, blockPos.y, blockPos.z),
            BlockPos(blockPos.x, blockPos.y, blockPos.z + 1),
            BlockPos(blockPos.x, blockPos.y, blockPos.z - 1),
            BlockPos(blockPos.x, blockPos.y + 1, blockPos.z),
            BlockPos(blockPos.x, blockPos.y - 1, blockPos.z),
        )

        var speed = 1
        var silkFound = false;

        interfaceEntity = null;
        directNeighbor.forEach {
            val possibleBlock = level!!.getBlockState(it).block
            if (possibleBlock is Speedup) {
                speed += 1 * possibleBlock.factor
            }
            silkFound = silkFound || possibleBlock is Silk
            if (possibleBlock is InterfaceBlock) {
                interfaceEntity = possibleBlock.getBlockEntity(level, it) as InterfaceBlockEntity
            }
        }

        tickRate = speed
        useSilk = silkFound
    }

    var interfaceEntity: InterfaceBlockEntity? = null

    private fun getPowerNeeded(): Int {
        return (64 * if (!useSilk) 1 else 2) * (tickRate * 5.0).toInt()
    }

    private fun getTickSkipNeeded(): Int {
        val ticks = ((-32 + tickRate) * -1)
        return 0.coerceAtLeast(ticks)
    }

    private fun lateInit() {
        // Startup the chunk
        chunk.init()

        if (!(level == null || level!!.isClientSide)) {
            val itemReg = level!!.registryAccess().registryOrThrow(Registries.ITEM);
            pickaxe = ItemStack(
                itemReg.get(
                    ResourceLocation.fromNamespaceAndPath(
                        "minecraft",
                        "netherite_pickaxe"
                    )
                ) as DiggerItem, 1
            )
            shovel = ItemStack(
                itemReg.get(
                    ResourceLocation.fromNamespaceAndPath(
                        "minecraft",
                        "netherite_shovel"
                    )
                ) as DiggerItem, 1
            )
        }
        isInit = true
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("energy", energyLevel)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        energyLevel = tag.getInt("energy")
    }
}