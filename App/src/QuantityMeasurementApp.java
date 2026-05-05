import java.util.Objects;
import java.util.function.DoubleBinaryOperator;

/* =======================
   IMeasurable Interface
======================= */
interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
}

/* =======================
   Arithmetic Operation Enum (UC13 Centralization)
======================= */
enum ArithmeticOperation {
    ADD((a, b) -> a + b),
    SUBTRACT((a, b) -> a - b),
    DIVIDE((a, b) -> {
        if (b == 0) throw new ArithmeticException("Division by zero");
        return a / b;
    });

    private final DoubleBinaryOperator operation;

    ArithmeticOperation(DoubleBinaryOperator operation) {
        this.operation = operation;
    }

    public double compute(double a, double b) {
        return operation.applyAsDouble(a, b);
    }
}

/* =======================
   Volume Unit Example
======================= */
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

/* =======================
   Generic Quantity Class (UC13 Refactored DRY Version)
======================= */
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

    /* =======================
       CENTRALIZED VALIDATION (DRY)
    ======================= */
    private void validate(Quantity<U> other) {
        if (other == null)
            throw new IllegalArgumentException("Quantity cannot be null");
        if (!this.unit.getClass().equals(other.unit.getClass()))
            throw new IllegalArgumentException("Different categories not allowed");
    }

    /* =======================
       CENTRALIZED ARITHMETIC CORE (DRY)
    ======================= */
    private double perform(Quantity<U> other, ArithmeticOperation op) {
        validate(other);

        double a = this.unit.convertToBaseUnit(this.value);
        double b = other.unit.convertToBaseUnit(other.value);

        return op.compute(a, b);
    }

    /* =======================
       ADDITION
    ======================= */
    public Quantity<U> add(Quantity<U> other) {
        double resultBase = perform(other, ArithmeticOperation.ADD);
        double converted = this.unit.convertFromBaseUnit(resultBase);
        return new Quantity<>(converted, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        double resultBase = perform(other, ArithmeticOperation.ADD);
        double converted = targetUnit.convertFromBaseUnit(resultBase);
        return new Quantity<>(converted, targetUnit);
    }

    /* =======================
       SUBTRACTION
    ======================= */
    public Quantity<U> subtract(Quantity<U> other) {
        double resultBase = perform(other, ArithmeticOperation.SUBTRACT);
        double converted = this.unit.convertFromBaseUnit(resultBase);
        return new Quantity<>(converted, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        double resultBase = perform(other, ArithmeticOperation.SUBTRACT);
        double converted = targetUnit.convertFromBaseUnit(resultBase);
        return new Quantity<>(converted, targetUnit);
    }

    /* =======================
       DIVISION (returns scalar)
    ======================= */
    public double divide(Quantity<U> other) {
        return perform(other, ArithmeticOperation.DIVIDE);
    }

    /* =======================
       EQUALITY
    ======================= */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Quantity<?> other)) return false;

        if (!this.unit.getClass().equals(other.unit.getClass()))
            return false;

        double a = this.unit.convertToBaseUnit(this.value);
        double b = other.unit.convertToBaseUnit(other.value);

        return Math.abs(a - b) < 0.0001;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit.getClass(), unit.convertToBaseUnit(value));
    }

    @Override
    public String toString() {
        return "Quantity(" + value + ", " + unit.getUnitName() + ")";
    }
}

/* =======================
   Demo Application
======================= */
public class QuantityMeasurementApp {

    public static void main(String[] args) {

        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> v3 = new Quantity<>(1.0, VolumeUnit.GALLON);

        // ADD
        System.out.println(v1.add(v2));

        // SUBTRACT
        System.out.println(v1.subtract(v2));

        // DIVIDE
        System.out.println(v1.divide(v2));

        // CROSS UNIT ADD
        System.out.println(v1.add(v3, VolumeUnit.MILLILITRE));

        // EQUALITY
        System.out.println(v1.equals(v2));
    }
}

/* =======================
   Sample UC13 Test Cases (simplified)
======================= */
class QuantityTest {

    void testAdd() {
        Quantity<VolumeUnit> a = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> b = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        assert a.add(b).equals(new Quantity<>(2.0, VolumeUnit.LITRE));
    }

    void testSubtract() {
        Quantity<VolumeUnit> a = new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> b = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        assert a.subtract(b).equals(new Quantity<>(4.0, VolumeUnit.LITRE));
    }

    void testDivide() {
        Quantity<VolumeUnit> a = new Quantity<>(10.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> b = new Quantity<>(2.0, VolumeUnit.LITRE);
        assert a.divide(b) == 5.0;
    }
}