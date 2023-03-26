package com.hexagram2021.shogicraft.mixin;

import com.hexagram2021.shogicraft.common.ShogiGame;
import com.hexagram2021.shogicraft.common.ShogiManager;
import com.hexagram2021.shogicraft.common.world.ICanPlayShogiIn;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerWorld.class)
public class ServerLevelMixin implements ICanPlayShogiIn {
	protected ShogiManager shogiGames;

	@Override
	public ShogiGame getShogiGameAt(BlockPos blockPos) {
		return this.shogiGames.getNearbyGame(blockPos, 16.0D);
	}
}
