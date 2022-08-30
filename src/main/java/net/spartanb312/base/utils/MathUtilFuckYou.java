package net.spartanb312.base.utils;

import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.core.setting.settings.IntSetting;
import org.lwjgl.util.vector.Vector2f;
//its named like this bc if its named 'mathutil' it keeps breaking its imports idk i cant fix it
public class MathUtilFuckYou {
    public static float clamp(float val, final float min, final float max) {
        if (val <= min) {
            val = min;
        }
        if (val >= max) {
            val = max;
        }
        return val;
    }


    public static long clamp(long val, final long min, final long max) {
        if (val <= min) {
            val = min;
        }
        if (val >= max) {
            val = max;
        }
        return val;
    }

    public static double getDistance(Vec3d start, Vec3d end) {
        double x = end.x - start.x;
        double y = end.y - start.y;
        double z = end.z - start.z;
        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public static double[] rotationAroundAxis3d(double x, double y, double z, double theta, String axis) {
        //xyz is offset from where the axis is

        if (theta == 0.0)
            return new double[] {x, y, z};

        switch (axis) {
            case "x": {
                return new double[] {x, y * Math.cos(theta) - z * Math.sin(theta), y * Math.sin(theta) + z * Math.cos(theta)};
            }

            case "y": {
                return new double[] {x * Math.cos(theta) + z * Math.sin(theta), y, z * Math.cos(theta) - x * Math.sin(theta)};
            }

            case "z": {
                return new double[] {x * Math.cos(theta) - y * Math.sin(theta), x * Math.sin(theta) + y * Math.cos(theta), z};
            }
        }
        return new double[] {0, 0, 0};
    }

    public static double[] cartesianToPolar2d(double x, double y) {
        return new double[] {Math.sqrt((x * x) + (y * y)), Math.atan(y / x)};
    }

    public static double[] polarToCartesian2d(double magnitude, double theta) {
        return new double[] {magnitude * Math.cos(theta), magnitude * Math.sin(theta)};
    }

    public static double[] cartesianToPolar3d(double x, double y, double z) {
        double magnitude = Math.sqrt((x * x) + (y * y) + (z * z));
        return new double[] {magnitude, Math.acos(x / (Math.sqrt((x * x) + (y * y)))) * ((y < 0) ? -1 : 1), Math.acos(z / magnitude)};
    }

    public static double[] polarToCartesian3d(double magnitude, double theta, double phi) {
        return new double[] {magnitude * Math.sin(phi) * Math.cos(theta), magnitude * Math.sin(phi) * Math.sin(theta), magnitude * Math.cos(phi)};
    }

    public static float dotProduct(Vector2f in, Vector2f normal) {
        float normalVecLength = (float) Math.sqrt((normal.x * normal.x) + (normal.y * normal.y));
        Vector2f normalizedVec = new Vector2f(normal.x / normalVecLength, normal.y / normalVecLength);
        return (in.x * normalizedVec.x) + (in.y * normalizedVec.y);
    }

    public static float dotProductNormalized(Vector2f in, Vector2f normalizedVec) {
        return (in.x * normalizedVec.x) + (in.y * normalizedVec.y);
    }

    public static Vector2f reflectVector2f(Vector2f in, Vector2f normal) {
        float normalVecLength = (float) Math.sqrt((normal.x * normal.x) + (normal.y * normal.y));
        Vector2f normalizedVec = new Vector2f(normal.x / normalVecLength, normal.y / normalVecLength);
        float dotProduct = 2.0f * dotProductNormalized(in, normalizedVec);
        return new Vector2f(in.x - (normalizedVec.x * (dotProduct)), in.y - (normalizedVec.y * (dotProduct)));
    }

    /*
     * progress has to be from 0.0f to 1.0f
     */
    public static float interpNonLinear(float start, float end, float progress, float factor) {
        progress = clamp(progress, 0.0f, 1.0f);
        float remaining = end - (start + (progress * (end - start)));
        return end - (remaining / ((factor * progress * 300.0f) + 1.0f));
    }

    /*
     * progress has to be from 0.0f to 300.0f
     */
    public static float linearInterp(float start, float end, float progressCounter) {
        return start + (progressCounter * ((end - start) / 300.0f));
    }

    public static float rolledLinearInterp(int component1, int component2, int offset, float speed, float size) {
        double componentState = Math.ceil(((System.currentTimeMillis() * (double)speed) + offset) / 20.0) / (double)size;
        componentState %= 300;
        componentState = (float)((150.0f * Math.sin(((componentState - 75.0f) * Math.PI) / 150.0f)) + 150.0f);
        return MathUtilFuckYou.linearInterp(component1, component2, (float)componentState);
    }
}
