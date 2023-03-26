package com.hexagram2021.shogicraft.common.utils;

import com.hexagram2021.shogicraft.common.ShogiGame;
import com.hexagram2021.shogicraft.common.entities.KomaEntity;

import javax.annotation.Nullable;

public class ShogiGameEvaluator {
	// Max Depth of MCTS.
	private static final int MAX_DEPTH_MCTS = 5;

	/**
	 * @param
	 * 	game the shogi game to evaluate
	 * @return
	 * 	positive value: advantage for sente.
	 * 	negative value: advantage for gote.
	 * 	0: can be opening joseki or balanced advantage.
	 */
	public static int evaluate(ShogiGame game) {
		//TODO: Evaluate a shogi game by a deep neural network
		return 0;
	}

	/**
	 * @param
	 * 	game the shogi game to predict
	 * @return
	 * 	a legal move for the game. null for resign (touryo).
	 */
	@Nullable
	public static Move tryPredictMove(ShogiGame game) {
		//TODO: Predict next move
		return null;
	}

	public static class Pos {
		final int x;
		final int z;

		public Pos(int x, int z) {
			this.x = x;
			this.z = z;
		}

		public long toLong() {
			return asLong(this.x, this.z);
		}

		public static long asLong(int x, int z) {
			return (long)x & 0xfL | (((long)z & 0xfL) << 4);
		}

		@Override
		public int hashCode() {
			return (int)this.toLong();
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof Pos)) {
				return false;
			}
			Pos pos = (Pos)other;
			return this.x == pos.x && this.z == pos.z;
		}

		@Override
		public String toString() {
			return String.format("%d%d", this.x, this.z);
		}
	}

	public static class Move {
		final boolean sente;
		final Pos before;
		final Pos after;
		final KomaEntity.Type type;

		public Move(boolean sente, Pos before, Pos after, KomaEntity.Type type) {
			this.sente = sente;
			this.before = before;
			this.after = after;
			this.type = type;
		}

		public long toLong() {
			return asLong(this.type, this.before, this.after);
		}

		public static long asLong(KomaEntity.Type type, Pos before, Pos after) {
			return type.getOrdinal() | ((before.toLong() & 0xffL | ((after.toLong() & 0xffL) << 8)) << 8);
		}

		@Override
		public int hashCode() {
			return (int)this.toLong();
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof Move)) {
				return false;
			}
			Move move = (Move)other;
			return this.before.equals(move.before) && this.after.equals(move.after);
		}

		@Override
		public String toString() {
			return String.format("%c%s%s%s", this.sente ? '+' : '-', this.before, this.after, this.type);
		}
	}
}
