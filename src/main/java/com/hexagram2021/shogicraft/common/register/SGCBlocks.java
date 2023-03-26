package com.hexagram2021.shogicraft.common.register;

import net.minecraft.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.hexagram2021.shogicraft.ShogiCraft.MODID;

public class SGCBlocks {
	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
