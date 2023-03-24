package com.hexagram2021.shogicraft;

import com.hexagram2021.shogicraft.common.SGCContent;
import com.hexagram2021.shogicraft.common.utils.SGCLogger;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;

@Mod(ShogiCraft.MODID)
public class ShogiCraft {
	public static final String MODID = "shogicraft";
	public static final String MODNAME = "Shogi Craft";
	public static final String VERSION = "${version}";

	public ShogiCraft() {
		SGCLogger.logger = LogManager.getLogger(MODID);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);
		SGCContent.modConstruction(bus);

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setup(FMLCommonSetupEvent event) {
		SGCTriggers.init();
		event.enqueueWork(() -> {
			Villages.init();
			SGCContent.init();
			ModVanillaCompat.setup();
		});
	}
}
