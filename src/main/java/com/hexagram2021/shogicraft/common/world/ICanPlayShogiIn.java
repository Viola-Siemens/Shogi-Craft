package com.hexagram2021.shogicraft.common.world;

import com.hexagram2021.shogicraft.common.ShogiGame;
import net.minecraft.util.math.BlockPos;

public interface ICanPlayShogiIn {
	ShogiGame getShogiGameAt(BlockPos blockPos);
}
