package com.rebane2001.cr3stal.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rebane2001.cr3stal.RubicsCubeRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystalRenderer.class)
public class EndCrystalEntityRendererMixin {
    @Unique
    private RubicsCubeRenderer cr3stal_Neoforge$rubicsCubeRenderer;
    @Unique
    private EndCrystal cr3stal_Neoforge$endCrystalEntity;

    @Inject(method = "render(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",at = @At("HEAD"))
    public void render(EndCrystal pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        this.cr3stal_Neoforge$endCrystalEntity = pEntity;
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V",ordinal = 1)
    )
    public void translate(PoseStack poseStack, float x, float y, float z) {
        poseStack.translate(x, this.cr3stal_Neoforge$endCrystalEntity != null && this.cr3stal_Neoforge$endCrystalEntity.showsBottom() ? 1.2f : 1f, z);
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V",ordinal = 3)
    )
    public void renderCore(ModelPart core, PoseStack poseStack, VertexConsumer vertices, int light, int overlay) {
        if (cr3stal_Neoforge$rubicsCubeRenderer == null) {
            cr3stal_Neoforge$rubicsCubeRenderer = new RubicsCubeRenderer();
        }

        cr3stal_Neoforge$rubicsCubeRenderer.render(core, poseStack, vertices, light, overlay);
    }
}
