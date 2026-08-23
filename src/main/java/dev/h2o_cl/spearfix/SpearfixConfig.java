package dev.h2o_cl.spearfix;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Tiny flat key=value loader for spearfix.toml. Written from scratch for this
 * mod; no library, no builder DSL, no ranges.
 */
public final class SpearfixConfig {
   public static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("spearfix.toml");

   private static final String DEFAULTS = """
      # Spearfix configuration (server side)

      # Rewind the hit scan by this many ticks, so a fast-moving target is hit
      # where the attacker actually sees it rendered (interpolation lag on
      # remote entities is up to 3 ticks).
      # 0 disables the feature and restores vanilla behaviour.
      rewindTicks = 3

      # In vanilla a spear charge tick that lands no effect at all still locks
      # the target into the contact cooldown, wasting the tick. With this on,
      # the cooldown only locks once an attack actually connects.
      refundMissedCooldown = true
      """;

   private static int rewindTicks = 3;
   private static boolean refundMissedCooldown = true;

   private SpearfixConfig() {
   }

   public static void load() {
      if (!Files.exists(FILE)) {
         writeDefaults();
         return;
      }

      try {
         for (String rawLine : Files.readAllLines(FILE, StandardCharsets.UTF_8)) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) {
               continue;
            }

            int eq = line.indexOf('=');
            if (eq <= 0) {
               continue;
            }

            String key = line.substring(0, eq).trim();
            String value = line.substring(eq + 1).trim();
            if (key.equals("rewindTicks")) {
               rewindTicks = clamp(value, 0, 4, 3);
            } else if (key.equals("refundMissedCooldown")) {
               refundMissedCooldown = value.equalsIgnoreCase("true") || value.equals("1");
            }
         }
      } catch (IOException ignored) {
      }
   }

   public static int rewindTicks() {
      return rewindTicks;
   }

   public static boolean refundMissedCooldown() {
      return refundMissedCooldown;
   }

   private static void writeDefaults() {
      try {
         Files.createDirectories(FILE.getParent());
         Files.writeString(FILE, DEFAULTS, StandardCharsets.UTF_8);
      } catch (IOException ignored) {
      }
   }

   private static int clamp(String raw, int min, int max, int fallback) {
      try {
         return Math.max(min, Math.min(max, Integer.parseInt(raw)));
      } catch (NumberFormatException e) {
         return fallback;
      }
   }
}
