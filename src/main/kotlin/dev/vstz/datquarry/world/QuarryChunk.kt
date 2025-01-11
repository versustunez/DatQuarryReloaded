package dev.vstz.datquarry.world

import dev.vstz.datquarry.world.QuarryWorldGen.MAX_CAPACITY
import net.minecraft.world.level.block.state.BlockState
import kotlin.random.Random

class QuarryChunk() {
    private var currentBiome: String? = null
    private val random = Random(System.currentTimeMillis())
    val amount: Int get() = MAX_CAPACITY - currentIndex
    private val blockStateArray = ArrayList<BlockState>(MAX_CAPACITY)
    private var currentIndex = 0

    init {
        // resize array ;)
        for (i in 0 until MAX_CAPACITY) {
            blockStateArray.add(QuarryWorldGen.air)
        }
    }

    val nextBlockState: BlockState
        get() = prepareNextBlock()


    private fun prepareNextBlock(): BlockState {
        currentIndex++
        if (currentIndex >= blockStateArray.size || currentBiome == null) {
            currentIndex = 0
            generateList()
        }
        return blockStateArray[currentIndex]
    }

    private fun generateList() {
        if (random.nextInt(0, 100) < 25 || currentBiome == null) {
            currentBiome = QuarryWorldGen.randomBiome
        }
        for (i in 0 until MAX_CAPACITY) {
            blockStateArray[i] = QuarryWorldGen.getBlock(currentBiome!!)
        }
    }
}