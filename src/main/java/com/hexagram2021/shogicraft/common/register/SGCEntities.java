package com.hexagram2021.shogicraft.common.register;

import com.hexagram2021.shogicraft.common.entities.KomaEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.hexagram2021.shogicraft.ShogiCraft.MODID;

public class SGCEntities {
	public static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

	public static final RegistryObject<EntityType<KomaEntity>> KOMA = REGISTER.register(
			"koma", () -> EntityType.Builder.<KomaEntity>of(KomaEntity::new, EntityClassification.MISC)
					.sized(0.8F, 0.3F)
					.clientTrackingRange(8)
					.build(new ResourceLocation(MODID, "koma").toString())
	);

	private SGCEntities() { }

	public static void init(IEventBus bus) {
		REGISTER.register(bus);
	}
}
