package com.hexagram2021.shogicraft.common;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShogiManager extends WorldSavedData {
	private final Int2ObjectMap<ShogiGame> shogiGames = new Int2ObjectOpenHashMap<>();
	public static final String SAVED_DATA_NAME = "Shogi-games";
	public static final String NEXT_AVAILABLE_ID_KEY = "NextAvailableID";
	public static final String TICK_KEY = "Tick";
	public static final String GAMES_KEY = "Games";

	private final ServerWorld level;
	private int nextAvailableID;
	private int tick;

	public ShogiManager(String name, ServerWorld level) {
		super(name);
		this.level = level;
		this.nextAvailableID = 0;
		this.setDirty();
	}

	public ShogiGame get(int index) {
		return this.shogiGames.get(index);
	}

	@Override
	public void load(CompoundNBT nbt) {
		this.nextAvailableID = nbt.getInt(NEXT_AVAILABLE_ID_KEY);
		this.tick = nbt.getInt(TICK_KEY);
		ListNBT games = nbt.getList(GAMES_KEY, Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < games.size(); ++i) {
			CompoundNBT compound = games.getCompound(i);
			ShogiGame game = new ShogiGame(this.level, compound);
			this.shogiGames.put(game.getId(), game);
		}
	}

	@Override @Nonnull
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.putInt(NEXT_AVAILABLE_ID_KEY, this.nextAvailableID);
		nbt.putInt(TICK_KEY, this.tick);
		ListNBT games = new ListNBT();

		for(ShogiGame game : this.shogiGames.values()) {
			CompoundNBT compoundnbt = new CompoundNBT();
			game.save(compoundnbt);
			games.add(compoundnbt);
		}

		nbt.put(GAMES_KEY, games);
		return nbt;
	}

	private int getUniqueId() {
		return ++this.nextAvailableID;
	}

	@Nullable
	public ShogiGame getNearbyGame(BlockPos blockPos, double maxDist) {
		ShogiGame ret = null;
		double d0 = maxDist * maxDist;

		for(ShogiGame game : this.shogiGames.values()) {
			double dist = game.getCenter().distSqr(blockPos);
			if (game.isActive() && dist < d0) {
				ret = game;
				d0 = dist;
			}
		}

		return ret;
	}
}
