package com.qiaolezi;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ZhangxuefengEntity extends ZombieEntity {
    
    public ZhangxuefengEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
    }

    // 使用静态方法创建属性
    public static DefaultAttributeContainer.Builder createZhangxuefengAttributes() {
        return ZombieEntity.createZombieAttributes()
            .add(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
            .add(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
            .add(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
            .add(net.minecraft.entity.attribute.EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    // 重写掉落方法
    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        
        // 只有被玩家杀死时才掉落
        if (causedByPlayer && this.getWorld() instanceof ServerWorld) {
            // 必掉一个巧乐兹
            this.dropItem(Qiaolezi.QIAOLEZI);
            
            // 30%概率再次掉一个巧乐兹
            if (this.random.nextFloat() < 0.3f) {
                this.dropItem(Qiaolezi.QIAOLEZI);
            }
            
            // 40%概率掉落雪碧
            if (this.random.nextFloat() < 0.4f) {
                this.dropItem(Qiaolezi.Spirit);
            }
            
            // 25%概率掉落灵魂疾行靴子
            if (this.random.nextFloat() < 0.25f) {
                ItemStack boots = new ItemStack(Items.IRON_BOOTS);
                
                // 获取灵魂疾行附魔
                RegistryEntry<Enchantment> soulSpeed = this.getWorld().getRegistryManager()
                    .get(RegistryKeys.ENCHANTMENT)
                    .getEntry(Enchantments.SOUL_SPEED)
                    .orElseThrow();
                
                // 使用正确的方式创建附魔组件
                ItemEnchantmentsComponent.Builder enchantmentBuilder = new ItemEnchantmentsComponent.Builder(
                    ItemEnchantmentsComponent.DEFAULT
                );
                enchantmentBuilder.add(soulSpeed, 3);
                ItemEnchantmentsComponent enchantments = enchantmentBuilder.build();
                
                boots.set(DataComponentTypes.ENCHANTMENTS, enchantments);
                
                // 设置名称
                boots.set(DataComponentTypes.CUSTOM_NAME, Text.literal("张雪峰的跑鞋"));
                
                this.dropStack(boots);
            }
        }
    }
}