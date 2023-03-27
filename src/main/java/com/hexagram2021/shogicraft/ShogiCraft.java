package com.hexagram2021.shogicraft;

import com.hexagram2021.shogicraft.common.ModVanillaCompat;
import com.hexagram2021.shogicraft.common.SGCContent;
import com.hexagram2021.shogicraft.common.entities.KomaEntity;
import com.hexagram2021.shogicraft.common.register.SGCItems;
import com.hexagram2021.shogicraft.common.register.SGCTriggers;
import com.hexagram2021.shogicraft.common.utils.SGCLogger;
import com.hexagram2021.shogicraft.common.world.Villages;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
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

	public static final ItemGroup SHOGI_CRAFT = new ItemGroup(MODID) {
		@Override @Nonnull
		public ItemStack makeIcon() {
			return new ItemStack(SGCItems.KOMA_ITEMS.get(KomaEntity.Type.OU));
		}
	};
}
