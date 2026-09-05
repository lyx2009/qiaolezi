package com.qiaolezi.client;

import com.qiaolezi.ModEntities;
import com.qiaolezi.Qiaolezi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class ModEntityRenderers implements ClientModInitializer {
    
    private static final Identifier ZHANGXUEFENG_TEXTURE = 
        Identifier.of(Qiaolezi.MOD_ID, "textures/entity/zhangxuefeng.png");
    
    @Override
    public void onInitializeClient() {
        // 注册实体渲染器
        EntityRendererRegistry.register(ModEntities.ZHANGXUEFENG, 
            context -> new ZombieEntityRenderer(
                context, 
                EntityModelLayers.ZOMBIE, 
                EntityModelLayers.ZOMBIE_INNER_ARMOR, 
                EntityModelLayers.ZOMBIE_OUTER_ARMOR
            ) {
                @Override
                public Identifier getTexture(net.minecraft.entity.mob.ZombieEntity entity) {
                    return ZHANGXUEFENG_TEXTURE;
                }
            }
        );
        
        Qiaolezi.LOGGER.info("张雪峰渲染器注册成功！");
    }
}