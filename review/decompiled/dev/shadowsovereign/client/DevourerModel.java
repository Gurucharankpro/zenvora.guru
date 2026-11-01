/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Mob
 *  org.joml.Quaternionf
 */
package dev.shadowsovereign.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.shadowsovereign.covenant.CovenantFauna;
import dev.shadowsovereign.moonveil.Nuna;
import dev.shadowsovereign.mythic.ChimeMite;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.SoulDevourer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.joml.Quaternionf;

public final class DevourerModel<T extends Mob>
extends EntityModel<T> {
    public static final ResourceLocation TEXTURE = new ResourceLocation("shadowsovereign", "textures/entity/soul_devourer.png");
    public float previewWidth = 4.0f;
    public float previewHeight = 4.0f;
    private final Bone[] bones;
    private final int[][] children;
    private static final float[] ZERO = new float[]{0.0f, 0.0f, 0.0f};
    private final Map<String, Clip> clips = new HashMap<String, Clip>();
    private final float[][] pose;
    private final Quaternionf rotation = new Quaternionf();

    private static float[] xyz(JsonArray a) {
        return new float[]{a.get(0).getAsFloat(), a.get(1).getAsFloat(), a.get(2).getAsFloat()};
    }

    public DevourerModel() {
        this("soul_devourer");
    }

    public DevourerModel(String id) {
        super(DevourerModel::expansion$renderLayer);
        try (InputStream stream = Minecraft.m_91087_().m_91098_().m_215595_(new ResourceLocation("shadowsovereign", "mythic/" + id + ".json"));
             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);){
            JsonObject json = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            if (json.has("previewBounds")) {
                this.previewWidth = json.getAsJsonArray("previewBounds").get(0).getAsFloat();
                this.previewHeight = json.getAsJsonArray("previewBounds").get(1).getAsFloat();
            }
            float tw = json.get("width").getAsFloat();
            float th = json.get("height").getAsFloat();
            JsonArray ba = json.getAsJsonArray("bones");
            this.bones = new Bone[ba.size()];
            this.children = new int[ba.size()][];
            this.pose = new float[ba.size()][9];
            for (int i = 0; i < this.bones.length; ++i) {
                JsonObject b = ba.get(i).getAsJsonObject();
                ArrayList<Cube> cubes = new ArrayList<Cube>();
                for (JsonElement ce : b.getAsJsonArray("cubes")) {
                    JsonObject c2 = ce.getAsJsonObject();
                    float[] from = DevourerModel.xyz(c2.getAsJsonArray("from"));
                    float[] to = DevourerModel.xyz(c2.getAsJsonArray("to"));
                    float[] origin = DevourerModel.xyz(c2.getAsJsonArray("origin"));
                    float x = from[0];
                    float y = from[1];
                    float z = from[2];
                    float X = to[0];
                    float Y = to[1];
                    float Z = to[2];
                    float[][] vertices = new float[][]{{X, Y, z, x, Y, z, x, y, z, X, y, z}, {x, Y, Z, X, Y, Z, X, y, Z, x, y, Z}, {X, Y, Z, X, Y, z, X, y, z, X, y, Z}, {x, Y, z, x, Y, Z, x, y, Z, x, y, z}, {x, Y, Z, x, Y, z, X, Y, z, X, Y, Z}, {x, y, z, x, y, Z, X, y, Z, X, y, z}};
                    int[][] normals = new int[][]{{0, 0, -1}, {0, 0, 1}, {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}};
                    String[] names = new String[]{"north", "south", "east", "west", "up", "down"};
                    ArrayList<Face> faces = new ArrayList<Face>();
                    for (int f = 0; f < 6; ++f) {
                        JsonObject fa = c2.getAsJsonObject("faces");
                        if (!fa.has(names[f])) continue;
                        JsonArray uv = fa.getAsJsonArray(names[f]);
                        float[] v = vertices[f];
                        for (int j = 0; j < 12; ++j) {
                            v[j] = (v[j] - origin[j % 3]) / 16.0f;
                        }
                        faces.add(new Face(v, new float[]{uv.get(0).getAsFloat() / tw, uv.get(1).getAsFloat() / th, uv.get(2).getAsFloat() / tw, uv.get(3).getAsFloat() / th}, normals[f][0], normals[f][1], normals[f][2]));
                    }
                    cubes.add(new Cube(origin, DevourerModel.xyz(c2.getAsJsonArray("rotation")), (Face[])faces.toArray(Face[]::new)));
                }
                this.bones[i] = new Bone(b.get("name").getAsString(), b.get("parent").getAsInt(), DevourerModel.xyz(b.getAsJsonArray("pivot")), DevourerModel.xyz(b.getAsJsonArray("rotation")), (Cube[])cubes.toArray(Cube[]::new));
            }
            for (int parent = 0; parent < this.bones.length; ++parent) {
                int index = parent;
                this.children[parent] = IntStream.range(0, this.bones.length).filter(c -> this.bones[c].parent == index).toArray();
            }
            for (Map.Entry entry : json.getAsJsonObject("animations").entrySet()) {
                JsonObject a = ((JsonElement)entry.getValue()).getAsJsonObject();
                ArrayList<Track> tracks = new ArrayList<Track>();
                for (JsonElement te : a.getAsJsonArray("tracks")) {
                    JsonObject t = te.getAsJsonObject();
                    JsonArray ka = t.getAsJsonArray("keys");
                    float[][] keys = new float[ka.size()][4];
                    for (int k = 0; k < keys.length; ++k) {
                        for (int n = 0; n < 4; ++n) {
                            keys[k][n] = ka.get(k).getAsJsonArray().get(n).getAsFloat();
                        }
                    }
                    tracks.add(new Track(t.get("bone").getAsInt(), t.get("channel").getAsString(), keys));
                }
                this.clips.put((String)entry.getKey(), new Clip(a.get("length").getAsFloat(), a.get("loop").getAsString().equals("loop"), (Track[])tracks.toArray(Track[]::new)));
            }
        }
        catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Cannot load original Soul Devourer model", e);
        }
    }

    private void apply(String name, float seconds) {
        Clip clip = this.clips.get(name);
        if (clip == null) {
            return;
        }
        float t = clip.loop ? seconds % clip.length : Math.min(seconds, clip.length);
        for (Track track : clip.tracks) {
            float mix;
            float[][] keys = track.keys;
            if (keys.length == 0) continue;
            int lo = 0;
            int hi = keys.length - 1;
            while (lo < hi) {
                int mid = lo + hi + 1 >>> 1;
                if (keys[mid][0] <= t) {
                    lo = mid;
                    continue;
                }
                hi = mid - 1;
            }
            int a = lo;
            int b = Math.min(keys.length - 1, a + 1);
            float f = mix = keys[b][0] == keys[a][0] ? 0.0f : Math.max(0.0f, Math.min(1.0f, (t - keys[a][0]) / (keys[b][0] - keys[a][0])));
            int offset = track.channel.equals("rotation") ? 3 : (track.channel.equals("scale") ? 6 : 0);
            for (int i = 0; i < 3; ++i) {
                float value = keys[a][i + 1] + (keys[b][i + 1] - keys[a][i + 1]) * mix;
                if (offset == 6) {
                    float[] fArray = this.pose[track.bone];
                    int n = offset + i;
                    fArray[n] = fArray[n] * value;
                    continue;
                }
                float[] fArray = this.pose[track.bone];
                int n = offset + i;
                fArray[n] = fArray[n] + value;
            }
        }
    }

    public void setupAnim(T entity, float limb, float amount, float age, float yaw, float pitch) {
        for (float[] p : this.pose) {
            Arrays.fill(p, 0.0f);
            p[8] = 1.0f;
            p[7] = 1.0f;
            p[6] = 1.0f;
        }
        float seconds = age / 20.0f;
        if (entity instanceof SoulDevourer) {
            String clip;
            SoulDevourer mob = (SoulDevourer)((Object)entity);
            String string = mob.m_21224_() ? "death_collapse" : (mob.action() > 0 ? SoulDevourer.CLIPS[mob.action()] : (amount > 0.02f ? (amount > 0.5f ? "run" : "walk_heavy") : (clip = "idle_breathing")));
            float elapsed = mob.m_21224_() ? (float)mob.f_20919_ / 20.0f : (mob.action() > 0 ? mob.actionAge(age - (float)mob.f_19797_) : seconds);
            this.apply(clip, elapsed);
            if (!clip.equals("death_collapse")) {
                this.apply("idle_cage_energy", seconds);
                this.apply("idle_tail", seconds);
            }
        } else if (entity instanceof MythicBeast) {
            String clip;
            MythicBeast mob = (MythicBeast)((Object)entity);
            String string = mob.m_21224_() ? mob.deathClip() : (mob.action() > 0 ? mob.clip() : (mob.profile().flies() && !mob.m_20096_() ? mob.motion(amount > 0.5f) : (mob.m_20160_() && !mob.m_20096_() ? "fly_slow" : (amount > 0.02f ? mob.motion(amount > 0.5f) : (clip = mob.idleClip())))));
            float elapsed = mob.m_21224_() ? (float)mob.f_20919_ / 20.0f : (mob.action() > 0 ? mob.actionAge(age - (float)mob.f_19797_) : seconds);
            this.apply(clip, elapsed);
            if (!mob.m_21224_() && mob.action() == 0) {
                this.apply("idle_tail_sway", seconds);
                if (mob.kind() == 2) {
                    this.apply("idle_petals", seconds);
                }
                if (mob.kind() == 3) {
                    this.apply("breathing", seconds);
                }
            }
        } else if (entity instanceof Nuna) {
            Nuna mob = (Nuna)((Object)entity);
            this.apply(mob.clip(amount), mob.m_21224_() ? (float)mob.f_20919_ / 20.0f : mob.clipAge(age - (float)mob.f_19797_));
        } else if (entity instanceof ChimeMite) {
            ChimeMite mob = (ChimeMite)((Object)entity);
            this.apply(mob.m_21224_() ? "death" : (mob.attackTicks() > 0 ? "attack" : (amount > 0.02f ? "walk" : "idle")), mob.m_21224_() ? (float)mob.f_20919_ / 20.0f : (mob.attackTicks() > 0 ? (float)(14 - mob.attackTicks()) / 20.0f : seconds));
        } else if (entity instanceof CovenantFauna) {
            CovenantFauna mob = (CovenantFauna)((Object)entity);
            this.apply(mob.m_21224_() ? "death" : (mob.kind() == 1 ? "fly" : (amount > 0.02f ? "walk" : "idle")), seconds);
        }
        for (int i = 0; i < this.bones.length; ++i) {
            if (!this.bones[i].name.equals("head")) continue;
            float[] fArray = this.pose[i];
            fArray[3] = fArray[3] + Math.max(-12.0f, Math.min(12.0f, pitch));
            float[] fArray2 = this.pose[i];
            fArray2[4] = fArray2[4] + Math.max(-18.0f, Math.min(18.0f, yaw));
        }
    }

    public void setupAnim(SoulDevourer mob, float limb, float amount, float age, float yaw, float pitch) {
        this.setupAnim((T)((Object)mob), limb, amount, age, yaw, pitch);
    }

    public void m_7695_(PoseStack stack, VertexConsumer out, int light, int overlay, float red, float green, float blue, float alpha) {
        stack.m_85836_();
        stack.m_85837_(0.0, 1.501, 0.0);
        stack.m_85841_(-1.0f, -1.0f, 1.0f);
        for (int i = 0; i < this.bones.length; ++i) {
            if (this.bones[i].parent >= 0) continue;
            this.renderBone(i, stack, out, light, overlay, red, green, blue, alpha);
        }
        stack.m_85849_();
    }

    private void rotate(PoseStack p, float x, float y, float z) {
        p.m_252781_(this.rotation.rotationZYX(z * (float)Math.PI / 180.0f, y * (float)Math.PI / 180.0f, x * (float)Math.PI / 180.0f));
    }

    private void renderBone(int index, PoseStack stack, VertexConsumer out, int light, int overlay, float r, float g, float b, float a) {
        Bone bone = this.bones[index];
        float[] parent = bone.parent < 0 ? ZERO : this.bones[bone.parent].pivot;
        float[] anim = this.pose[index];
        stack.m_85836_();
        stack.m_252880_((bone.pivot[0] - parent[0] + anim[0]) / 16.0f, (bone.pivot[1] - parent[1] + anim[1]) / 16.0f, (bone.pivot[2] - parent[2] + anim[2]) / 16.0f);
        this.rotate(stack, bone.rotation[0] + anim[3], bone.rotation[1] + anim[4], bone.rotation[2] + anim[5]);
        stack.m_85841_(anim[6], anim[7], anim[8]);
        int glow = bone.name.contains("energy") || bone.name.contains("eye") || bone.name.contains("crystal") ? 0xF000F0 : light;
        for (Cube cube : bone.cubes) {
            stack.m_85836_();
            stack.m_252880_((cube.origin[0] - bone.pivot[0]) / 16.0f, (cube.origin[1] - bone.pivot[1]) / 16.0f, (cube.origin[2] - bone.pivot[2]) / 16.0f);
            this.rotate(stack, cube.rotation[0], cube.rotation[1], cube.rotation[2]);
            PoseStack.Pose entry = stack.m_85850_();
            for (Face f : cube.faces) {
                for (int k = 0; k < 4; ++k) {
                    float u = k == 0 || k == 3 ? f.uv[0] : f.uv[2];
                    float v = k < 2 ? f.uv[1] : f.uv[3];
                    out.m_252986_(entry.m_252922_(), f.vertices[k * 3], f.vertices[k * 3 + 1], f.vertices[k * 3 + 2]).m_85950_(r, g, b, a).m_7421_(u, v).m_86008_(overlay).m_85969_(glow).m_252939_(entry.m_252943_(), (float)f.nx, (float)f.ny, (float)f.nz).m_5752_();
                }
            }
            stack.m_85849_();
        }
        for (int i : this.children[index]) {
            this.renderBone(i, stack, out, glow, overlay, r, g, b, a);
        }
        stack.m_85849_();
    }

    private static RenderType expansion$renderLayer(ResourceLocation resourceLocation) {
        if (resourceLocation.toString().contains("glass_ronin")) {
            return RenderType.m_110473_((ResourceLocation)resourceLocation);
        }
        return RenderType.m_110458_((ResourceLocation)resourceLocation);
    }

    private record Bone(String name, int parent, float[] pivot, float[] rotation, Cube[] cubes) {
    }

    private record Face(float[] vertices, float[] uv, int nx, int ny, int nz) {
    }

    private record Cube(float[] origin, float[] rotation, Face[] faces) {
    }

    private record Track(int bone, String channel, float[][] keys) {
    }

    private record Clip(float length, boolean loop, Track[] tracks) {
    }
}

