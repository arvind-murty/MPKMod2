package io.github.kurrycat.mpkmod.compatibility.forge_1_8;

import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;

public final class CameraSmoothing {
    private static boolean active = false;
    private static float prevYaw;
    private static float prevPitch;
    private static float targetYaw;
    private static float targetPitch;

    private CameraSmoothing() {
    }

    public static void updateTarget(float yaw, float pitch) {
        if (active) {
            prevYaw = targetYaw;
            prevPitch = targetPitch;
        } else {
            prevYaw = yaw;
            prevPitch = pitch;
        }
        targetYaw = yaw;
        targetPitch = pitch;
        active = true;
    }

    public static void disable() {
        active = false;
    }

    public static boolean apply(net.minecraft.entity.Entity entity, float partialTicks) {
        if (!active || entity == null) {
            return false;
        }
        float lerpedYaw = lerpAngle(prevYaw, targetYaw, partialTicks);
        float lerpedPitch = lerpPitch(prevPitch, targetPitch, partialTicks);

        entity.rotationYaw = lerpedYaw;
        entity.rotationPitch = lerpedPitch;
        entity.prevRotationYaw = lerpedYaw;
        entity.prevRotationPitch = lerpedPitch;
        return true;
    }

    public static boolean apply(EntityViewRenderEvent.CameraSetup event) {
        if (!active || event == null) {
            return false;
        }
        float partialTicks = (float) event.renderPartialTicks;
        event.yaw = lerpAngle(prevYaw, targetYaw, partialTicks) + 180.0F;
        event.pitch = lerpPitch(prevPitch, targetPitch, partialTicks);
        return true;
    }

    private static float lerpAngle(float from, float to, float amount) {
        float delta = MathHelper.wrapAngleTo180_float(to - from);
        return from + delta * amount;
    }

    private static float lerpPitch(float from, float to, float amount) {
        float lerpedPitch = from + (to - from) * amount;
        return MathHelper.clamp_float(lerpedPitch, -90.0F, 90.0F);
    }
}
