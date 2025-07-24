package jackiecrazy.footwork.utils;

public interface EasingFunction {
    EasingFunction LINEAR = x -> x;
    EasingFunction IN_SINE = x -> 1 - Math.cos((x * Math.PI) / 2);
    EasingFunction OUT_SINE = x -> Math.sin((x * Math.PI) / 2);
    EasingFunction IN_OUT_SINE = x -> -(Math.cos(Math.PI * x) - 1) / 2;
    EasingFunction IN_CUBIC = x -> x * x * x;
    EasingFunction OUT_CUBIC = x -> 1 - Math.pow(1 - x, 3);
    EasingFunction IN_OUT_CUBIC = x -> x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;

    public double ease(double partialDuration);
}
