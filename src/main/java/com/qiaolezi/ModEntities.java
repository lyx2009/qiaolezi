package com.qiaolezi;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    
    public static final EntityType<ZhangxuefengEntity> ZHANGXUEFENG = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Qiaolezi.MOD_ID, "zhangxuefeng"),
            EntityType.Builder.<ZhangxuefengEntity>create(ZhangxuefengEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6F, 1.95F)
                    .build()
    );

    // 张雪峰刷怪蛋（复用僵尸刷怪蛋配色：青 + 橄榄绿）
    public static final Item ZHANGXUEFENG_SPAWN_EGG = new SpawnEggItem(
            ZHANGXUEFENG,
            0x00AFAF,   // 主色
            0x799C65,   // 副色
            new Item.Settings()
    );

    public static void initialize() {
        // 注册属性 - 使用实体类中定义的属性
        FabricDefaultAttributeRegistry.register(
            ZHANGXUEFENG, 
            ZhangxuefengEntity.createZhangxuefengAttributes().build()
        );

        // 注册刷怪蛋并加入「刷怪蛋」创造栏
        Registry.register(Registries.ITEM, Identifier.of(Qiaolezi.MOD_ID, "zhangxuefeng_spawn_egg"), ZHANGXUEFENG_SPAWN_EGG);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS)
                .register(entries -> entries.add(ZHANGXUEFENG_SPAWN_EGG));
        
        Qiaolezi.LOGGER.info("✅ 张雪峰实体注册成功！");
    }
}