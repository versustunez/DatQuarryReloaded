package dev.vstz.datquarry.block

import net.minecraft.world.level.block.Block

class Speedup(val factor: Int) : Block(
    Properties.of().strength(2.0f)
        .destroyTime(1.0f)
        .explosionResistance(1000.0f)

)

class Silk : Block(
    Properties.of().strength(2.0f)
        .destroyTime(1.0f)
        .explosionResistance(1000.0f)
) {}