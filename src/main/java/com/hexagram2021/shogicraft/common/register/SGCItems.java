package com.hexagram2021.shogicraft.common.register;

import com.hexagram2021.shogicraft.common.entities.KomaEntity;
import com.hexagram2021.shogicraft.common.items.KomaItem;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hexagram2021.shogicraft.ShogiCraft.MODID;
import static com.hexagram2021.shogicraft.ShogiCraft.SHOGI_CRAFT;

public class SGCItems {
	public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static final Map<KomaEntity.Type, ItemEntry<? extends Item>> KOMA_ITEMS = new EnumMap<>(KomaEntity.Type.class);


	public static void init(IEventBus bus) {
		REGISTER.register(bus);

		for(KomaEntity.Type type: KomaEntity.Type.values()) {
			KOMA_ITEMS.put(type, new ItemEntry<>(
					"koma_" + type.name().toLowerCase(Locale.ROOT),
					() -> new Item.Properties().tab(SHOGI_CRAFT),
					props -> new KomaItem(type, props)
			));
		}
	}

	public static class ItemEntry<T extends Item> implements Supplier<T>, IItemProvider {
		private final RegistryObject<T> regObject;
		private final Supplier<Item.Properties> properties;

		public ItemEntry(String name, Supplier<Item.Properties> properties, Function<Item.Properties, T> make) {
			this.properties = properties;
			this.regObject = REGISTER.register(name, () -> make.apply(properties.get()));
		}

		public Item.Properties getProperties() {
			return this.properties.get();
		}

		@Override
		public T get() {
			return this.regObject.get();
		}

		@Override @Nonnull
		public Item asItem() {
			return this.get().asItem();
		}
	}
}
