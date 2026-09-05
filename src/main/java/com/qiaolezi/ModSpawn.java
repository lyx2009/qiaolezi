package com.qiaolezi;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;

public class ModSpawn {
    
    public static void initialize() {
        BiomeModifications.create(Identifier.of(Qiaolezi.MOD_ID, "zhangxuefeng_spawn"))
            .add(
                ModificationPhase.ADDITIONS,
                BiomeSelectors.foundInTheNether(),
                context -> {
                    context.getSpawnSettings().addSpawn(
                        SpawnGroup.MONSTER,
                        new net.minecraft.world.biome.SpawnSettings.SpawnEntry(
                            ModEntities.ZHANGXUEFENG,
                            80,
                            1,
                            3
                        )
                    );
                }
            );
        
        Qiaolezi.LOGGER.info("张雪峰生成逻辑注册成功（仅下界）！");
    }
}