package com.hexagram2021.shogicraft.common.items;

import com.hexagram2021.shogicraft.common.entities.KomaEntity;
import com.hexagram2021.shogicraft.common.register.SGCBlocks;
import com.hexagram2021.shogicraft.common.register.SGCEntities;
import com.hexagram2021.shogicraft.common.register.SGCItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Objects;

public class KomaItem extends Item {
	protected final KomaEntity.Type type;

	public KomaItem(KomaEntity.Type type, Properties properties) {
		super(properties);
		this.type = type;
	}

	@Override
	public boolean useOnRelease(@Nonnull ItemStack itemStack) {
		return true;
	}

	@Override @Nonnull
	public ActionResultType useOn(ItemUseContext useContext) {
		World level = useContext.getLevel();
		if (!(level instanceof ServerWorld)) {
			return ActionResultType.SUCCESS;
		}
		ServerWorld serverlevel = (ServerWorld) level;
		ItemStack itemstack = useContext.getItemInHand();
		BlockPos blockpos = useContext.getClickedPos();
		Direction direction = useContext.getClickedFace();
		PlayerEntity player = useContext.getPlayer();
		BlockState blockstate = serverlevel.getBlockState(blockpos);
		CompoundNBT nbt = itemstack.getTag();

		if((!blockstate.is(SGCBlocks.BOARD_BLOCK_PLACEABLE.get()) && !blockstate.is(SGCBlocks.KOMA_DAI_BLOCK_PLACEABLE.get())) ||
				!direction.equals(Direction.UP)) {
			return ActionResultType.FAIL;
		}
		if(player != null && player.isSecondaryUseActive()) {
			return ActionResultType.PASS;
		}

		BlockPos targetPos;
		if (blockstate.getCollisionShape(serverlevel, blockpos).isEmpty()) {
			targetPos = blockpos;
		} else {
			targetPos = blockpos.relative(direction);
		}

		KomaEntity koma = (KomaEntity)SGCEntities.KOMA.get().spawn(serverlevel, itemstack, player, targetPos, SpawnReason.SPAWN_EGG, true, !Objects.equals(blockpos, targetPos));
		if (koma != null) {
			koma.setKomaType(this.type);
			if(nbt != null) {
				if(nbt.contains("KomaDirection", Constants.NBT.TAG_STRING)) {
					Direction directPlacement = Direction.byName(nbt.getString("KomaDirection"));
					if (directPlacement != null) {
						koma.setKomaDefaultRotation(directPlacement);
					}
				}
				if(nbt.contains("KomaSente", Constants.NBT.TAG_BYTE)) {
					koma.setKomaRotation(nbt.getBoolean("KomaSente"));
				}
			}
			itemstack.shrink(1);
		}
		return ActionResultType.CONSUME;
	}

	@Override @Nonnull
	public ActionResult<ItemStack> use(@Nonnull World level, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		KomaEntity.Type promoteTo = this.type.getPromoteTo();
		if(!level.isClientSide && promoteTo != null && player.isSecondaryUseActive()) {
			player.setItemInHand(hand, new ItemStack(SGCItems.KOMA_ITEMS.get(promoteTo), itemstack.getCount()));
			return ActionResult.pass(itemstack);
		}
		return ActionResult.pass(itemstack);
	}
}
