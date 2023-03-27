package com.hexagram2021.shogicraft.common.entities;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.shogicraft.common.register.SGCBlocks;
import com.hexagram2021.shogicraft.common.register.SGCEntities;
import com.hexagram2021.shogicraft.common.register.SGCItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class KomaEntity extends Entity {
	private static final DataParameter<Integer> DATA_TYPE = EntityDataManager.defineId(KomaEntity.class, DataSerializers.INT);
	private static final DataParameter<Boolean> DATA_SENTE = EntityDataManager.defineId(KomaEntity.class, DataSerializers.BOOLEAN);

	public static final List<List<IPossibleMove>> POSSIBLE_MOVES_BY_TYPE = new ArrayList<>();

	public KomaEntity(EntityType<? extends KomaEntity> type, World level) {
		super(type, level);
		this.noPhysics = true;
		this.setCustomNameVisible(false);
	}

	public KomaEntity(World level, double x, double y, double z, Type type, boolean sente) {
		super(SGCEntities.KOMA.get(), level);
		this.noPhysics = true;
		this.setCustomNameVisible(false);

		this.setPos(x, y, z);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setKomaType(type);
		this.setKomaRotation(sente);
	}

	@Override
	public void tick() {
		super.tick();

		BlockPos blockpos = this.blockPosition();
		BlockState blockState = this.level.getBlockState(blockpos);
		if(!blockState.is(SGCBlocks.BOARD_BLOCK.get()) && !blockState.is(SGCBlocks.KOMA_DAI_BLOCK.get())) {
			this.remove();
		}
		this.setPosAndOldPos((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.375D, (double)blockpos.getZ() + 0.5D);
		if (this.isAddedToWorld() && this.level instanceof ServerWorld) {
			((ServerWorld)this.level).updateChunkPos(this);
		}
		this.setBoundingBox(new AxisAlignedBB(this.getX() - 0.4D, this.getY() + 0.375D, this.getZ() - 0.4D, this.getX() + 0.4D, this.getY() + 0.625D, this.getZ() + 0.4D));
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_TYPE, Type.OU.getOrdinal());
		this.entityData.define(DATA_SENTE, true);
	}

	@Override
	protected void readAdditionalSaveData(@Nonnull CompoundNBT nbt) {
		if(nbt.contains("Type", Constants.NBT.TAG_INT)) {
			this.setKomaType(Type.BY_ID[nbt.getInt("Type")]);
		}
		if(nbt.contains("Sente", Constants.NBT.TAG_BYTE)) {
			this.setKomaRotation(nbt.getBoolean("Sente"));
		}
	}

	@Override
	protected void addAdditionalSaveData(@Nonnull CompoundNBT nbt) {
		nbt.putInt("Type", this.getKomaType().getOrdinal());
		nbt.putBoolean("Sente", this.getKomaRotation());
	}

	@Override @Nonnull
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override @Nonnull
	public ActionResultType interact(PlayerEntity player, @Nonnull Hand hand) {
		if(player.level.isClientSide) {
			return ActionResultType.SUCCESS;
		}
		ItemStack itemStack = player.getItemInHand(hand);
		if(itemStack.isEmpty()) {
			ItemStack komaItemStack = new ItemStack(SGCItems.KOMA_ITEMS.get(this.getKomaType()));
			CompoundNBT nbt = komaItemStack.getOrCreateTag();
			nbt.putString("KomaDirection", Direction.fromYRot(this.yRot).getName());
			nbt.putBoolean("KomaSente", this.getKomaRotation());

			player.setItemInHand(hand, komaItemStack);
			this.remove();

			return ActionResultType.SUCCESS;
		}
		return super.interact(player, hand);
	}

	public Type getKomaType() {
		return Type.BY_ID[this.entityData.get(DATA_TYPE)];
	}

	public void setKomaType(Type type) {
		this.entityData.set(DATA_TYPE, type.getOrdinal());
	}

	public boolean getKomaRotation() {
		return this.entityData.get(DATA_SENTE);
	}

	public void setKomaRotation(boolean sente) {
		this.entityData.set(DATA_SENTE, sente);
	}

	public void setKomaDefaultRotation(Direction face) {
		this.setRot(face.toYRot(), 0.0F);
	}

	public enum Type {
		OU(13, null),		//王将
		RY(12, null),		//龍王
		UM(11, null),		//龍馬
		NG(10, null),		//成銀
		NK(9, null),		//成桂
		NY(8, null),		//成香
		TO(7, null),		//と金
		HI(6, RY),					//飛車
		KA(5, UM),					//角行
		KI(4, null),		//金将
		GI(3, NG),					//銀将
		KE(2, NK),					//桂馬
		KY(1, NY),					//香車
		FU(0, TO);					//歩兵

		public static final Type[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Type::getOrdinal)).toArray(Type[]::new);

		private final int id;

		@Nullable
		private final Type promoteTo;

		Type(int id, @Nullable Type promoteTo) {
			this.id = id;
			this.promoteTo = promoteTo;
		}

		public int getOrdinal() {
			return this.id;
		}

		@Nullable
		public Type getPromoteTo() {
			return this.promoteTo;
		}
	}

	public interface IPossibleMove {
		void markPossibleMoves(int x, int z, BiFunction<Integer, Integer, Boolean> hasKoma, BiConsumer<Integer, Integer> marker);

		static boolean isXZLegal(int v) {
			return v >= 1 && v <= 9;
		}
	}

	public static class DirectMove implements IPossibleMove {
		final int dx;
		final int dz;

		public DirectMove(int dx, int dz) {
			this.dx = dx;
			this.dz = dz;
		}

		@Override
		public void markPossibleMoves(int x, int z, BiFunction<Integer, Integer, Boolean> hasKoma, BiConsumer<Integer, Integer> marker) {
			int markX = x + this.dx;
			int markZ = z + this.dz;
			if(IPossibleMove.isXZLegal(markX) && IPossibleMove.isXZLegal(markZ)) {
				marker.accept(markX, markZ);
			}
		}
	}

	public static class LineMove implements IPossibleMove {
		final int dx;
		final int dz;

		public LineMove(int dx, int dz) {
			this.dx = dx;
			this.dz = dz;
		}

		@Override
		public void markPossibleMoves(int x, int z, BiFunction<Integer, Integer, Boolean> hasKoma, BiConsumer<Integer, Integer> marker) {
			for(int i = 1; i < 9; ++i) {
				int markX = x + i * this.dx;
				int markZ = z + i * this.dz;
				if(IPossibleMove.isXZLegal(markX) && IPossibleMove.isXZLegal(markZ)) {
					marker.accept(markX, markZ);
					if(hasKoma.apply(markX, markZ)) {
						break;
					}
				} else {
					break;
				}
			}
		}
	}

	static {
		ImmutableList<IPossibleMove> kin_moves = ImmutableList.of(
				new DirectMove(1, 1),	new DirectMove(0, 1),	new DirectMove(-1, 1),
				new DirectMove(1, 0),									new DirectMove(-1, 0),
												new DirectMove(0, -1)
		);
		POSSIBLE_MOVES_BY_TYPE.add(Type.FU.getOrdinal(), ImmutableList.of(new DirectMove(0, 1)));
		POSSIBLE_MOVES_BY_TYPE.add(Type.KY.getOrdinal(), ImmutableList.of(new LineMove(0, 1)));
		POSSIBLE_MOVES_BY_TYPE.add(Type.KE.getOrdinal(), ImmutableList.of(new DirectMove(1, 2), new DirectMove(-1, 2)));
		POSSIBLE_MOVES_BY_TYPE.add(Type.GI.getOrdinal(), ImmutableList.of(
				new DirectMove(1, 1),	new DirectMove(0, 1),	new DirectMove(-1, 1),
				new DirectMove(1, -1),									new DirectMove(-1, -1)
		));
		POSSIBLE_MOVES_BY_TYPE.add(Type.KI.getOrdinal(), kin_moves);
		POSSIBLE_MOVES_BY_TYPE.add(Type.KA.getOrdinal(), ImmutableList.of(
				new LineMove(1, 1),		new LineMove(-1, 1),
				new LineMove(1, -1),	new LineMove(-1, -1)
		));
		POSSIBLE_MOVES_BY_TYPE.add(Type.HI.getOrdinal(), ImmutableList.of(
				new LineMove(1, 0),		new LineMove(-1, 0),
				new LineMove(0, 1),		new LineMove(0, -1)
		));
		POSSIBLE_MOVES_BY_TYPE.add(Type.TO.getOrdinal(), kin_moves);
		POSSIBLE_MOVES_BY_TYPE.add(Type.NY.getOrdinal(), kin_moves);
		POSSIBLE_MOVES_BY_TYPE.add(Type.NK.getOrdinal(), kin_moves);
		POSSIBLE_MOVES_BY_TYPE.add(Type.NG.getOrdinal(), kin_moves);
		POSSIBLE_MOVES_BY_TYPE.add(Type.UM.getOrdinal(), ImmutableList.of(
				new LineMove(1, 1),		new DirectMove(0, 1),	new LineMove(-1, 1),
				new DirectMove(1, 0),									new DirectMove(-1, 0),
				new LineMove(1, -1),	new DirectMove(0, -1),	new LineMove(-1, -1)
		));
		POSSIBLE_MOVES_BY_TYPE.add(Type.RY.getOrdinal(), ImmutableList.of(
				new DirectMove(1, 1),	new LineMove(0, 1),		new DirectMove(-1, 1),
				new LineMove(1, 0),										new LineMove(-1, 0),
				new DirectMove(1, -1),	new LineMove(0, -1),	new DirectMove(-1, -1)
		));
		POSSIBLE_MOVES_BY_TYPE.add(Type.OU.getOrdinal(), ImmutableList.of(
				new DirectMove(1, 1),	new DirectMove(0, 1),	new DirectMove(-1, 1),
				new DirectMove(1, 0),									new DirectMove(-1, 0),
				new DirectMove(1, -1),	new DirectMove(0, -1),	new DirectMove(-1, -1)
		));
	}
}
