import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;

public class GraphicsDisplay extends JPanel {
    private Double[][] graphicsData;

    private boolean showAxis = true;
    private boolean showMarkers = false;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private double scale;

    private final BasicStroke graphicsStroke;
    private final BasicStroke axisStroke;
    private final BasicStroke markerStroke;

    private final Font axisFont;

    public GraphicsDisplay() {

        setBackground(Color.WHITE);


        // Новый массив значений для штрихов
        float[] dashPattern = {20, 10, 5, 10};



        // Использование массива значений при создании BasicStroke
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, dashPattern, 0.0f);

        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    public void showGraphics(Double[][] graphicsData) {
        this.graphicsData = graphicsData;
        repaint();
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (graphicsData == null || graphicsData.length == 0){
            return;
        }

        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY)
                minY = graphicsData[i][1];

            if (graphicsData[i][1] > maxY)
                maxY = graphicsData[i][1];

        }

        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);

        scale = Math.min(scaleX, scaleY);

        if (scale == scaleX) {
            double yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale == scaleY) {
            double xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;
            maxX += xIncrement;
            minX -= xIncrement;
        }

        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        if (showAxis)
            paintAxis(canvas);

        paintGraphics(canvas);

        if (showMarkers)
            paintMarkers(canvas);

        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);

        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            if (i > 0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }

        canvas.draw(graphics);
    }

    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);

        final int MS = 5;

        for (Double[] point : graphicsData) {
            int integerValue = point[1].intValue();
            int digitSum = calculateDigitSum(integerValue);

            if (digitSum < 10) {
                canvas.setColor(Color.BLUE);
            } else {
                canvas.setColor(Color.RED);
            }

            Point2D.Double center = xyToPoint(point[0], point[1]);
            int x = (int) center.x;
            int y = (int) center.y;

            int[] xPoints = {x - 5, x, x + 5};
            int[] yPoints = {y - 5, y + 6, y - 5};
            canvas.drawPolygon(xPoints, yPoints, 3);
            Double[] specialPoint = {0.0, 1.0};
            Point2D.Double specialCenter = xyToPoint(specialPoint[0], specialPoint[1]);
            Line2D.Double dash = new Line2D.Double();
            Point2D.Double dashStart = shiftPoint(specialCenter, -8, -1);
            Point2D.Double dashEnd = shiftPoint(specialCenter, 8, -1);
            dash.setLine(dashStart, dashEnd);
            canvas.setColor(Color.BLACK);
            canvas.setPaint(Color.BLACK);
            canvas.draw(dash);

        }
    }
    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY) {

        // Инициализировать новый экземпляр точки
        Point2D.Double dest = new Point2D.Double();

        // Задать ее координаты как координаты существующей точки + заданные смещения
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
    private int calculateDigitSum(int number) {
        int sum = 0;
        while (number != 0) {
            sum += number % 10;
            number /= 10;
        }
        return sum;
    }

    protected void paintAxis(Graphics2D canvas) {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);

        FontRenderContext context = canvas.getFontRenderContext();
        Point2D.Double labelPos = xyToPoint(0, maxY);
        Point2D.Double label0Pos = xyToPoint(0, 0);

        if (minX <= 0.0 && maxX >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));

            GeneralPath arrow = new GeneralPath();

            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());

            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);

            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());

            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            //Point2D.Double labelPos = xyToPoint(0, maxY);//

            canvas.drawString("y", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()));
            canvas.drawString("-", (float) labelPos.getX() -5, (float) (label0Pos.getY() - bounds.getY())-75);
            canvas.drawString("1", (float) label0Pos.getX() +10, (float) (label0Pos.getY() - bounds.getY())-75);
        }

        if (minY <= 0.0 && maxY >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));

            GeneralPath arrow = new GeneralPath();

            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());

            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);

            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);

            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("x", context);


            //Point2D.Double label0Pos = xyToPoint(0, 0);
            FontMetrics fontMetrics = canvas.getFontMetrics();
            Rectangle2D bounds0 = fontMetrics.getStringBounds("0", canvas);

            canvas.drawString("0", (float) label0Pos.getX() + 10, (float) (label0Pos.getY() - bounds0.getY()));

        }

    }

    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale, deltaY * scale);

    }


}