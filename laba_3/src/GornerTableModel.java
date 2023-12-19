import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@SuppressWarnings("serial")
class GornerTableModel extends AbstractTableModel {
    private Double[] coefficients;
    private Double from;
    private Double to;
    private Double step;

    public GornerTableModel(Double from, Double to, Double step, Double[] coefficients) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.setCoefficients(coefficients);
    }

    public Double getFrom() {
        return from;
    }

    public Double getTo() {
        return to;
    }

    public Double getStep() {
        return step;
    }

    public int getColumnCount() { // В данной модели два столбца
        return 3;
    }

    public int getRowCount() { // Вычислить количество точек между началом и концом отрезка исходя из шага табулирования
        return Double.valueOf(Math.ceil((to - from) / step)).intValue() + 1;
    }

    public Object getValueAt(int row, int col) {
        double x = from + step * row; // Вычислить значение X как НАЧАЛО_ОТРЕЗКА + ШАГ*НОМЕР_СТРОКИ

        Double result = 0.0;
        for (int i = coefficients.length - 1; i >= 0; i--) {
            result = result * x + coefficients[i];
        }
        double fractionalPart = result - Math.floor(result);
        //System.out.println("fractional part= "+fractionalPart);
        //String fractionalStr=Double.toString(fractionalPart).substring(2,7);
        //System.out.println("string fractional= "+fractionalStr);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(5);
        formatter.setGroupingUsed(false);
        String formattedDouble = formatter.format(result);
        int pointIndex=formattedDouble.indexOf(",");
        if (pointIndex!=-1){
            String fractionalPartString = formattedDouble.substring(pointIndex+1);
            System.out.println("fractional part with formatter  = "+fractionalPartString);

        }


        String numberString = String.valueOf(result);
        int decimalIndex = numberString.indexOf(".");
        int digitsAfterDecimal = numberString.length() - decimalIndex - 1;
        fractionalPart *= Math.pow(10, digitsAfterDecimal);
        fractionalPart=Math.round(fractionalPart);
        double sqrtfractional=Math.sqrt(fractionalPart);
        int roundedNumber = (int) Math.round(sqrtfractional);
        boolean hasIntegerSquareRoot=false;
        if (sqrtfractional == roundedNumber) {
            hasIntegerSquareRoot=true;
        }


        switch(col) {
            case 0:
                return x;
            case 1:
                return result;
            case 2:
                return hasIntegerSquareRoot;
        }
        return 0;
    }

    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return "Значение X"; // Название 1-го столбца
            case 1:
                return "Значение многочлена"; // Название 2-го столбца
            case 2:
                return "Дробная часть является квадратом?";
        }
        return null;
    }

    public Class<?> getColumnClass(int col) { // И в 1-ом и во 2-ом столбце находятся значения типа Double
        if (col == 2)
            return Boolean.class;
        else
            return Double.class;
    }

    public Double[] getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(Double[] coefficients) {
        this.coefficients = coefficients;
    }
}

