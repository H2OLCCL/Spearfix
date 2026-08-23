package dev.h2o_cl.spearfix.mixin;

import dev.h2o_cl.spearfix.SpearfixConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.KineticWeapon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Vanilla locks a target into the contact cooldown before the three charge
 * sub-checks run, so a tick that lands nothing still burns the cooldown.
 * Here the early lock is dropped and re-applied only when stabAttack actually
 * connects.
 */
@Mixin(KineticWeapon.class)
public abstract class KineticWeaponMixin {
   @Redirect(
      method = "damageEntities",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;rememberStabbedEntity(Lnet/minecraft/world/entity/Entity;)V"
      )
   )
   private void spearfix$skipEarlyLock(LivingEntity livingEntity, Entity target) {
      if (!SpearfixConfig.refundMissedCooldown()) {
         livingEntity.rememberStabbedEntity(target);
      }
   }

   @Redirect(
      method = "damageEntities",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;stabAttack(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/Entity;FZZZ)Z"
      )
   )
   private boolean spearfix$lockOnlyOnEffect(
      LivingEntity livingEntity, EquipmentSlot slot, Entity target, float baseDamage, boolean dealsDamage, boolean dealsKnockback, boolean dismounts
   ) {
      boolean hit = livingEntity.stabAttack(slot, target, baseDamage, dealsDamage, dealsKnockback, dismounts);
      if (hit && SpearfixConfig.refundMissedCooldown()) {
         livingEntity.rememberStabbedEntity(target);
      }
      return hit;
   }
}
