import java.util.Objects;

public class QuantityMeasurementApp {

    // =========================
    // LENGTH UNIT (UC8)
    // =========================
    enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(1.0 / 30.48);

        private final double factor;

        LengthUnit(double factor) {
            this.factor = factor;
        }

        public double convertToBaseUnit(double value) {
            return value * factor;
        }

        public double convertFromBaseUnit(double baseValue) {
            return baseValue / factor;
        }
    }

    // =========================
    // WEIGHT UNIT (UC9)
    // =========================
    enum WeightUnit {
        KILOGRAM(1.0),
        GRAM(0.001),
        POUND(0.453592);

        private final double factor;

        WeightUnit(double factor) {
            this.factor = factor;
        }

        public double convertToBaseUnit(double value) {
            return value * factor;
        }

        public double convertFromBaseUnit(double baseValue) {
            return baseValue / factor;
        }
    }

    // =========================
    // QUANTITY LENGTH
    // =========================
    static final class QuantityLength {
        private final double value;
        private final LengthUnit unit;
        private static final double EPSILON = 1e-9;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null || !Double.isFinite(value))
                throw new IllegalArgumentException();
            this.value = value;
            this.unit = unit;
        }

        public QuantityLength convertTo(LengthUnit target) {
            double base = unit.convertToBaseUnit(value);
            return new QuantityLength(target.convertFromBaseUnit(base), target);
        }

        public QuantityLength add(QuantityLength other) {
            return add(this, other, this.unit);
        }

        public static QuantityLength add(QuantityLength q1, QuantityLength q2, LengthUnit target) {
            double sum = q1.unit.convertToBaseUnit(q1.value)
                    + q2.unit.convertToBaseUnit(q2.value);
            return new QuantityLength(target.convertFromBaseUnit(sum), target);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof QuantityLength)) return false;
            QuantityLength q = (QuantityLength) o;
            double a = unit.convertToBaseUnit(value);
            double b = q.unit.convertToBaseUnit(q.value);
            return Math.abs(a - b) < EPSILON;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, unit);
        }

        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }
    }

    // =========================
    // QUANTITY WEIGHT (UC9)
    // =========================
    static final class QuantityWeight {
        private final double value;
        private final WeightUnit unit;
        private static final double EPSILON = 1e-6;

        public QuantityWeight(double value, WeightUnit unit) {
            if (unit == null || !Double.isFinite(value))
                throw new IllegalArgumentException();
            this.value = value;
            this.unit = unit;
        }

        // Convert
        public QuantityWeight convertTo(WeightUnit target) {
            double base = unit.convertToBaseUnit(value);
            return new QuantityWeight(target.convertFromBaseUnit(base), target);
        }

        // Add (default)
        public QuantityWeight add(QuantityWeight other) {
            return add(this, other, this.unit);
        }

        // Add (explicit target)
        public static QuantityWeight add(QuantityWeight q1, QuantityWeight q2, WeightUnit target) {
            double sum = q1.unit.convertToBaseUnit(q1.value)
                    + q2.unit.convertToBaseUnit(q2.value);
            return new QuantityWeight(target.convertFromBaseUnit(sum), target);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof QuantityWeight)) return false;
            QuantityWeight q = (QuantityWeight) o;

            double a = unit.convertToBaseUnit(value);
            double b = q.unit.convertToBaseUnit(q.value);

            return Math.abs(a - b) < EPSILON;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, unit);
        }

        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }
    }

    // =========================
    // MAIN METHOD (DEMO)
    // =========================
    public static void main(String[] args) {

        System.out.println("---- LENGTH TESTS ----");

        System.out.println(new QuantityLength(1, LengthUnit.FEET)
                .convertTo(LengthUnit.INCHES));

        System.out.println(QuantityLength.add(
                new QuantityLength(1, LengthUnit.FEET),
                new QuantityLength(12, LengthUnit.INCHES),
                LengthUnit.YARDS));

        System.out.println(new QuantityLength(36, LengthUnit.INCHES)
                .equals(new QuantityLength(1, LengthUnit.YARDS)));

        System.out.println("\n---- WEIGHT TESTS ----");

        // Equality
        System.out.println(new QuantityWeight(1, WeightUnit.KILOGRAM)
                .equals(new QuantityWeight(1000, WeightUnit.GRAM)));

        System.out.println(new QuantityWeight(1, WeightUnit.KILOGRAM)
                .equals(new QuantityWeight(2.20462, WeightUnit.POUND)));

        // Conversion
        System.out.println(new QuantityWeight(1, WeightUnit.KILOGRAM)
                .convertTo(WeightUnit.GRAM));

        System.out.println(new QuantityWeight(2, WeightUnit.POUND)
                .convertTo(WeightUnit.KILOGRAM));

        // Addition
        System.out.println(new QuantityWeight(1, WeightUnit.KILOGRAM)
                .add(new QuantityWeight(1000, WeightUnit.GRAM)));

        System.out.println(QuantityWeight.add(
                new QuantityWeight(1, WeightUnit.KILOGRAM),
                new QuantityWeight(1000, WeightUnit.GRAM),
                WeightUnit.GRAM));

        System.out.println(QuantityWeight.add(
                new QuantityWeight(2, WeightUnit.KILOGRAM),
                new QuantityWeight(4, WeightUnit.POUND),
                WeightUnit.KILOGRAM));

        // Category safety check
        System.out.println("\n---- CATEGORY SAFETY ----");
        System.out.println(new QuantityWeight(1, WeightUnit.KILOGRAM)
                .equals(new QuantityLength(1, LengthUnit.FEET))); // false
    }
}