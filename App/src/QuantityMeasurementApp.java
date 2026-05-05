// ================= IMeasurable =================
interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

// ================= LENGTH =================
enum LengthUnit implements IMeasurable {
    FEET(1.0),
    INCHES(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(0.0328084);

    private final double factor;

    LengthUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return name();
    }
}

// ================= WEIGHT =================
enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(0.001),
    TONNE(1000.0);

    private final double factor;

    WeightUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return name();
    }
}

// ================= VOLUME =================
enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double factor;

    VolumeUnit(double factor) {
        this.factor = factor;
    }

    public double getConversionFactor() {
        return factor;
    }

    public double convertToBaseUnit(double value) {
        return value * factor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / factor;
    }

    public String getUnitName() {
        return name();
    }
}

// ================= GENERIC QUANTITY (UC10–UC12 CORE) =================
class Quantity<U extends IMeasurable> {
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (Double.isNaN(value) || Double.isInfinite(value))
            throw new IllegalArgumentException("Invalid value");

        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public U getUnit() {
        return unit;
    }

    // ================= CONVERSION =================
    public Quantity<U> convertTo(U targetUnit) {
        double base = unit.convertToBaseUnit(value);
        double converted = targetUnit.convertFromBaseUnit(base);
        return new Quantity<>(round(converted), targetUnit);
    }

    // ================= ADDITION =================
    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        double sum = this.unit.convertToBaseUnit(this.value)
                + other.unit.convertToBaseUnit(other.value);

        return new Quantity<>(round(targetUnit.convertFromBaseUnit(sum)), targetUnit);
    }

    // ================= SUBTRACTION (UC12) =================
    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validate(other);

        double result = this.unit.convertToBaseUnit(this.value)
                - other.unit.convertToBaseUnit(other.value);

        return new Quantity<>(round(targetUnit.convertFromBaseUnit(result)), targetUnit);
    }

    // ================= DIVISION (UC12) =================
    public double divide(Quantity<U> other) {
        validate(other);

        double divisor = other.unit.convertToBaseUnit(other.value);
        if (divisor == 0) throw new ArithmeticException("Division by zero");

        double dividend = this.unit.convertToBaseUnit(this.value);

        return dividend / divisor;
    }

    // ================= VALIDATION =================
    private void validate(Quantity<U> other) {
        if (other == null) throw new IllegalArgumentException("Quantity cannot be null");

        if (!this.unit.getClass().equals(other.unit.getClass()))
            throw new IllegalArgumentException("Cross-category operation not allowed");
    }

    // ================= EQUALITY =================
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Quantity<?>)) return false;

        Quantity<?> other = (Quantity<?>) obj;

        if (!this.unit.getClass().equals(other.unit.getClass()))
            return false;

        double b1 = this.unit.convertToBaseUnit(this.value);
        double b2 = ((IMeasurable) other.unit).convertToBaseUnit(other.value);

        return Math.abs(b1 - b2) < 0.0001;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(unit.convertToBaseUnit(value));
    }

    @Override
    public String toString() {
        return "Quantity(" + value + ", " + unit.getUnitName() + ")";
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}

// ================= APP (UC10–UC12 DEMO) =================
public class QuantityMeasurementApp {

    public static <U extends IMeasurable> void eq(Quantity<U> a, Quantity<U> b) {
        System.out.println(a + " == " + b + " → " + a.equals(b));
    }

    public static <U extends IMeasurable> void conv(Quantity<U> a, U u) {
        System.out.println(a + " → " + a.convertTo(u));
    }

    public static <U extends IMeasurable> void add(Quantity<U> a, Quantity<U> b, U u) {
        System.out.println(a + " + " + b + " → " + a.add(b, u));
    }

    public static <U extends IMeasurable> void sub(Quantity<U> a, Quantity<U> b, U u) {
        System.out.println(a + " - " + b + " → " + a.subtract(b, u));
    }

    public static <U extends IMeasurable> void div(Quantity<U> a, Quantity<U> b) {
        System.out.println(a + " / " + b + " → " + a.divide(b));
    }

    public static void main(String[] args) {

        // ===== LENGTH =====
        Quantity<LengthUnit> l1 = new Quantity<>(10, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(6, LengthUnit.INCHES);

        eq(l1, l2);
        conv(l1, LengthUnit.INCHES);
        add(l1, l2, LengthUnit.FEET);
        sub(l1, l2, LengthUnit.FEET);
        div(l1, new Quantity<>(5, LengthUnit.FEET));

        // ===== WEIGHT =====
        Quantity<WeightUnit> w1 = new Quantity<>(10, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(5000, WeightUnit.GRAM);

        eq(w1, w2);
        sub(w1, w2, WeightUnit.KILOGRAM);
        div(w1, w2);

        // ===== VOLUME =====
        Quantity<VolumeUnit> v1 = new Quantity<>(5, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(500, VolumeUnit.MILLILITRE);

        eq(v1, v2);
        add(v1, v2, VolumeUnit.LITRE);
        sub(v1, v2, VolumeUnit.LITRE);
        div(v1, v2);

        // ===== CROSS CATEGORY SAFETY =====
        System.out.println("Cross category check:");
        System.out.println(l1.equals(v1)); // false
    }
}