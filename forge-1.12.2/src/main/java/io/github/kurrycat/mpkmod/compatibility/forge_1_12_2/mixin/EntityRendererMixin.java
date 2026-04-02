package io.github.kurrycat.mpkmod.compatibility.forge_1_12_2.mixin;

import io.github.kurrycat.mpkmod.compatibility.forge_1_12_2.CameraSmoothing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Shadow
    @Final
    private Minecraft mc;

    @Unique
    private float mpkmod$origYaw;
    @Unique
    private float mpkmod$origPitch;
    @Unique
    private float mpkmod$origPrevYaw;
    @Unique
    private float mpkmod$origPrevPitch;
    @Unique
    private boolean mpkmod$applied;

    @Inject(method = "orientCamera", at = @At("HEAD"))
    private void mpkmod$applySmooth(float partialTicks, CallbackInfo ci) {
        Entity view = mc.getRenderViewEntity();
        if (view == null) {
            mpkmod$applied = false;
            return;
        }
        mpkmod$origYaw = view.rotationYaw;
        mpkmod$origPitch = view.rotationPitch;
        mpkmod$origPrevYaw = view.prevRotationYaw;
        mpkmod$origPrevPitch = view.prevRotationPitch;
        mpkmod$applied = CameraSmoothing.apply(view, partialTicks);
    }

    @Inject(method = "orientCamera", at = @At("RETURN"))
    private void mpkmod$restoreSmooth(float partialTicks, CallbackInfo ci) {
        if (!mpkmod$applied) {
            return;
        }
        Entity view = mc.getRenderViewEntity();
        if (view == null) {
            return;
        }
        view.rotationYaw = mpkmod$origYaw;
        view.rotationPitch = mpkmod$origPitch;
        view.prevRotationYaw = mpkmod$origPrevYaw;
        view.prevRotationPitch = mpkmod$origPrevPitch;
    }
}
