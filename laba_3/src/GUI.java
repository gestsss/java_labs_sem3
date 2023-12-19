import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class GUI extends JFrame {
    private static final String TITLE = "Схема Горнера";
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;

    private Double[] coefficients;

    private JFileChooser fileChooser;

    private JMenuItem saveToTextMenuItem;
    private JMenuItem saveToGraphicsMenuItem;
    private JMenuItem searchValueMenuItem;

    private final JTextField textFieldFrom;
    private final JTextField textFieldTo;
    private final JTextField textFieldStep;
    {
        textFieldFrom = createTextField();
        textFieldTo = createTextField();
        textFieldStep = createTextField();
    }

    private Box hBoxResult;

    private final GornerTableCellRenderer renderer = new GornerTableCellRenderer();
    private GornerTableModel data;

    private JTextField createTextField() {
        JTextField field = new JTextField("0", 10);
        field.setMaximumSize(field.getPreferredSize());
        return field;
    }

    public void setCoefficients(Double[] coefficients) {
        this.coefficients = coefficients;
    }

    private void calculateResult() {
        try {
            Double from = Double.parseDouble(textFieldFrom.getText());
            Double to = Double.parseDouble(textFieldTo.getText());
            Double step = Double.parseDouble(textFieldStep.getText());
            data = new GornerTableModel(from, to, step, GUI.this.coefficients);

            JTable table = new JTable(data);
            table.setDefaultRenderer(Double.class, renderer);
            table.setRowHeight(30);
            hBoxResult.removeAll();
            hBoxResult.add(new JScrollPane(table));
            getContentPane().validate();
            saveToTextMenuItem.setEnabled(true);
            saveToGraphicsMenuItem.setEnabled(true);
            searchValueMenuItem.setEnabled(true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(GUI.this, "Ошибка в формате записи числа с плавающей точкой", "Ошибочный формат числа",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    Action createActionSaveToFile() {
        return new AbstractAction("Сохранить в текстовый файл") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }

                if (fileChooser.showSaveDialog(GUI.this) == JFileChooser.APPROVE_OPTION)
                    saveToTextFile(fileChooser.getSelectedFile());
            }
        };

    }

    Action createActionSaveToGraphics() {

        return new AbstractAction("Сохранить данные для построения графика") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showSaveDialog(GUI.this) == JFileChooser.APPROVE_OPTION)
                    saveToGraphicsFile(fileChooser.getSelectedFile());
            }
        };
    }

    Action createActionSearchValue() {
        return new AbstractAction("Найти значение многочлена") {
            public void actionPerformed(ActionEvent event) {
                String value = JOptionPane.showInputDialog(GUI.this, "Введите значение для поиска",
                        "Поиск значения", JOptionPane.QUESTION_MESSAGE);
                renderer.setNeedle(value);
                getContentPane().repaint();

            }
        };
    }

    JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        createMenuItemAction(fileMenu);
        JMenu tableMenu = new JMenu(("Таблица"));
        JMenu referenceMenu = new JMenu(("Справка"));
        menuBar.add(fileMenu);
        menuBar.add(tableMenu);
        menuBar.add(referenceMenu);
        JMenuItem creators = new JMenuItem("О программе");
        creators.addActionListener(e -> {
            String message = "Разработчик :\nСтудентка 2 курса 7 группы\nГесть Анна";
            JOptionPane.showMessageDialog(GUI.this, message, "Сообщение", JOptionPane.PLAIN_MESSAGE);
        });
        referenceMenu.add(creators);
        return menuBar;
    }

    void createMenuItemAction(JMenu fileMenu) {
        Action saveToTextAction = createActionSaveToFile();
        saveToTextMenuItem = fileMenu.add(saveToTextAction);
        saveToTextMenuItem.setEnabled(false);

        Action saveToGraphicsAction = createActionSaveToGraphics();
        saveToGraphicsMenuItem = fileMenu.add(saveToGraphicsAction);
        saveToGraphicsMenuItem.setEnabled(false);

        Action searchValueAction = createActionSearchValue();
        searchValueMenuItem = fileMenu.add(searchValueAction);
        searchValueMenuItem.setEnabled(false);
    }

    Box createBoxRange() {
        Box hboxRange = Box.createHorizontalBox();
        hboxRange.setBorder(BorderFactory.createBevelBorder(1));
        hboxRange.add(Box.createHorizontalGlue());
        hboxRange.add(new JLabel("X изменяется на интервале от:"));
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldFrom);
        hboxRange.add(Box.createHorizontalStrut(20));
        hboxRange.add(new JLabel("до:"));
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldTo);
        hboxRange.add(Box.createHorizontalStrut(20));
        hboxRange.add(new JLabel("с шагом:"));
        hboxRange.add(Box.createHorizontalStrut(10));
        hboxRange.add(textFieldStep);
        hboxRange.add(Box.createHorizontalGlue());
        hboxRange.setPreferredSize(new Dimension(Double.valueOf(hboxRange.getMaximumSize().getWidth()).intValue(), Double.valueOf(hboxRange.getMinimumSize().getHeight()).intValue() * 2));
        return hboxRange;
    }

    Box createBoxButtons() {
        JButton buttonCalc = new JButton("Вычислить");
        buttonCalc.addActionListener(ev -> calculateResult());
        JButton buttonReset = new JButton("Очистить поля");
        buttonReset.addActionListener(ev -> {
            textFieldFrom.setText("0.0");
            textFieldTo.setText("0.0");
            textFieldStep.setText("0.0");
            hBoxResult.removeAll();
            hBoxResult.add(new JPanel());
            saveToTextMenuItem.setEnabled(false);
            saveToGraphicsMenuItem.setEnabled(false);
            searchValueMenuItem.setEnabled(false);
            getContentPane().validate();
        });
        Box hboxButtons = Box.createHorizontalBox();
        hboxButtons.setBorder(BorderFactory.createBevelBorder(1));
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.add(buttonCalc);
        hboxButtons.add(Box.createHorizontalStrut(30));
        hboxButtons.add(buttonReset);
        hboxButtons.add(Box.createHorizontalGlue());
        hboxButtons.setPreferredSize(new Dimension(Double.valueOf(hboxButtons.getMaximumSize().getWidth()).intValue(), Double.valueOf(hboxButtons.getMinimumSize().getHeight()).intValue() * 2));
        return hboxButtons;
    }

    Box createBoxResult() {
        hBoxResult = Box.createHorizontalBox();
        hBoxResult.add(new JPanel());
        return hBoxResult;
    }

    public GUI() {
        super(TITLE);
        this.setSize(WIDTH, HEIGHT);

        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setJMenuBar(createMenu());

        getContentPane().add(createBoxRange(), BorderLayout.NORTH);
        getContentPane().add(createBoxButtons(), BorderLayout.SOUTH);
        getContentPane().add(createBoxResult(), BorderLayout.CENTER);

        setVisible(true);
    }

    private void saveToGraphicsFile(File selectedFile) {
        try(DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile))) {
            for (int i = 0; i < data.getRowCount(); i++) {
                out.writeDouble((Double) data.getValueAt(i, 0));
                out.writeDouble((Double) data.getValueAt(i, 1));
            }
        } catch (Exception ignored) {
        }

    }

    private void saveToTextFile(File selectedFile) {
        try(PrintStream out = new PrintStream(selectedFile)) {
            out.println("Результаты табулирования многочлена по схеме Горнера");
            out.print("Многочлен: ");
            for (int i = 0; i < coefficients.length; i++) {
                out.print(coefficients[i] + "*X^" +
                        (coefficients.length - i - 1));
                if (i != coefficients.length - 1)
                    out.print(" + ");
            }

            out.println();
            out.println("Интервал от " + data.getFrom() + " до " +
                    data.getTo() + " с шагом " + data.getStep());
            out.println("====================================================");

            for (int i = 0; i < data.getRowCount(); i++)
                out.println("Значение в точке " + data.getValueAt(i, 0) + " равно " + data.getValueAt(i, 1));

        } catch (FileNotFoundException ignored) {

        }
    }
}