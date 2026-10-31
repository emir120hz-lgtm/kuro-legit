package kuro;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/**
 * Safe Anchor: yakindaki respawn anchor ile senin aranda engel yoksa, anchor'in sana bakan
 * yuzune bir blok (obsidyen tercih edilir) koyar -> patlama hasari azalir.
 * Legit: tepki suresi bekler, kamerayi gercekten cevirir, sadece nisan o yuzdeyken tiklar.
 * Nether'de anchor patlamadigi icin orada calismaz. Glowstone koymaz (anchor'i sarj etmesin).
 */
public final class SafeAnchor {
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   private static int cd;
   private static int react = -1;
   private static int idle;
   private static int restore = -1;
   private static int prevSlot = -1;
   private static Vec3d aimOff = Vec3d.ZERO;
   private static BlockPos lastAnchor;

   private SafeAnchor() {
   }

   public static void reset() {
      react = -1;
      idle = 0;
      lastAnchor = null;
      restoreSlot();
   }

   private static void restoreSlot() {
      if (prevSlot >= 0 && mc.player != null) {
         mc.player.getInventory().selectedSlot = prevSlot;
      }

      prevSlot = -1;
      restore = -1;
   }

   public static void tick() {
      ClientPlayerEntity p = mc.player;
      if (p == null || mc.world == null || mc.currentScreen != null || Freecam.active) {
         reset();
         return;
      }

      if (restore >= 0 && restore-- == 0) {
         restoreSlot();
      }

      if (cd > 0) {
         cd--;
      }

      if (mc.world.getRegistryKey() == World.NETHER) {
         reset();
         return;
      }

      Vec3d eye = p.getEyePos();
      BlockPos anchor = nearestAnchor(p, eye);
      if (anchor == null) {
         idleTick();
         return;
      }

      if (shielded(eye, anchor)) {
         idle = 0;
         return;
      }

      int slot = blockSlot(p);
      if (slot < 0) {
         return;
      }

      if (!ready()) {
         return;
      }

      Vec3d center = Vec3d.ofCenter(anchor);
      Direction d = Direction.getFacing(eye.x - center.x, eye.y - center.y, eye.z - center.z);
      if (!anchor.equals(lastAnchor)) {
         lastAnchor = anchor;
         aimOff = new Vec3d(Legit.noise(0.25F), Legit.noise(0.25F), Legit.noise(0.25F));
      }

      // yuzun ortasina (hafif sapmali) nisan al
      Vec3d aim = center.add(d.getOffsetX() * 0.5, d.getOffsetY() * 0.5, d.getOffsetZ() * 0.5);
      aim = aim.add(d.getOffsetX() == 0 ? aimOff.x : 0.0, d.getOffsetY() == 0 ? aimOff.y : 0.0, d.getOffsetZ() == 0 ? aimOff.z : 0.0);
      Legit.aim(aim, Config.saAimSpeed, Config.acAimNoise);

      HitResult hr = mc.crosshairTarget;
      if (cd <= 0 && hr instanceof BlockHitResult bhr && hr.getType() == HitResult.Type.BLOCK && bhr.getBlockPos().equals(anchor) && bhr.getSide() == d) {
         if (prevSlot < 0) {
            prevSlot = p.getInventory().selectedSlot;
         }

         p.getInventory().selectedSlot = slot;
         mc.interactionManager.interactBlock(p, Hand.MAIN_HAND, bhr);
         p.swingHand(Hand.MAIN_HAND);
         cd = Legit.rand(Config.saDelayMin, Config.saDelayMax);
         restore = 2;
      }
   }

   private static boolean ready() {
      idle = 0;
      if (react < 0) {
         react = Legit.rand(Config.saReactMin, Config.saReactMax);
      }

      if (react > 0) {
         react--;
         return false;
      }

      return true;
   }

   private static void idleTick() {
      if (++idle > 4) {
         react = -1;
         lastAnchor = null;
      }
   }

   private static BlockPos nearestAnchor(ClientPlayerEntity p, Vec3d eye) {
      double range = Config.saRange;
      int r = (int)Math.ceil(range);
      BlockPos o = p.getBlockPos();
      BlockPos best = null;
      double bd = range * range;
      boolean onlyCharged = Config.saOnlyCharged >= 0.5F;

      for (int dx = -r; dx <= r; dx++) {
         for (int dy = -r; dy <= r; dy++) {
            for (int dz = -r; dz <= r; dz++) {
               BlockPos pos = new BlockPos(o.getX() + dx, o.getY() + dy, o.getZ() + dz);
               BlockState st = mc.world.getBlockState(pos);
               if (!st.isOf(Blocks.RESPAWN_ANCHOR)) {
                  continue;
               }

               if (onlyCharged && st.get(RespawnAnchorBlock.CHARGES) <= 0) {
                  continue;
               }

               double d = Vec3d.ofCenter(pos).squaredDistanceTo(eye);
               if (d < bd) {
                  bd = d;
                  best = pos;
               }
            }
         }
      }

      return best;
   }

   /** Anchor ile goz arasinda baska bir blok varsa korunmus say. */
   private static boolean shielded(Vec3d eye, BlockPos anchor) {
      Vec3d c = Vec3d.ofCenter(anchor);
      BlockHitResult r = mc.world.raycast(new RaycastContext(eye, c, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player));
      return r.getType() == HitResult.Type.BLOCK && !r.getBlockPos().equals(anchor);
   }

   private static int blockSlot(ClientPlayerEntity p) {
      int any = -1;
      for (int i = 0; i < 9; i++) {
         ItemStack s = p.getInventory().getStack(i);
         if (s.isOf(Items.OBSIDIAN)) {
            return i;
         }

         if (any < 0 && s.getItem() instanceof BlockItem bi) {
            Block b = bi.getBlock();
            if (b != Blocks.GLOWSTONE && b != Blocks.RESPAWN_ANCHOR && !(b instanceof FallingBlock) && b.getDefaultState().isFullCube(mc.world, BlockPos.ORIGIN)) {
               any = i;
            }
         }
      }

      return any;
   }
}
