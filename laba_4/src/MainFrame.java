import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class MainFrame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private JFileChooser fileChooser;

    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;

    private final GraphicsDisplay display = new GraphicsDisplay();

    private boolean fileLoaded = false;

    private Action createGraphicAction() {
        return new AbstractAction("Открыть файл с графиком") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser == null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(MainFrame.this) ==
                        JFileChooser.APPROVE_OPTION)
                    openGraphics(fileChooser.getSelectedFile());
            }
        };
    }

    private Action createShowAxisAction(){
        return new AbstractAction("Показывать оси координат") {
            public void actionPerformed(ActionEvent event) {
                display.setShowAxis(showAxisMenuItem.isSelected());
            }
        };
    }

    private Action createMarkerAction(){
        return new AbstractAction("Показывать маркеры точек") {
            public void actionPerformed(ActionEvent event) {
                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.add(createGraphicAction());
        menuBar.add(fileMenu);
        JMenu graphicsMenu = new JMenu("График");
        showMarkersMenuItem = new JCheckBoxMenuItem(createMarkerAction());
        graphicsMenu.add(showMarkersMenuItem);
        graphicsMenu.addMenuListener(new GraphicsMenuListener());
        menuBar.add(graphicsMenu);
        showAxisMenuItem = new JCheckBoxMenuItem(createShowAxisAction());
        showAxisMenuItem.setSelected(true);
        graphicsMenu.add(showAxisMenuItem);
    }

    public MainFrame() {
        super("Построение графиков функций на основе заранее подготовленных файлов");
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2);

        setExtendedState(MAXIMIZED_BOTH);
        createMenuBar();

        getContentPane().add(display, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        //display.setShowMarkers(true);
    }

    protected void openGraphics(File selectedFile) {
        try(DataInputStream in = new DataInputStream(new FileInputStream(selectedFile))) {
            Double[][] graphicsData = new Double[in.available() / (Double.SIZE / 8) / 2][];

            int i = 0;
            while (in.available() > 0) {
                double x = in.readDouble();
                double y = in.readDouble();

                graphicsData[i++] = new Double[]{x, y};
            }

            if (graphicsData.length > 0) {
                fileLoaded = true;
                display.showGraphics(graphicsData);
            }

        } catch (FileNotFoundException ex) {

            JOptionPane.showMessageDialog(
                    MainFrame.this,
                    "Указанный файл не найден",
                    "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
        }
    }

    private class GraphicsMenuListener implements MenuListener {
        public void menuSelected(MenuEvent e) {
            showAxisMenuItem.setEnabled(fileLoaded);
            showMarkersMenuItem.setEnabled(fileLoaded);
        }

        public void menuDeselected(MenuEvent e) {
        }

        public void menuCanceled(MenuEvent e) {
        }
    }
}