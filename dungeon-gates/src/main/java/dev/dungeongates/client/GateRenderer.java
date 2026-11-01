package dev.dungeongates.client;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.dungeongates.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class GateRenderer implements BlockEntityRenderer<GateAnchor> {
  public GateRenderer(BlockEntityRendererProvider.Context c) {}

  public boolean shouldRenderOffScreen(GateAnchor a) {
    return true;
  }

  public int getViewDistance() {
    return 48;
  }

  public void render(
      GateAnchor a,
      float partial,
      PoseStack pose,
      MultiBufferSource buffers,
      int light,
      int overlay) {
    var kind = a.kind();
    long time = a.getLevel().getGameTime();
    pose.pushPose();
    pose.translate(.5, 0, .5);
    pose.mulPose(Axis.YP.rotationDegrees(-a.getBlockState().getValue(GateBlock.FACING).toYRot()));
    if (kind == GateKind.EMERALD) {
      plane(
          pose,
          buffers,
          "emerald_portal_distortion_anim.png",
          (int) (time / 3 % 8),
          8,
          -.006f,
          3,
          4.5f);
      plane(
          pose, buffers, "emerald_portal_surface_anim.png", (int) (time / 2 % 16), 16, 0, 3, 4.5f);
      plane(
          pose,
          buffers,
          "emerald_portal_border_anim.png",
          (int) (time / 2 % 16),
          16,
          .006f,
          3,
          4.5f);
    } else
      ellipse(
          pose, buffers, "frozen_portal_surface_strip.png", (int) (time / 2 % 8), 8, 0, 3, 4.8125f);
    if (kind == GateKind.EMERALD) runes(pose, buffers, time);
    pose.popPose();
    var mc = Minecraft.getInstance();
    if (mc.player == null
        || mc.player.distanceToSqr(
                a.getBlockPos().getX() + .5, a.getBlockPos().getY(), a.getBlockPos().getZ() + .5)
            > 40 * 40) return;
    pose.pushPose();
    pose.translate(.5, kind == GateKind.EMERALD ? 7.2 : 6.4, .5);
    pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
    pose.scale(-.025f, -.025f, .025f);
    Component[] text = {
      Component.translatable("dg.level", 1),
      Component.translatable("dg.gate", kind.number),
      Component.translatable("dg.name." + kind.id),
      Component.translatable("dg.category", kind.category())
    };
    for (int i = 0; i < 4; i++) {
      int color = i == 3 && a.exit && !a.unlocked ? 0xFFFF9090 : 0xFF000000 | kind.color;
      mc.font.drawInBatch(
          text[i],
          -mc.font.width(text[i]) / 2f,
          i * 11,
          color,
          false,
          pose.last().pose(),
          buffers,
          Font.DisplayMode.NORMAL,
          0x66000000,
          LightTexture.FULL_BRIGHT);
    }
    pose.popPose();
  }

  static void runes(PoseStack pose, MultiBufferSource buffers, long time) {
    var consumer =
        buffers.getBuffer(
            RenderType.entityTranslucent(
                new ResourceLocation(
                    "dungeongates", "textures/gate/emerald_portal_runes_anim.png")));
    float[][] origins = {
      {26, 40},
      {17.213f, 71.113f},
      {-4, 84},
      {-25.213f, 71.113f},
      {-34, 40},
      {-25.213f, 8.887f},
      {-4, -4},
      {17.213f, 8.887f}
    };
    int frame = (int) (time / 4 % 4);
    for (int n = 0; n < 8; n++) {
      float x = origins[n][0] / 16, y = .5f + origins[n][1] / 16;
      float lo = (float) n / 8,
          hi = (float) (n + 1) / 8,
          top = (float) frame / 4,
          bottom = (float) (frame + 1) / 4;
      float[][] quad = {
        {x, y, .015f, lo, bottom},
        {x + .5f, y, .015f, hi, bottom},
        {x + .5f, y + .5f, .015f, hi, top},
        {x, y + .5f, .015f, lo, top}
      };
      for (int side = 0; side < 2; side++)
        for (int i = 0; i < 4; i++) {
          var v = quad[side == 0 ? i : 3 - i];
          consumer
              .vertex(pose.last().pose(), v[0], v[1], v[2])
              .color(255, 255, 255, 255)
              .uv(v[3], v[4])
              .overlayCoords(OverlayTexture.NO_OVERLAY)
              .uv2(LightTexture.FULL_BRIGHT)
              .normal(pose.last().normal(), 0, 0, side == 0 ? 1 : -1)
              .endVertex();
        }
    }
  }

  static void ellipse(
      PoseStack pose,
      MultiBufferSource buffers,
      String file,
      int frame,
      int frames,
      float z,
      float width,
      float height) {
    var consumer =
        buffers.getBuffer(
            RenderType.entityTranslucent(
                new ResourceLocation("dungeongates", "textures/gate/" + file)));
    for (int side = 0; side < 2; side++)
      for (int n = 0; n < 48; n++) {
        double a = n * Math.PI * 2 / 48, b = (n + 1) * Math.PI * 2 / 48;
        float[][] points = {
          {0, .5f + height / 2, z, .5f, ((float) frame + .5f) / frames},
          {
            (float) Math.cos(a) * width / 2,
            .5f + height / 2 + (float) Math.sin(a) * height / 2,
            z,
            .5f + .5f * (float) Math.cos(a),
            (frame + .5f - .5f * (float) Math.sin(a)) / frames
          },
          {
            (float) Math.cos(b) * width / 2,
            .5f + height / 2 + (float) Math.sin(b) * height / 2,
            z,
            .5f + .5f * (float) Math.cos(b),
            (frame + .5f - .5f * (float) Math.sin(b)) / frames
          }
        };
        int[] order = side == 0 ? new int[] {0, 1, 2, 2} : new int[] {0, 2, 1, 1};
        for (int v : order) {
          var p = points[v];
          consumer
              .vertex(pose.last().pose(), p[0], p[1], p[2])
              .color(255, 255, 255, 255)
              .uv(p[3], p[4])
              .overlayCoords(OverlayTexture.NO_OVERLAY)
              .uv2(LightTexture.FULL_BRIGHT)
              .normal(pose.last().normal(), 0, 0, side == 0 ? 1 : -1)
              .endVertex();
        }
      }
  }

  static void plane(
      PoseStack p,
      MultiBufferSource buffers,
      String file,
      int frame,
      int frames,
      float z,
      float width,
      float height) {
    var v =
        buffers.getBuffer(
            RenderType.entityTranslucent(
                new ResourceLocation("dungeongates", "textures/gate/" + file)));
    var matrix = p.last().pose();
    float low = (float) frame / frames, high = (float) (frame + 1) / frames;
    float[][] coords = {
      {-width / 2, .5f, z, 0, high},
      {width / 2, .5f, z, 1, high},
      {width / 2, .5f + height, z, 1, low},
      {-width / 2, .5f + height, z, 0, low}
    };
    for (int side = 0; side < 2; side++)
      for (int i = 0; i < 4; i++) {
        var c = coords[side == 0 ? i : 3 - i];
        v.vertex(matrix, c[0], c[1], c[2])
            .color(255, 255, 255, 255)
            .uv(c[3], c[4])
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(LightTexture.FULL_BRIGHT)
            .normal(p.last().normal(), 0, 0, side == 0 ? 1 : -1)
            .endVertex();
      }
  }
}
