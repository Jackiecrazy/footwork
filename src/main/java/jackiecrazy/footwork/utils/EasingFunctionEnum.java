package jackiecrazy.footwork.utils;

public enum EasingFunctionEnum implements EasingFunction {


    LINEAR(x -> x),

    IN_SINE(x -> 1 - Math.cos((x * Math.PI) / 2)),

    OUT_SINE(x -> Math.sin((x * Math.PI) / 2)),

    IN_OUT_SINE(x -> -(Math.cos(Math.PI * x) - 1) / 2),

    IN_CUBIC(x -> x * x * x),

    OUT_CUBIC(x -> 1 - Math.pow(1 - x, 3)),

    IN_OUT_CUBIC(x -> x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2);

    private final EasingFunction delegate;

    EasingFunctionEnum(EasingFunction delegate) {
        this.delegate = delegate;
    }

    @Override
    public double ease(double partialDuration) {
        return delegate.ease(partialDuration);
    }

    // Optional: nice toString / name for debugging
    @Override
    public String toString() {
        return name().toLowerCase().replace('_', '-');
    }
}
