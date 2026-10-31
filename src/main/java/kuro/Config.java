package kuro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/** Kalici ayarlar (kuro_settings.txt). Tuslar eskisi gibi kuro_keys.txt icinde. */
public final class Config {
   public static int accent = 0;
   public static boolean watermark = true;
   public static boolean moduleList = true;
   public static boolean keybinds = true;
   public static boolean info = true;
   public static boolean toasts = true;

   public static float flyH = 0.8F;
   public static float flyV = 0.5F;
   public static float freecamSpeed = 0.6F;
   public static float crystalPlace = 1.0F;
   public static float crystalBreak = 0.0F;
   public static float anchorDelay = 2.0F;
   // AutoTotem (legit ayarlari)
   public static float totemMin = 3.0F;
   public static float totemMax = 7.0F;
   public static float totemHealth = 20.0F;
   public static float totemLegit = 1.0F;
   // yeni moduller
   public static float auraRange = 4.2F;
   public static float auraTurn = 45.0F;
   public static float speedValue = 0.35F;
   public static float velocityKeep = 0.0F;
   // AutoCrystal (legit)
   public static float acLegit = 1.0F;
   public static float acHold = 0.0F;
   public static float acReactMin = 3.0F;
   public static float acReactMax = 7.0F;
   public static float acPlaceMin = 2.0F;
   public static float acPlaceMax = 4.0F;
   public static float acBreakMin = 1.0F;
   public static float acBreakMax = 3.0F;
   public static float acAimSpeed = 22.0F;
   public static float acAimNoise = 0.6F;
   public static float acPlaceRange = 4.0F;
   public static float acTargetRange = 10.0F;
   public static float acMinDmg = 5.0F;
   public static float acMaxSelf = 6.0F;
   public static float acMiss = 0.0F;
   public static float acSwap = 0.0F;
   // SafeAnchor
   public static float saRange = 4.5F;
   public static float saOnlyCharged = 0.0F;
   public static float saReactMin = 3.0F;
   public static float saReactMax = 7.0F;
   public static float saDelayMin = 2.0F;
   public static float saDelayMax = 4.0F;
   public static float saAimSpeed = 22.0F;
   // makrolar (legit)
   public static float macroJitter = 1.0F;
   public static float macroMiss = 0.0F;

   private Config() {
   }

   static Path file() {
      return FabricLoader.getInstance().getGameDir().resolve("kuro_settings.txt");
   }

   public static void load() {
      Path f = file();
      if (!Files.exists(f)) {
         return;
      }

      try {
         for (String line : Files.readAllLines(f)) {
            String[] p = line.trim().split("=");
            if (p.length != 2) {
               continue;
            }

            String k = p[0].trim();
            String v = p[1].trim();
            try {
               switch (k) {
                  case "accent" -> accent = Math.max(0, Math.min(7, Integer.parseInt(v)));
                  case "watermark" -> watermark = Boolean.parseBoolean(v);
                  case "moduleList" -> moduleList = Boolean.parseBoolean(v);
                  case "keybinds" -> keybinds = Boolean.parseBoolean(v);
                  case "info" -> info = Boolean.parseBoolean(v);
                  case "toasts" -> toasts = Boolean.parseBoolean(v);
                  case "flyH" -> flyH = clamp(Float.parseFloat(v), 0.1F, 2.0F);
                  case "flyV" -> flyV = clamp(Float.parseFloat(v), 0.1F, 1.5F);
                  case "freecamSpeed" -> freecamSpeed = clamp(Float.parseFloat(v), 0.1F, 3.0F);
                  case "crystalPlace" -> crystalPlace = clamp(Float.parseFloat(v), 0.0F, 6.0F);
                  case "crystalBreak" -> crystalBreak = clamp(Float.parseFloat(v), 0.0F, 6.0F);
                  case "anchorDelay" -> anchorDelay = clamp(Float.parseFloat(v), 1.0F, 8.0F);
                  case "totemMin" -> totemMin = clamp(Float.parseFloat(v), 0.0F, 20.0F);
                  case "totemMax" -> totemMax = clamp(Float.parseFloat(v), 0.0F, 20.0F);
                  case "totemHealth" -> totemHealth = clamp(Float.parseFloat(v), 1.0F, 20.0F);
                  case "totemLegit" -> totemLegit = clamp(Float.parseFloat(v), 0.0F, 1.0F);
                  case "auraRange" -> auraRange = clamp(Float.parseFloat(v), 2.5F, 6.0F);
                  case "auraTurn" -> auraTurn = clamp(Float.parseFloat(v), 0.0F, 180.0F);
                  case "speedValue" -> speedValue = clamp(Float.parseFloat(v), 0.1F, 1.0F);
                  case "velocityKeep" -> velocityKeep = clamp(Float.parseFloat(v), 0.0F, 100.0F);
                  case "acLegit" -> acLegit = clamp(Float.parseFloat(v), 0.0F, 1.0F);
                  case "acHold" -> acHold = clamp(Float.parseFloat(v), 0.0F, 1.0F);
                  case "acReactMin" -> acReactMin = clamp(Float.parseFloat(v), 0.0F, 20.0F);
                  case "acReactMax" -> acReactMax = clamp(Float.parseFloat(v), 0.0F, 20.0F);
                  case "acPlaceMin" -> acPlaceMin = clamp(Float.parseFloat(v), 0.0F, 10.0F);
                  case "acPlaceMax" -> acPlaceMax = clamp(Float.parseFloat(v), 0.0F, 10.0F);
                  case "acBreakMin" -> acBreakMin = clamp(Float.parseFloat(v), 0.0F, 10.0F);
                  case "acBreakMax" -> acBreakMax = clamp(Float.parseFloat(v), 0.0F, 10.0F);
                  case "acAimSpeed" -> acAimSpeed = clamp(Float.parseFloat(v), 1.0F, 90.0F);
                  case "acAimNoise" -> acAimNoise = clamp(Float.parseFloat(v), 0.0F, 5.0F);
                  case "acPlaceRange" -> acPlaceRange = clamp(Float.parseFloat(v), 1.0F, 4.5F);
                  case "acTargetRange" -> acTargetRange = clamp(Float.parseFloat(v), 3.0F, 16.0F);
                  case "acMinDmg" -> acMinDmg = clamp(Float.parseFloat(v), 0.0F, 36.0F);
                  case "acMaxSelf" -> acMaxSelf = clamp(Float.parseFloat(v), 0.0F, 20.0F);
                  case "acMiss" -> acMiss = clamp(Float.parseFloat(v), 0.0F, 50.0F);
                  case "acSwap" -> acSwap = clamp(Float.parseFloat(v), 0.0F, 1.0F);
                  case "macroJitter" -> macroJitter = clamp(Float.parseFloat(v), 0.0F, 6.0F);
                  case "macroMiss" -> macroMiss = clamp(Float.parseFloat(v), 0.0F, 50.0F);
                  case "saRange" -> saRange = clamp(Float.parseFloat(v), 2.0F, 4.5F);
                  case "saOnlyCharged" -> saOnlyCharged = clamp(Float.parseFloat(v), 0.0F, 1.0F);
                  case "saReactMin" -> saReactMin = clamp(Float.parseFloat(v), 0.0F, 20.0F);
                  case "saReactMax" -> saReactMax = clamp(Float.parseFloat(v), 0.0F, 20.0F);
                  case "saDelayMin" -> saDelayMin = clamp(Float.parseFloat(v), 0.0F, 10.0F);
                  case "saDelayMax" -> saDelayMax = clamp(Float.parseFloat(v), 0.0F, 10.0F);
                  case "saAimSpeed" -> saAimSpeed = clamp(Float.parseFloat(v), 1.0F, 90.0F);
                  default -> {
                  }
               }
            } catch (NumberFormatException ignored) {
            }
         }
      } catch (IOException ignored) {
      }
   }

   public static void save() {
      String s = "accent=" + accent + "\n"
         + "watermark=" + watermark + "\n"
         + "moduleList=" + moduleList + "\n"
         + "keybinds=" + keybinds + "\n"
         + "info=" + info + "\n"
         + "toasts=" + toasts + "\n"
         + "flyH=" + flyH + "\n"
         + "flyV=" + flyV + "\n"
         + "freecamSpeed=" + freecamSpeed + "\n"
         + "crystalPlace=" + crystalPlace + "\n"
         + "crystalBreak=" + crystalBreak + "\n"
         + "anchorDelay=" + anchorDelay + "\n"
         + "totemMin=" + totemMin + "\n"
         + "totemMax=" + totemMax + "\n"
         + "totemHealth=" + totemHealth + "\n"
         + "totemLegit=" + totemLegit + "\n"
         + "auraRange=" + auraRange + "\n"
         + "auraTurn=" + auraTurn + "\n"
         + "speedValue=" + speedValue + "\n"
         + "velocityKeep=" + velocityKeep + "\n"
         + "acLegit=" + acLegit + "\n"
         + "acHold=" + acHold + "\n"
         + "acReactMin=" + acReactMin + "\n"
         + "acReactMax=" + acReactMax + "\n"
         + "acPlaceMin=" + acPlaceMin + "\n"
         + "acPlaceMax=" + acPlaceMax + "\n"
         + "acBreakMin=" + acBreakMin + "\n"
         + "acBreakMax=" + acBreakMax + "\n"
         + "acAimSpeed=" + acAimSpeed + "\n"
         + "acAimNoise=" + acAimNoise + "\n"
         + "acPlaceRange=" + acPlaceRange + "\n"
         + "acTargetRange=" + acTargetRange + "\n"
         + "acMinDmg=" + acMinDmg + "\n"
         + "acMaxSelf=" + acMaxSelf + "\n"
         + "acMiss=" + acMiss + "\n"
         + "acSwap=" + acSwap + "\n"
         + "macroJitter=" + macroJitter + "\n"
         + "macroMiss=" + macroMiss + "\n"
         + "saRange=" + saRange + "\n"
         + "saOnlyCharged=" + saOnlyCharged + "\n"
         + "saReactMin=" + saReactMin + "\n"
         + "saReactMax=" + saReactMax + "\n"
         + "saDelayMin=" + saDelayMin + "\n"
         + "saDelayMax=" + saDelayMax + "\n"
         + "saAimSpeed=" + saAimSpeed + "\n";
      try {
         Files.writeString(file(), s);
      } catch (IOException ignored) {
      }
   }

   static float clamp(float v, float a, float b) {
      return Math.max(a, Math.min(b, v));
   }
}
