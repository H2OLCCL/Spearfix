package dev.h2o_cl.spearfix;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Re-runs the spear hit scan against entity positions rewound by the
 * interpolation lag. A remote entity is rendered up to a few ticks behind its
 * true position, so the attacker aims at the stale spot while the vanilla scan
 * only tests live bounding boxes. This re-tests the same ray against the boxes
 * the attacker actually saw.
 */
public final class LagScan {
   private LagScan() {
   }

   public static List<EntityHitResult> find(
      Entity source, AttackRange range, Predicate<Entity> matching, ClipContext.Block clipType, int rewindTicks
   ) {
      Level level = source.level();
      Vec3 origin = source.getEyePosition();
      Vec3 look = source.getHeadLookAngle();
      Vec3 from = origin.add(look.scale(range.effectiveMinRange(source)));
      double reach = range.effectiveMaxRange(source) + Math.max(0.0, source.getKnownMovement().dot(look));
      Vec3 to = origin.add(look.scale(reach));

      BlockHitResult blockHit = level.clipIncludingBorder(new ClipContext(origin, to, clipType, ClipContext.Fluid.NONE, source));
      if (blockHit.getType() != HitResult.Type.MISS) {
         if (origin.distanceToSqr(blockHit.getLocation()) < origin.distanceToSqr(from)) {
            return List.of();
         }
         to = blockHit.getLocation();
      }

      float margin = range.hitboxMargin();
      AABB searchArea = AABB.ofSize(from, margin, margin, margin).expandTowards(to.subtract(from)).inflate(1.0);
      List<EntityHitResult> found = new ArrayList<>();

      for (Entity entity : level.getEntities(source, searchArea, matching)) {
         AABB lagged = entity.getBoundingBox().move(entity.getDeltaMovement().scale(-rewindTicks));
         Vec3 point = hitPoint(level, source, clipType, margin, lagged, from, to);
         if (point != null) {
            found.add(new EntityHitResult(entity, point));
         }
      }

      found.sort(Comparator.comparingDouble(hit -> hit.getLocation().distanceToSqr(from)));
      return found;
   }

   private static Vec3 hitPoint(
      Level level, Entity source, ClipContext.Block clipType, float margin, AABB box, Vec3 from, Vec3 to
   ) {
      if (box.contains(from)) {
         return from;
      }

      Optional<Vec3> exact = box.clip(from, to);
      if (exact.isPresent()) {
         return exact.get();
      }

      if (margin <= 0.0F) {
         return null;
      }

      Optional<Vec3> outside = box.inflate(margin).clip(from, to);
      if (outside.isEmpty()) {
         return null;
      }

      Vec3 toward = box.getCenter();
      BlockHitResult blocking = level.clipIncludingBorder(new ClipContext(outside.get(), toward, clipType, ClipContext.Fluid.NONE, source));
      if (blocking.getType() != HitResult.Type.MISS) {
         toward = blocking.getLocation();
      }

      Optional<Vec3> surface = box.clip(outside.get(), toward);
      return surface.orElse(null);
   }
}
