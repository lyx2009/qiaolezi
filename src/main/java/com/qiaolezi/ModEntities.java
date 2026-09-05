package com.qiaolezi;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
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

    public static void initialize() {
        // 注册属性 - 使用实体类中定义的属性
        FabricDefaultAttributeRegistry.register(
            ZHANGXUEFENG, 
            ZhangxuefengEntity.createZhangxuefengAttributes().build()
        );
        
        Qiaolezi.LOGGER.info("✅ 张雪峰实体注册成功！");
    }
}