package com.hexagram2021.shogicraft.common.register;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.shogicraft.ShogiCraft.MODID;
import static com.hexagram2021.shogicraft.ShogiCraft.SHOGI_CRAFT;

public class SGCBlocks {
	public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

	public static final Supplier<AbstractBlock.Properties> BOARD_BLOCK_PROPERTIES = () ->
			AbstractBlock.Properties.of(Material.WOOD, MaterialColor.COLOR_YELLOW)
					.strength(2.0F).sound(SoundType.WOOD);
	public static final Supplier<AbstractBlock.Properties> KOMA_DAI_BLOCK_PROPERTIES = () ->
			AbstractBlock.Properties.of(Material.WOOD, MaterialColor.TERRACOTTA_BROWN)
					.strength(2.0F).sound(SoundType.WOOD);

	public static final BlockEntry<Block> BOARD_BLOCK = new BlockEntry<>("board_block", BOARD_BLOCK_PROPERTIES, Block::new);
	public static final BlockEntry<Block> BOARD_BLOCK_PLACEABLE = new BlockEntry<>("board_block_placeable", BOARD_BLOCK_PROPERTIES, Block::new);
	public static final BlockEntry<Block> KOMA_DAI_BLOCK = new BlockEntry<>("koma_dai_block", KOMA_DAI_BLOCK_PROPERTIES, Block::new);
	public static final BlockEntry<Block> KOMA_DAI_BLOCK_PLACEABLE = new BlockEntry<>("koma_dai_block_placeable", KOMA_DAI_BLOCK_PROPERTIES, Block::new);

	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		SGCItems.REGISTER.register(BOARD_BLOCK.getId().getPath(), () -> new BlockItem(
				BOARD_BLOCK.get(), new Item.Properties().tab(SHOGI_CRAFT)
		) {
			@Override
			public void appendHoverText(@Nonnull ItemStack itemStack, @Nullable World level, @Nonnull List<ITextComponent> components, @Nonnull ITooltipFlag tooltipFlag) {
				super.appendHoverText(itemStack, level, components, tooltipFlag);
				components.add(new TranslationTextComponent("desc.shogicraft.board_block").withStyle(TextFormatting.GRAY));
			}

			@Override
			public int getBurnTime(ItemStack itemStack, @Nullable IRecipeType<?> recipeType) {
				return 300;
			}
		});
		SGCItems.REGISTER.register(KOMA_DAI_BLOCK.getId().getPath(), () -> new BlockItem(
				KOMA_DAI_BLOCK.get(), new Item.Properties().tab(SHOGI_CRAFT)
		) {
			@Override
			public void appendHoverText(@Nonnull ItemStack itemStack, @Nullable World level, @Nonnull List<ITextComponent> components, @Nonnull ITooltipFlag tooltipFlag) {
				super.appendHoverText(itemStack, level, components, tooltipFlag);
				components.add(new TranslationTextComponent("desc.shogicraft.koma_dai_block").withStyle(TextFormatting.GRAY));
			}

			@Override
			public int getBurnTime(ItemStack itemStack, @Nullable IRecipeType<?> recipeType) {
				return 300;
			}
		});
	}

	public static final class BlockEntry<T extends Block> implements Supplier<T>, IItemProvider {
		private final RegistryObject<T> regObject;
		private final Supplier<AbstractBlock.Properties> properties;

		public static BlockEntry<Block> simple(String name, Supplier<AbstractBlock.Properties> properties, Consumer<Block> extra) {
			return new BlockEntry<>(name, properties, p -> Util.make(new Block(p), extra));
		}

		public BlockEntry(String name, Supplier<AbstractBlock.Properties> properties, Function<AbstractBlock.Properties, T> make) {
			this.properties = properties;
			this.regObject = REGISTER.register(name, () -> make.apply(properties.get()));
		}

		@Override
		public T get() {
			return this.regObject.get();
		}

		public BlockState defaultBlockState() {
			return this.get().defaultBlockState();
		}

		public ResourceLocation getId() {
			return this.regObject.getId();
		}

		public AbstractBlock.Properties getProperties() {
			return this.properties.get();
		}

		@Nonnull
		@Override
		public Item asItem() {
			return this.get().asItem();
		}
	}
}
