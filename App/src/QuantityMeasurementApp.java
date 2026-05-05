import java.util.Objects;

public class QuantityMeasurementApp {

    // =========================
    // ENUM: LengthUnit
    // =========================
    enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(0.0328084); // 1 cm = 0.0328084 feet

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value) {
            return value * toFeetFactor;
        }

        public double fromFeet(double feetValue) {
            return feetValue / toFeetFactor;
        }
    }

    // =========================
    // CLASS: QuantityLength
    // =========================
    static final class QuantityLength {

        private final double value;
        private final LengthUnit unit;
        private static final double EPSILON = 1e-9;

        public QuantityLength(double value, LengthUnit unit) {
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Value must be finite");
            }
            this.value = value;
            this.unit = unit;
        }

        public double getValue() {
            return value;
        }

        public LengthUnit getUnit() {
            return unit;
        }

        private double toFeet() {
            return unit.toFeet(value);
        }

        private static double fromFeet(double feetValue, LengthUnit targetUnit) {
            return targetUnit.fromFeet(feetValue);
        }

        // =========================
        // UC6: Default (first operand unit)
        // =========================
        public QuantityLength add(QuantityLength other) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }
            return add(this, other, this.unit);
        }

        // =========================
        // UC7: Explicit Target Unit
        // =========================
        public static QuantityLength add(QuantityLength q1,
                                         QuantityLength q2,
                                         LengthUnit targetUnit) {

            if (q1 == null || q2 == null) {
                throw new IllegalArgumentException("Operands cannot be null");
            }
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            // Convert to base (feet)
            double sumFeet = q1.toFeet() + q2.toFeet();

            // Convert to target unit
            double result = fromFeet(sumFeet, targetUnit);

            return new QuantityLength(result, targetUnit);
        }

        // =========================
        // Equality (epsilon-based)
        // =========================
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QuantityLength)) return false;

            QuantityLength that = (QuantityLength) o;

            return Math.abs(this.toFeet() - that.toFeet()) < EPSILON;
        }

        @Override
        public int hashCode() {
            return Objects.hash(unit, value);
        }

        @Override
        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }
    }

    // =========================
    // MAIN METHOD (Demo UC7)
    // =========================
    public static void main(String[] args) {

        System.out.println("---- UC7 Explicit Target Unit Results ----");

        System.out.println(QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES),
                LengthUnit.FEET));

        System.out.println(QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES),
                LengthUnit.INCHES));

        System.out.println(QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES),
                LengthUnit.YARDS));

        System.out.println(QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.YARDS),
                new QuantityLength(3.0, LengthUnit.FEET),
                LengthUnit.YARDS));

        System.out.println(QuantityLength.add(
                new QuantityLength(36.0, LengthUnit.INCHES),
                new QuantityLength(1.0, LengthUnit.YARDS),
                LengthUnit.FEET));

        System.out.println(QuantityLength.add(
                new QuantityLength(2.54, LengthUnit.CENTIMETERS),
                new QuantityLength(1.0, LengthUnit.INCHES),
                LengthUnit.CENTIMETERS));

        System.out.println(QuantityLength.add(
                new QuantityLength(5.0, LengthUnit.FEET),
                new QuantityLength(0.0, LengthUnit.INCHES),
                LengthUnit.YARDS));

        System.out.println(QuantityLength.add(
                new QuantityLength(5.0, LengthUnit.FEET),
                new QuantityLength(-2.0, LengthUnit.FEET),
                LengthUnit.INCHES));

        // =========================
        // VALIDATION TESTS
        // =========================
        System.out.println("\n---- UC7 Test Validations ----");

        QuantityLength a = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength b = new QuantityLength(12.0, LengthUnit.INCHES);

        // Commutativity
        System.out.println("Commutative (yards): " +
                QuantityLength.add(a, b, LengthUnit.YARDS)
                        .equals(QuantityLength.add(b, a, LengthUnit.YARDS)));

        // Zero test
        System.out.println("Zero test: " +
                QuantityLength.add(
                        new QuantityLength(5.0, LengthUnit.FEET),
                        new QuantityLength(0.0, LengthUnit.INCHES),
                        LengthUnit.YARDS));

        // Negative test
        System.out.println("Negative test: " +
                QuantityLength.add(
                        new QuantityLength(5.0, LengthUnit.FEET),
                        new QuantityLength(-2.0, LengthUnit.FEET),
                        LengthUnit.INCHES));

        // Large → small scale
        System.out.println("Large to small: " +
                QuantityLength.add(
                        new QuantityLength(1000.0, LengthUnit.FEET),
                        new QuantityLength(500.0, LengthUnit.FEET),
                        LengthUnit.INCHES));

        // Small → large scale
        System.out.println("Small to large: " +
                QuantityLength.add(
                        new QuantityLength(12.0, LengthUnit.INCHES),
                        new QuantityLength(12.0, LengthUnit.INCHES),
                        LengthUnit.YARDS));
    }
}