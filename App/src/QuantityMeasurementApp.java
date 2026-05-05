public class QuantityMeasurementApp {
    public static class Feet {
        private final double value;
        public Feet (double value){
            this.value=value;
        }
        @Override
        public boolean equals(Object obj){

            if (obj == this) {
                return true;
            }

            if(obj == null ){
                return false;
            }

            if (!(obj instanceof Feet)) {
                return false;
            }


            Feet c = (Feet) obj;


            return Double.compare(value, c.value) == 0;
        }

    }

    public static  class Inches {

        private final double value;

        public Inches(double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {

            if (obj == this) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (!(obj instanceof Inches)) {
                return false;
            }


            Inches c = (Inches) obj;


            return Double.compare(value, c.value) == 0;
        }
    }
    public static void demonstrateFeetEquality(){
        Feet f1= new Feet(1.0);
        Feet f2 = new Feet(1.0);

        System.out.println(f1.equals(f2));
    }

    public static void demonstrateInchesEquality(){
        Inches i1= new Inches(1.0);
        Inches i2 = new Inches(2.0);
        System.out.println(i1.equals(i2));
    }


    public static void main(String[] args){
        demonstrateFeetEquality();
        demonstrateInchesEquality();




    }

}