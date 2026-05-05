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

        // UC6 ADD METHOD
        public QuantityLength add(QuantityLength other) {
            if (other == null) {
                throw new IllegalArgumentException("Other quantity cannot be null");
            }

            double sumFeet = this.toFeet() + other.toFeet();
            double result = fromFeet(sumFeet, this.unit);

            return new QuantityLength(result, this.unit);
        }

        // Static version (optional)
        public static QuantityLength add(QuantityLength q1, QuantityLength q2) {
            if (q1 == null || q2 == null) {
                throw new IllegalArgumentException("Inputs cannot be null");
            }
            return q1.add(q2);
        }

        // Equality using epsilon
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
    // MAIN METHOD (Demo)
    // =========================
    public static void main(String[] args) {

        System.out.println("---- UC6 Addition Results ----");

        System.out.println(new QuantityLength(1.0, LengthUnit.FEET)
                .add(new QuantityLength(2.0, LengthUnit.FEET)));

        System.out.println(new QuantityLength(1.0, LengthUnit.FEET)
                .add(new QuantityLength(12.0, LengthUnit.INCHES)));

        System.out.println(new QuantityLength(12.0, LengthUnit.INCHES)
                .add(new QuantityLength(1.0, LengthUnit.FEET)));

        System.out.println(new QuantityLength(1.0, LengthUnit.YARDS)
                .add(new QuantityLength(3.0, LengthUnit.FEET)));

        System.out.println(new QuantityLength(36.0, LengthUnit.INCHES)
                .add(new QuantityLength(1.0, LengthUnit.YARDS)));

        System.out.println(new QuantityLength(2.54, LengthUnit.CENTIMETERS)
                .add(new QuantityLength(1.0, LengthUnit.INCHES)));

        System.out.println(new QuantityLength(5.0, LengthUnit.FEET)
                .add(new QuantityLength(0.0, LengthUnit.INCHES)));

        System.out.println(new QuantityLength(5.0, LengthUnit.FEET)
                .add(new QuantityLength(-2.0, LengthUnit.FEET)));

        // =========================
        // BASIC TEST VALIDATIONS
        // =========================
        System.out.println("\n---- Test Validations ----");

        QuantityLength a = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength b = new QuantityLength(12.0, LengthUnit.INCHES);

        // Commutativity
        System.out.println("Commutative: " + a.add(b).equals(b.add(a)));

        // Identity (zero)
        QuantityLength zeroTest = new QuantityLength(5.0, LengthUnit.FEET)
                .add(new QuantityLength(0.0, LengthUnit.INCHES));
        System.out.println("Identity (5 ft + 0): " + zeroTest);

        // Negative values
        QuantityLength negativeTest = new QuantityLength(5.0, LengthUnit.FEET)
                .add(new QuantityLength(-2.0, LengthUnit.FEET));
        System.out.println("Negative (5 ft + -2 ft): " + negativeTest);

        // Large values
        QuantityLength largeTest = new QuantityLength(1e6, LengthUnit.FEET)
                .add(new QuantityLength(1e6, LengthUnit.FEET));
        System.out.println("Large values: " + largeTest);

        // Small values
        QuantityLength smallTest = new QuantityLength(0.001, LengthUnit.FEET)
                .add(new QuantityLength(0.002, LengthUnit.FEET));
        System.out.println("Small values: " + smallTest);
    }
}