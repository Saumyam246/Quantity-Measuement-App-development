

public class QuantityMeasurementApp {

    // Equality check
    public static boolean demonstrateLengthEquality(Length l1, Length l2) {
        return l1.equals(l2);
    }

    // Comparison using raw values
    public static boolean demonstrateLengthComparison(
            double value1, Length.LengthUnit unit1,
            double value2, Length.LengthUnit unit2) {

        Length l1 = new Length(value1, unit1);
        Length l2 = new Length(value2, unit2);

        return demonstrateLengthEquality(l1, l2);
    }

    // Conversion (value-based)
    public static Length demonstrateLengthConversion(
            double value,
            Length.LengthUnit fromUnit,
            Length.LengthUnit toUnit) {

        Length length = new Length(value, fromUnit);
        return length.convertTo(toUnit);
    }

    // Conversion (object-based) → Method Overloading
    public static Length demonstrateLengthConversion(
            Length length,
            Length.LengthUnit toUnit) {

        return length.convertTo(toUnit);
    }

    // Main method
    public static void main(String[] args) {

        // Equality
        System.out.println(demonstrateLengthComparison(
                1.0, Length.LengthUnit.FEET,
                12.0, Length.LengthUnit.INCHES));

        // Conversion
        Length result = demonstrateLengthConversion(
                3.0, Length.LengthUnit.FEET,
                Length.LengthUnit.INCHES);

        System.out.println(result);

        Length yard = new Length(2.0, Length.LengthUnit.YARDS);
        System.out.println(demonstrateLengthConversion(yard,
                Length.LengthUnit.INCHES));
    }
}