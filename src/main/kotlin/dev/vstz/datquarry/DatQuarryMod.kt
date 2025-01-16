package dev.vstz.datquarry

import dev.vstz.datquarry.block.ModBlocks
import dev.vstz.datquarry.entity.ModEntities
import dev.vstz.datquarry.item.ModItems
import dev.vstz.datquarry.utils.CreativeTaab
import dev.vstz.datquarry.world.QuarryWorldEventHandler
import net.minecraft.client.Minecraft
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

@Mod(DatQuarryMod.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object DatQuarryMod {
    const val ID = "datquarry"
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        ModBlocks.REGISTRY.register(MOD_BUS)
        ModEntities.REGISTRY.register(MOD_BUS)
        ModItems.REGISTRY.register(MOD_BUS)
        CreativeTaab.REGISTRY.register(MOD_BUS)

        FORGE_BUS.register(QuarryWorldEventHandler())

        runForDist(
            clientTarget = {
                MOD_BUS.addListener(DatQuarryMod::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(DatQuarryMod::onServerSetup)
                "test"
            })
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
    }


    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.log(Level.INFO, "Hello! This is working!")
    }

    @SubscribeEvent
    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModEntities.QUARRY_ENTITY.get()) { o, _ -> o }
    }
}