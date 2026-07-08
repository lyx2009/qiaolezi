package com.qiaolezi;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;      // 替代 ResourceLocation
import net.minecraft.util.Rarity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.util.Formatting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.entity.LivingEntity;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Qiaolezi implements ModInitializer {
	public static final String MOD_ID = "qiaolezi";

	public static final Item QIAOLEZI = new Item(new Item.Settings()
		.maxCount(64) // 最大堆叠64
		.food(new FoodComponent.Builder()
			.nutrition(4) // 恢复饱食度 4
			.saturationModifier(0.3f) // 饱和度 0.3
			.statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1000, 9), 1.0f) // 100%概率获得速度提升
			.statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 500, 0), 1.0f) // 100%概率获得力量效果
			.statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 2), 0.2f) // 20%概率获得中毒效果
			.statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 500, 2), 0.2f) // 20%概率获得反胃效果
			.statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 100, 2), 0.1f) // 10%概率获得瞬间伤害
			.snack() // 快速使用
			.alwaysEdible()
			.build()
			)
		.component(DataComponentTypes.LORE, new LoreComponent(
			List.of(
				Text.literal("你跑不过我你信不信").formatted(Formatting.GOLD)
			)
		))
		.rarity(Rarity.EPIC) 
	);
	// 巧乐兹逻辑代码

	public static final Item Spirit = new Item(new Item.Settings()
		.maxCount(16)
		.food (new FoodComponent.Builder()
			.nutrition(2)
			.saturationModifier(0.1f)
			.statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 500, 9), 1.0f) // 100%概率获得速度提升
			.statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 2), 0.2f) // 20%概率获得中毒效果
			.statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 500, 2), 0.2f) // 20%概率获得反胃效果
			.statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 100, 2), 0.05f) // 5%概率获得瞬间伤害

			.snack()
			.alwaysEdible()
			.build()

		)
		.component(DataComponentTypes.LORE, new LoreComponent(
			List.of(
				Text.literal("美味雪碧,喝了能见张雪峰").formatted(Formatting.GOLD)
			)
		))
		.rarity(Rarity.EPIC)

	){
		@Override
		public UseAction getUseAction(ItemStack stack) {
			return UseAction.DRINK;
		}
		@Override
		public int getMaxUseTime(ItemStack stack, LivingEntity user) {
			return 32;
		}
	};
	// 雪碧功能
	// 雪碧配方

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		// 注册物品 //
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "qiaolezi"), QIAOLEZI);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register(entries -> entries.add(QIAOLEZI));
		// 巧乐兹注册

		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "spirit"), Spirit);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
				.register(entries -> entries.add(Spirit));
		// 雪碧注册

	// 	BrewingRecipeRegistry.registerPotionRecipe(
	// 	Potions.WATER,
	// 	Items.SUGAR,
	// 	ModPotions.SPIRIT_POTION
	// );
	// 雪碧酿造台（弃）

	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}

// 菩萨保佑不出bug
// 这Fabric真他妈难写，下次用NeoForge
// 主要的物品逻辑都在这里