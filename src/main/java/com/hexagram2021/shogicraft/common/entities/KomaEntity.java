package com.hexagram2021.shogicraft.common.entities;

import com.hexagram2021.shogicraft.common.register.SGCBlocks;
import com.hexagram2021.shogicraft.common.register.SGCEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;

public class KomaEntity extends Entity {
	private static final DataParameter<Boolean> DATA_SENTE = EntityDataManager.defineId(KomaEntity.class, DataSerializers.BOOLEAN);

	public KomaEntity(EntityType<? extends KomaEntity> type, World level) {
		super(type, level);
		this.noPhysics = true;
	}

	public KomaEntity(World level, double x, double y, double z, boolean sente) {
		super(SGCEntities.KOMA.get(), level);
		this.noPhysics = true;

		this.setPos(x, y, z);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setRotation(sente);
	}

	@Override
	public void tick() {
		super.tick();

		BlockPos blockpos = this.blockPosition();
		BlockState blockState = this.level.getBlockState(blockpos);
		if(!blockState.is(SGCBlocks.BOARD_BLOCK.get()) && !blockState.is(SGCBlocks.KOMA_DAI.get())) {
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

	}

	@Override
	protected void readAdditionalSaveData(@Nonnull CompoundNBT nbt) {

	}

	@Override
	protected void addAdditionalSaveData(@Nonnull CompoundNBT nbt) {

	}

	@Override @Nonnull
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public void setRotation(boolean sente) {
		this.entityData.set(DATA_SENTE, sente);
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
	}
}
