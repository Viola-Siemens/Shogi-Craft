package com.hexagram2021.shogicraft.common;

import com.hexagram2021.shogicraft.common.register.SGCBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;

public class ModVanillaCompat {
	public static void setup() {
		registerFlammable(SGCBlocks.BOARD_BLOCK.get(), 5, 5);
		registerFlammable(SGCBlocks.KOMA_DAI_BLOCK.get(), 5, 5);
	}

	private static final FireBlock fireblock = (FireBlock) Blocks.FIRE;

	public static void registerFlammable(Block blockIn, int encouragement, int flammability) {
		fireblock.setFlammable(blockIn, encouragement, flammability);
	}
}
