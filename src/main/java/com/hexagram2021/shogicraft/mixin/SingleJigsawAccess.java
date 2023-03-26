package com.hexagram2021.shogicraft.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.LegacySingleJigsawPiece;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.feature.template.Template;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Supplier;

@Mixin(LegacySingleJigsawPiece.class)
public interface SingleJigsawAccess {
	@Invoker("<init>")
	static LegacySingleJigsawPiece construct(Either<ResourceLocation, Template> pool,
											 Supplier<StructureProcessorList> structure,
											 JigsawPattern.PlacementBehaviour projection) {
		throw new UnsupportedOperationException("Replaced by Mixin");
	}
}
