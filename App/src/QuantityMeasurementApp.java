

public class QuantityMeasurementApp {

    public static void demonstrateLengthEquality(Length length1, Length length2) {
        System.out.println("Length Equality: " + length1.equals(length2));
    }

    public static void demonstrateFeetEquality() {
        Length l1 = new Length(1.0, Length.LengthUnit.FEET);
        Length l2 = new Length(1.0, Length.LengthUnit.FEET);

        System.out.println("Feet Equality: " + l1.equals(l2));
    }

    public static void demonstrateInchesEquality() {
        Length l1 = new Length(1.0, Length.LengthUnit.INCHES);
        Length l2 = new Length(1.0, Length.LengthUnit.INCHES);

        System.out.println("Inches Equality: " + l1.equals(l2));
    }

    public static void demonstrateFeetInchesComparison() {
        Length l1 = new Length(1.0, Length.LengthUnit.FEET);
        Length l2 = new Length(12.0, Length.LengthUnit.INCHES);

        System.out.println("Feet vs Inches: " + l1.equals(l2));
    }

    public static void main(String[] args) {
        demonstrateFeetEquality();
        demonstrateInchesEquality();
        demonstrateFeetInchesComparison();
    }
}