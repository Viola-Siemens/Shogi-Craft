package com.hexagram2021.shogicraft.common;

import com.hexagram2021.shogicraft.common.register.SGCBlocks;
import com.hexagram2021.shogicraft.common.register.SGCEntities;
import com.hexagram2021.shogicraft.common.register.SGCItems;
import com.hexagram2021.shogicraft.common.world.Villages;
import net.minecraftforge.eventbus.api.IEventBus;

public class SGCContent {
	public static void init() {

	}

	public static void modConstruction(IEventBus bus) {
		SGCBlocks.init(bus);
		SGCItems.init(bus);
		Villages.Registers.init(bus);
		SGCEntities.init(bus);
	}
}
