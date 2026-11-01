package dev.dungeongates.client;

import com.google.gson.*;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.geometry.*;
import org.joml.*;

/**
 * Preserves supplied vertices/UVs and arbitrary authored angles, without illegal vanilla JSON
 * rotations.
 */
public final class AuthoredMesh implements IUnbakedGeometry<AuthoredMesh> {
  final JsonObject json;

  AuthoredMesh(JsonObject j) {
    json = j;
  }

  public static final class Loader implements IGeometryLoader<AuthoredMesh> {
    public AuthoredMesh read(JsonObject j, JsonDeserializationContext c) {
      return new AuthoredMesh(j.deepCopy());
    }
  }

  public BakedModel bake(
      IGeometryBakingContext context,
      ModelBaker baker,
      Function<Material, TextureAtlasSprite> sprites,
      ModelState state,
      ItemOverrides overrides,
      ResourceLocation location) {
    var quads = new ArrayList<BakedQuad>();
    for (var v : json.getAsJsonArray("authored_elements")) {
      var e = v.getAsJsonObject();
      float[] from = vec(e.getAsJsonArray("from")), to = vec(e.getAsJsonArray("to"));
      for (var f : e.getAsJsonObject("faces").entrySet()) {
        var direction = Direction.byName(f.getKey());
        var face = f.getValue().getAsJsonObject();
        String texture = face.get("texture").getAsString();
        var sprite =
            sprites.apply(
                context.getMaterial(texture.startsWith("#") ? texture.substring(1) : texture));
        var uv =
            face.has("uv")
                ? face.getAsJsonArray("uv")
                : JsonParser.parseString("[0,0,16,16]").getAsJsonArray();
        float u0 = uv.get(0).getAsFloat(),
            v0 = uv.get(1).getAsFloat(),
            u1 = uv.get(2).getAsFloat(),
            v1 = uv.get(3).getAsFloat();
        float[][] corners = corners(direction, from, to);
        int[] data = new int[32];
        Vector3f normal =
            new Vector3f(direction.getStepX(), direction.getStepY(), direction.getStepZ());
        if (e.has("rotation")) rotate(normal, e.getAsJsonObject("rotation"), false);
        normal.mul(new Matrix3f(state.getRotation().getMatrix())).normalize();
        for (int i = 0; i < 4; i++) {
          var p = new Vector3f(corners[i]);
          if (e.has("rotation")) rotate(p, e.getAsJsonObject("rotation"), true);
          p.div(16).sub(.5f, .5f, .5f);
          p.mulPosition(state.getRotation().getMatrix()).add(.5f, .5f, .5f);
          int rotation = face.has("rotation") ? face.get("rotation").getAsInt() / 90 : 0;
          int n = (i + rotation) % 4;
          float u = n == 0 || n == 3 ? u0 : u1;
          float w = n < 2 ? v1 : v0;
          int o = i * 8;
          data[o] = Float.floatToRawIntBits(p.x);
          data[o + 1] = Float.floatToRawIntBits(p.y);
          data[o + 2] = Float.floatToRawIntBits(p.z);
          data[o + 3] = -1;
          data[o + 4] = Float.floatToRawIntBits(sprite.getU(u));
          data[o + 5] = Float.floatToRawIntBits(sprite.getV(w));
          data[o + 6] = 0;
          data[o + 7] =
              ((int) (normal.x * 127) & 255)
                  | (((int) (normal.y * 127) & 255) << 8)
                  | (((int) (normal.z * 127) & 255) << 16);
        }
        quads.add(
            new BakedQuad(
                data, -1, Direction.getNearest(normal.x, normal.y, normal.z), sprite, true));
      }
    }
    var particle = sprites.apply(context.getMaterial("particle"));
    var transforms = context.getTransforms();
    return new BakedModel() {
      public List<BakedQuad> getQuads(BlockState s, Direction side, RandomSource random) {
        return side == null ? quads : List.of();
      }

      public boolean useAmbientOcclusion() {
        return true;
      }

      public boolean isGui3d() {
        return true;
      }

      public boolean usesBlockLight() {
        return true;
      }

      public boolean isCustomRenderer() {
        return false;
      }

      public TextureAtlasSprite getParticleIcon() {
        return particle;
      }

      public ItemTransforms getTransforms() {
        return transforms;
      }

      public ItemOverrides getOverrides() {
        return overrides;
      }
    };
  }

  static float[] vec(JsonArray a) {
    return new float[] {a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat()};
  }

  static void rotate(Vector3f v, JsonObject r, boolean point) {
    var o = vec(r.getAsJsonArray("origin"));
    if (point) v.sub(o[0], o[1], o[2]);
    float a = (float) java.lang.Math.toRadians(r.get("angle").getAsDouble());
    switch (r.get("axis").getAsString()) {
      case "x" -> v.rotateX(a);
      case "y" -> v.rotateY(a);
      case "z" -> v.rotateZ(a);
    }
    if (point) v.add(o[0], o[1], o[2]);
  }

  static float[][] corners(Direction d, float[] a, float[] b) {
    float x = a[0], y = a[1], z = a[2], X = b[0], Y = b[1], Z = b[2];
    return switch (d) {
      case NORTH -> new float[][] {{X, y, z}, {x, y, z}, {x, Y, z}, {X, Y, z}};
      case SOUTH -> new float[][] {{x, y, Z}, {X, y, Z}, {X, Y, Z}, {x, Y, Z}};
      case WEST -> new float[][] {{x, y, z}, {x, y, Z}, {x, Y, Z}, {x, Y, z}};
      case EAST -> new float[][] {{X, y, Z}, {X, y, z}, {X, Y, z}, {X, Y, Z}};
      case UP -> new float[][] {{x, Y, Z}, {X, Y, Z}, {X, Y, z}, {x, Y, z}};
      case DOWN -> new float[][] {{x, y, z}, {X, y, z}, {X, y, Z}, {x, y, Z}};
    };
  }
}
