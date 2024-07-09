package com.rebane2001.cr3stal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Quaternionf;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class RubicsCubeRenderer {

    private static final int ANIMATION_LENGTH = 400;
    private static final float CUBELET_SCALE = 0.4f;

    // Currently rotating side
    private int rotatingSide = 0;
    // front - 0
    // back - 1
    // top - 2
    // bottom - 3
    // left - 4
    // right - 5

    private long lastTime = 0;

    public void render(ModelPart core, PoseStack poseStack, VertexConsumer vertices, int light, int overlay) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - ANIMATION_LENGTH > lastTime) {
            // rotate sides and corners
            int[] currentSide = Util.cubeSides[rotatingSide];
            Quaternionf[] cubletsTemp = {
                    Util.cubeletStatus[currentSide[0]],
                    Util.cubeletStatus[currentSide[1]],
                    Util.cubeletStatus[currentSide[2]],
                    Util.cubeletStatus[currentSide[3]],
                    Util.cubeletStatus[currentSide[4]],
                    Util.cubeletStatus[currentSide[5]],
                    Util.cubeletStatus[currentSide[6]],
                    Util.cubeletStatus[currentSide[7]],
                    Util.cubeletStatus[currentSide[8]]
            };

            // rotation direction
            Util.cubeletStatus[currentSide[0]] = cubletsTemp[6];
            Util.cubeletStatus[currentSide[1]] = cubletsTemp[3];
            Util.cubeletStatus[currentSide[2]] = cubletsTemp[0];
            Util.cubeletStatus[currentSide[3]] = cubletsTemp[7];
            Util.cubeletStatus[currentSide[4]] = cubletsTemp[4];
            Util.cubeletStatus[currentSide[5]] = cubletsTemp[1];
            Util.cubeletStatus[currentSide[6]] = cubletsTemp[8];
            Util.cubeletStatus[currentSide[7]] = cubletsTemp[5];
            Util.cubeletStatus[currentSide[8]] = cubletsTemp[2];

            int[] trans = Util.cubeSideTransforms[rotatingSide];
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        if (x != 0 || y != 0 || z != 0)
                            applyCubeletRotation(x, y, z, trans[0], trans[1], trans[2]);
                    }
                }
            }
            rotatingSide = ThreadLocalRandom.current().nextInt(0, 5 + 1);
            lastTime = currentTime;
        }

        poseStack.scale(CUBELET_SCALE, CUBELET_SCALE, CUBELET_SCALE);

        // Draw non-rotating cubes
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    if (x != 0 || y != 0 || z != 0)
                        drawCubeletStatic(core, poseStack, vertices, light, overlay, x, y, z);
                }
            }
        }

        // Draw rotating cubes
        int[] trans = Util.cubeSideTransforms[rotatingSide];
        poseStack.pushPose();
        poseStack.translate(trans[0] * CUBELET_SCALE, trans[1] * CUBELET_SCALE, trans[2] * CUBELET_SCALE);
        //GlStateManager.rotate((currentTime - lastTime) * 90 / ANIMATION_LENGTH, trans[0], trans[1], trans[2]);
        float RotationAngle = (float) Math.toRadians(Util.easeInOutCubic(((float) (currentTime - lastTime)) / ANIMATION_LENGTH) * 90);
        float xx = (float) (trans[0] * Math.sin(RotationAngle / 2));
        float yy = (float) (trans[1] * Math.sin(RotationAngle / 2));
        float zz = (float) (trans[2] * Math.sin(RotationAngle / 2));
        float ww = (float) Math.cos(RotationAngle / 2);
        Quaternionf qq = new Quaternionf(xx, yy, zz, ww);
        poseStack.mulPose(qq);
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                for (int z = -1; z < 2; z++) {
                    if (x != 0 || y != 0 || z != 0)
                        drawCubeletRotating(core, poseStack, vertices, light, overlay, x, y, z);
                }
            }
        }
        poseStack.popPose();
    }

    private void drawCubeletStatic(ModelPart core, PoseStack poseStack, VertexConsumer vertices, int light, int overlay, int x, int y, int z) {
        int cubletId = Util.cubletLookup[x + 1][y + 1][z + 1];

        if (Arrays.stream(Util.cubeSides[rotatingSide]).anyMatch(r -> r == cubletId))
            return;

        drawCubelet(core, poseStack, vertices, light, overlay, x, y, z, cubletId);
    }

    private void drawCubeletRotating(ModelPart core, PoseStack poseStack, VertexConsumer vertices, int light, int overlay, int x, int y, int z) {
        int cubletId = Util.cubletLookup[x + 1][y + 1][z + 1];

        if (Arrays.stream(Util.cubeSides[rotatingSide]).noneMatch(r -> r == cubletId))
            return;

        int[] trans = Util.cubeSideTransforms[rotatingSide];
        drawCubelet(core, poseStack, vertices, light, overlay, x - trans[0], y - trans[1], z - trans[2], cubletId);
    }

    private void applyCubeletRotation(int x, int y, int z, int rX, int rY, int rZ) {
        int cubletId = Util.cubletLookup[x + 1][y + 1][z + 1];

        if (Arrays.stream(Util.cubeSides[rotatingSide]).noneMatch(r -> r == cubletId))
            return;

        float RotationAngle = (float) Math.toRadians(90);
        float xx = (float) (rX * Math.sin(RotationAngle / 2));
        float yy = (float) (rY * Math.sin(RotationAngle / 2));
        float zz = (float) (rZ * Math.sin(RotationAngle / 2));
        float ww = (float) Math.cos(RotationAngle / 2);
        Quaternionf tempQuat = new Quaternionf(xx, yy, zz, ww);
        tempQuat.mul(Util.cubeletStatus[cubletId]);
        Util.cubeletStatus[cubletId] = tempQuat;
    }

    private void drawCubelet(ModelPart core, PoseStack poseStack, VertexConsumer vertices, int light, int overlay, int x, int y, int z, int cubletId) {
        poseStack.pushPose();
        poseStack.translate(x * CUBELET_SCALE, y * CUBELET_SCALE, z * CUBELET_SCALE);
        poseStack.pushPose();
        poseStack.mulPose(Util.cubeletStatus[cubletId]);
        poseStack.scale(0.8f, 0.8f, 0.8f);

        core.render(poseStack, vertices, light, overlay);

        poseStack.popPose();
        poseStack.popPose();
    }
}
