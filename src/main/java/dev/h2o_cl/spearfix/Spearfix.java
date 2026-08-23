package dev.h2o_cl.spearfix;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class Spearfix implements ModInitializer {
   public static final String MOD_ID = "spearfix";
   public static final Logger LOGGER = LogUtils.getLogger();

   @Override
   public void onInitialize() {
      SpearfixConfig.load();
      LOGGER.info(
         "[Spearfix] loaded: rewindTicks={}, refundMissedCooldown={}",
         SpearfixConfig.rewindTicks(),
         SpearfixConfig.refundMissedCooldown()
      );
   }
}
