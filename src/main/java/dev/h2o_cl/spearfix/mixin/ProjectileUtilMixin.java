package dev.h2o_cl.spearfix.mixin;

import com.mojang.datafixers.util.Either;
import dev.h2o_cl.spearfix.LagScan;
import dev.h2o_cl.spearfix.SpearfixConfig;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * When the vanilla spear scan comes up empty, retry it against entity boxes
 * rewound by the interpolation lag. Only spear scans use COLLIDER here; the
 * crosshair pick path uses OUTLINE and is left untouched.
 */
@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {
   @Inject(method = "getHitEntitiesAlong", at = @At("RETURN"), cancellable = true)
   private static void spearfix$rewindOnMiss(
      Entity source,
      AttackRange attackRange,
      Predicate<Entity> matching,
      ClipContext.Block clipType,
      CallbackInfoReturnable<Either<BlockHitResult, Collection<EntityHitResult>>> callback
   ) {
      int rewindTicks = SpearfixConfig.rewindTicks();
      if (rewindTicks <= 0 || clipType != ClipContext.Block.COLLIDER) {
         return;
      }

      boolean anyHit = callback.getReturnValue().map(left -> false, right -> !right.isEmpty());
      if (anyHit) {
         return;
      }

      List<EntityHitResult> rewound = LagScan.find(source, attackRange, matching, clipType, rewindTicks);
      if (!rewound.isEmpty()) {
         callback.setReturnValue(Either.right((Collection<EntityHitResult>)rewound));
      }
   }
}
