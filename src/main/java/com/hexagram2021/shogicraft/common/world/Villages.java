package com.hexagram2021.shogicraft.common.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.hexagram2021.shogicraft.mixin.SingleJigsawAccess;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.hexagram2021.shogicraft.ShogiCraft.MODID;

// TODO: What about play shogi against villager? (#Unfinished#)
public class Villages {
	public static final ResourceLocation KISHI = new ResourceLocation(MODID, "kishi");

	public static void init() {

	}

	@SuppressWarnings("SameParameterValue")
	private static void addToPool(ResourceLocation pool, ResourceLocation toAdd, int weight) {
		JigsawPattern old = WorldGenRegistries.TEMPLATE_POOL.get(pool);
		//int id = WorldGenRegistries.TEMPLATE_POOL.getId(old);

		if(old != null) {
			List<JigsawPiece> shuffled = old.getShuffledTemplates(new Random(0));

			Object2IntMap<JigsawPiece> newPieces = new Object2IntLinkedOpenHashMap<>();
			for(JigsawPiece p : shuffled) {
				newPieces.computeInt(p, (JigsawPiece pTemp, Integer i) -> (i == null ? 0 : i) + 1);
			}
			newPieces.put(SingleJigsawAccess.construct(
					Either.left(toAdd), () -> ProcessorLists.EMPTY, JigsawPattern.PlacementBehaviour.RIGID
			), weight);
			List<Pair<JigsawPiece, Integer>> newPieceList = newPieces.object2IntEntrySet().stream()
					.map(e -> Pair.of(e.getKey(), e.getIntValue()))
					.collect(Collectors.toList());

			ResourceLocation name = old.getName();
			Registry.register(WorldGenRegistries.TEMPLATE_POOL, pool, new JigsawPattern(pool, name, newPieceList));
		}
	}

	public static class Registers {
		public static final DeferredRegister<PointOfInterestType> POINTS_OF_INTEREST = DeferredRegister.create(ForgeRegistries.POI_TYPES, MODID);
		public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, MODID);

		private static Collection<BlockState> assembleStates(Block block) {
			return block.getStateDefinition().getPossibleStates();
		}

		private static PointOfInterestType createPOI(String name, Collection<BlockState> block) {
			return new PointOfInterestType(MODID+":"+name, ImmutableSet.copyOf(block), 1, 1);
		}

		private static VillagerProfession createProf(ResourceLocation name, PointOfInterestType poi, ImmutableSet<Block> secondaryPoi, SoundEvent sound) {
			return new VillagerProfession(
					name.toString(), poi,
					ImmutableSet.<Item>builder().build(),
					secondaryPoi,
					sound
			);
		}

		public static void init(IEventBus bus) {
			POINTS_OF_INTEREST.register(bus);
			PROFESSIONS.register(bus);
		}
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
	public static class Events {
		@SubscribeEvent
		public static void registerTrades(VillagerTradesEvent event) {

		}
	}
}
