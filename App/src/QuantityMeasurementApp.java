import java.util.Objects;

public class QuantityMeasurementApp {

    // =========================
    // UC8: Standalone-style ENUM
    // =========================
    enum LengthUnit {
        FEET(1.0),
        INCHES(1.0 / 12.0),
        YARDS(3.0),
        CENTIMETERS(1.0 / 30.48);

        private final double conversionFactorToFeet;

        LengthUnit(double factor) {
            this.conversionFactorToFeet = factor;
        }

        // Convert THIS unit → base unit (feet)
        public double convertToBaseUnit(double value) {
            return value * conversionFactorToFeet;
        }

        // Convert base unit (feet) → THIS unit
        public double convertFromBaseUnit(double baseValue) {
            return baseValue / conversionFactorToFeet;
        }

        public double getConversionFactor() {
            return conversionFactorToFeet;
        }
    }

    // =========================
    // QuantityLength (Refactored)
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
                throw new IllegalArgumentException("Invalid value");
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

        // =========================
        // UC5: Convert
        // =========================
        public QuantityLength convertTo(LengthUnit targetUnit) {
            if (targetUnit == null) {
                throw new IllegalArgumentException("Target unit cannot be null");
            }

            double baseValue = unit.convertToBaseUnit(value);
            double converted = targetUnit.convertFromBaseUnit(baseValue);

            return new QuantityLength(converted, targetUnit);
        }

        // =========================
        // UC6: Add (default unit = first operand)
        // =========================
        public QuantityLength add(QuantityLength other) {
            if (other == null) {
                throw new IllegalArgumentException("Other cannot be null");
            }
            return add(this, other, this.unit);
        }

        // =========================
        // UC7: Add with explicit target unit
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

            double baseSum =
                    q1.unit.convertToBaseUnit(q1.value) +
                            q2.unit.convertToBaseUnit(q2.value);

            double result = targetUnit.convertFromBaseUnit(baseSum);

            return new QuantityLength(result, targetUnit);
        }

        // =========================
        // Equality (UC1–UC4)
        // =========================
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QuantityLength)) return false;

            QuantityLength that = (QuantityLength) o;

            double thisBase = this.unit.convertToBaseUnit(this.value);
            double thatBase = that.unit.convertToBaseUnit(that.value);

            return Math.abs(thisBase - thatBase) < EPSILON;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, unit);
        }

        @Override
        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }
    }

    // =========================
    // MAIN METHOD (Demo UC8)
    // =========================
    public static void main(String[] args) {

        System.out.println("---- UC8 Refactored Output ----");

        // Conversion
        System.out.println(new QuantityLength(1.0, LengthUnit.FEET)
                .convertTo(LengthUnit.INCHES));

        // Addition (explicit target)
        System.out.println(QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.FEET),
                new QuantityLength(12.0, LengthUnit.INCHES),
                LengthUnit.FEET));

        // Equality
        System.out.println(new QuantityLength(36.0, LengthUnit.INCHES)
                .equals(new QuantityLength(1.0, LengthUnit.YARDS)));

        // Addition (yards)
        System.out.println(QuantityLength.add(
                new QuantityLength(1.0, LengthUnit.YARDS),
                new QuantityLength(3.0, LengthUnit.FEET),
                LengthUnit.YARDS));

        // Conversion cm → inch
        System.out.println(new QuantityLength(2.54, LengthUnit.CENTIMETERS)
                .convertTo(LengthUnit.INCHES));

        // Zero addition
        System.out.println(QuantityLength.add(
                new QuantityLength(5.0, LengthUnit.FEET),
                new QuantityLength(0.0, LengthUnit.INCHES),
                LengthUnit.FEET));

        // Unit direct conversion
        System.out.println("Feet to base: " +
                LengthUnit.FEET.convertToBaseUnit(12.0));

        System.out.println("Inches to base: " +
                LengthUnit.INCHES.convertToBaseUnit(12.0));

        // =========================
        // VALIDATION TESTS
        // =========================
        System.out.println("\n---- Validation ----");

        // Commutativity
        QuantityLength a = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength b = new QuantityLength(12.0, LengthUnit.INCHES);

        System.out.println("Commutative: " +
                QuantityLength.add(a, b, LengthUnit.YARDS)
                        .equals(QuantityLength.add(b, a, LengthUnit.YARDS)));

        // Round-trip conversion
        QuantityLength original = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength roundTrip = original
                .convertTo(LengthUnit.INCHES)
                .convertTo(LengthUnit.FEET);

        System.out.println("Round-trip equal: " + original.equals(roundTrip));
    }
}