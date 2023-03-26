package com.hexagram2021.shogicraft.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hexagram2021.shogicraft.common.entities.IShogiKishi;
import com.hexagram2021.shogicraft.common.entities.KomaEntity;
import com.hexagram2021.shogicraft.common.utils.ShogiGameEvaluator;
import com.hexagram2021.shogicraft.common.world.ICanPlayShogiIn;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

public class ShogiGame {
	private static final ITextComponent GAME_NAME_COMPONENT = new TranslationTextComponent("event.shogicraft.game");
	private static final ITextComponent SENTE_ADVANTAGE = new TranslationTextComponent("event.shogicraft.game.sente_advantage");
	private static final ITextComponent GOTE_ADVANTAGE = new TranslationTextComponent("event.shogicraft.game.gote_advantage");
	private static final ITextComponent VICTORY = new TranslationTextComponent("event.shogicraft.game.victory");
	private static final ITextComponent DEFEAT = new TranslationTextComponent("event.shogicraft.game.defeat");
	private static final ITextComponent GAME_BAR_SENTE_ADVANTAGE_COMPONENT = GAME_NAME_COMPONENT.copy().append(" - ").append(SENTE_ADVANTAGE);
	private static final ITextComponent GAME_BAR_GOTE_ADVANTAGE_COMPONENT = GAME_NAME_COMPONENT.copy().append(" - ").append(GOTE_ADVANTAGE);
	private static final ITextComponent GAME_BAR_VICTORY_COMPONENT = GAME_NAME_COMPONENT.copy().append(" - ").append(VICTORY);
	private static final ITextComponent GAME_BAR_DEFEAT_COMPONENT = GAME_NAME_COMPONENT.copy().append(" - ").append(DEFEAT);

	private final int id;
	private IShogiKishi sente;
	private IShogiKishi gote;
	private final List<KomaEntity> komas = Lists.newArrayList();
	private boolean active;

	private long ticksSingleMove;
	private BlockPos center;		// 5,5 - center of the board.
	private final ServerWorld level;
	private Status status;
	private final ServerBossInfo gameEvent = new ServerBossInfo(GAME_NAME_COMPONENT, BossInfo.Color.GREEN, BossInfo.Overlay.NOTCHED_6);
	private double advantage;

	public ShogiGame(int id, ServerWorld level, BlockPos blockPos) {
		this.id = id;
		this.active = true;
		this.level = level;
		this.gameEvent.setPercent(0.0F);
		this.center = blockPos;
		this.status = Status.JOSEKI;
	}

	public ShogiGame(ServerWorld level, CompoundNBT nbt) {
		this.level = level;
		this.id = nbt.getInt("Id");
		this.active = nbt.getBoolean("Active");
		this.ticksSingleMove = nbt.getLong("TicksSingleMove");
		this.center = new BlockPos(nbt.getInt("CX"), nbt.getInt("CY"), nbt.getInt("CZ"));
		this.status = Status.getByName(nbt.getString("Status"));
		this.advantage = nbt.getDouble("Advantage");
	}

	@SuppressWarnings("UnusedReturnValue")
	@Nonnull
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.putInt("Id", this.id);
		nbt.putBoolean("Active", this.active);
		nbt.putLong("TicksSingleMove", this.ticksSingleMove);
		nbt.putInt("CX", this.center.getX());
		nbt.putInt("CY", this.center.getY());
		nbt.putInt("CZ", this.center.getZ());
		nbt.putString("Status", this.status.getName());
		nbt.putDouble("Advantage", this.advantage);

		return nbt;
	}

	public int getId() {
		return this.id;
	}

	public ServerWorld getLevel() {
		return this.level;
	}

	public BlockPos getCenter() {
		return this.center;
	}

	private void setCenter(BlockPos blockPos) {
		this.center = blockPos;
	}

	private void updateAdvantage() {
		int value = ShogiGameEvaluator.evaluate(this);
		this.advantage = sigmoid(value * 0.01D);
	}

	public void stop() {
		this.active = false;
		this.gameEvent.removeAllPlayers();
		this.status = Status.STOP;
	}

	public boolean isStopped() {
		return this.status == Status.STOP;
	}

	public boolean isVictory(IShogiKishi shogiKishi) {
		return this.status == Status.END && ((this.advantage > 0) ^ (this.sente == shogiKishi));
	}

	public boolean isLoss(IShogiKishi shogiKishi) {
		return this.status == Status.END && ((this.advantage < 0) ^ (this.sente == shogiKishi));
	}

	public boolean isActive() {
		return this.active;
	}

	public void tick() {
		if(this.isStopped()) {
			return;
		}

	}

	private Predicate<ServerPlayerEntity> validPlayer() {
		return player -> {
			BlockPos blockpos = player.blockPosition();
			return player.isAlive() && ((ICanPlayShogiIn)this.level).getShogiGameAt(blockpos) == this;
		};
	}

	private void updatePlayers() {
		Set<ServerPlayerEntity> set = Sets.newHashSet(this.gameEvent.getPlayers());
		List<ServerPlayerEntity> list = this.level.getPlayers(this.validPlayer());

		for(ServerPlayerEntity serverPlayer : list) {
			if (!set.contains(serverPlayer)) {
				this.gameEvent.addPlayer(serverPlayer);
			}
		}

		for(ServerPlayerEntity serverPlayer : set) {
			if (!list.contains(serverPlayer)) {
				this.gameEvent.removePlayer(serverPlayer);
			}
		}
	}

	public static double sigmoid(double value) {
		return 1.0D / (1.0D + Math.exp(-value));
	}

	public enum Status {
		JOSEKI,
		MIDDLE,
		FINAL,
		END,
		STOP;

		private static final ShogiGame.Status[] VALUES = values();

		private static ShogiGame.Status getByName(String name) {
			for(ShogiGame.Status status : VALUES) {
				if (name.equalsIgnoreCase(status.name())) {
					return status;
				}
			}

			return JOSEKI;
		}

		public String getName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
