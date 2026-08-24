package dev.h2o_cl.spearfix.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu entry point. Only loaded when ModMenu is installed (it is the only
 * consumer of the "modmenu" entrypoint), so Spearfix works fine without it.
 */
public class SpearfixModMenu implements ModMenuApi {
   @Override
   public ConfigScreenFactory<?> getModConfigScreenFactory() {
      return SpearfixConfigScreen::new;
   }
}
