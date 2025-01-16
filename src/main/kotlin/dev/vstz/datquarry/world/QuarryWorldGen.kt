package dev.vstz.datquarry.world

import dev.vstz.datquarry.DatQuarryMod
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.server.ServerStoppedEvent
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.apache.logging.log4j.Level
import java.io.File
import mekanism.common.world.ResizableOreFeature
import mekanism.common.world.ResizableOreFeatureConfig
import kotlin.system.measureTimeMillis

class PossibleBlockMap {
    val data = HashMap<BlockState, Int>()

    fun add(name: BlockState, amount: Int): Int {
        data[name] = (data[name] ?: 0) + amount
        return amount
    }

    val hasItems: Boolean get() = data.isNotEmpty()

}

object QuarryWorldGen {
    const val MAX_CAPACITY = 2048 * 2 * 2
    private var AIR: BlockState? = null
    private var STONE: BlockState? = null
    private var DEEPSLATE: BlockState? = null
    private var GLOWSTONE: BlockState? = null
    private var NETHER_QUARTZ: BlockState? = null

    var ready: Boolean = false

    data class PossibleBlock(val block: BlockState, val weight: Float)

    data class BiomePlacement(val id: String) {
        private val blocks = ArrayList<PossibleBlock>()
        private val lookupArray = ArrayList<Int>()
        var allSize = 0
        val randomBlock: BlockState
            get() = blocks[lookupArray.random()].block

        fun fill(possibleBlockMap: HashMap<BlockState, Int>) {
            blocks.ensureCapacity(possibleBlockMap.size)
            lookupArray.ensureCapacity(allSize)
            for ((item, weight) in possibleBlockMap) {
                blocks.add(PossibleBlock(item, weight.toFloat() / allSize.toFloat()))
                repeat(weight) { lookupArray.add(blocks.size - 1) }
            }
        }

        val allBlocks: ArrayList<PossibleBlock> get() = blocks
    }

    private val possibleBlocks = HashMap<String, BiomePlacement>()

    fun onWorldLoad(server: MinecraftServer) {
        NeoForgeRegistries.Keys.BIOME_MODIFIERS.registry()

        server.allLevels.forEach { serverLevel ->
            val biomeRegistry = serverLevel.registryAccess().registryOrThrow(Registries.BIOME)
            biomeRegistry.holders().forEach { holder -> iterateBiome(holder.registeredName, holder.value()) }
        }
        /*
        File("block.yaml").printWriter().use { out ->
            possibleBlocks.forEach { biome ->
                out.println("${biome.key}:")
                biome.value.allBlocks.forEach { block ->
                    out.println("  - name: \"${block.block.block.descriptionId}\"")
                    out.println("    weight: ${block.weight}")
                }
            }
        }*/
        DatQuarryMod.LOGGER.log(Level.INFO, "Found {} biomes", possibleBlocks.keys.size)
        ready = true
    }

    fun onWorldUnload(server: MinecraftServer) {
        DatQuarryMod.LOGGER.log(Level.INFO, "unload world")
        possibleBlocks.clear();
    }

    private fun iterateBiome(name: String, biome: Biome) {
        possibleBlocks[name] = BiomePlacement(name)
        val possibleBlockMap = PossibleBlockMap()
        val settings = biome.generationSettings;
        val features = settings.features()
        features.forEach { placedFeatures ->
            placedFeatures.forEach { element ->
                val config = element.value().feature.value().config()
                if (config is OreConfiguration) {
                    config.targetStates.forEach {
                        possibleBlocks[name]!!.allSize += possibleBlockMap.add(it.state, config.size)
                    }
                } else if (config is ResizableOreFeatureConfig) {
                    config.targetStates.forEach {
                        possibleBlocks[name]!!.allSize += possibleBlockMap.add(it.state, config.size.asInt)
                    }
                }
            }
        }
        if (possibleBlockMap.hasItems) {
            val all = possibleBlocks[name]!!.allSize
            possibleBlocks[name]!!.allSize += possibleBlockMap.add(STONE!!, orMin(all, 0.4, 50))
            possibleBlocks[name]!!.allSize += possibleBlockMap.add(DEEPSLATE!!, orMin(all, 0.1, 20))
            possibleBlocks[name]!!.allSize += possibleBlockMap.add(GLOWSTONE!!, orMin(all, 0.1, 10))
            possibleBlocks[name]!!.allSize += possibleBlockMap.add(NETHER_QUARTZ!!, orMin(all, 0.01, 10))
            possibleBlocks[name]!!.fill(possibleBlockMap.data)
        } else {
            possibleBlocks.remove(name)
        }
    }

    private fun orMin(size: Int, percent: Double, min: Int): Int {
        val amount = (size * percent).toInt()
        return if (amount < min) min else amount
    }

    fun prepareBlockStates(server: MinecraftServer) {
        val blockRegistry = server.registryAccess().registry(Registries.BLOCK).get()
        STONE = blockRegistry.get(ResourceLocation.fromNamespaceAndPath("minecraft", "stone"))!!.defaultBlockState()
        DEEPSLATE =
            blockRegistry.get(ResourceLocation.fromNamespaceAndPath("minecraft", "deepslate"))!!.defaultBlockState()
        AIR = blockRegistry.get(ResourceLocation.fromNamespaceAndPath("minecraft", "air"))!!.defaultBlockState()
        GLOWSTONE =
            blockRegistry.get(ResourceLocation.fromNamespaceAndPath("minecraft", "glowstone"))!!.defaultBlockState()
        NETHER_QUARTZ = blockRegistry.get(ResourceLocation.fromNamespaceAndPath("minecraft", "nether_quartz_ore"))!!
            .defaultBlockState()
    }

    val air: BlockState
        get() {
            return AIR!!
        };

    val randomBiome: String
        get() {
            return possibleBlocks.keys.random()
        }

    fun getBlock(biome: String): BlockState {
        return possibleBlocks[biome]!!.randomBlock
    }
}

class QuarryWorldEventHandler {

    @SubscribeEvent
    fun onServerAboutToStart(event: ServerAboutToStartEvent) {
        QuarryWorldGen.prepareBlockStates(event.server);
    }

    @SubscribeEvent
    fun onServerStart(event: ServerStartedEvent) {
        val timeInMillis = measureTimeMillis {
            QuarryWorldGen.onWorldLoad(event.server)
        }
        DatQuarryMod.LOGGER.log(Level.INFO, "(Generating Possible blocks took $timeInMillis ms)")
    }

    @SubscribeEvent
    fun onServerStopped(event: ServerStoppedEvent) {
        QuarryWorldGen.onWorldUnload(event.server)
    }
}