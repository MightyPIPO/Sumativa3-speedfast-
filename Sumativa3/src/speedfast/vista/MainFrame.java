package speedfast.vista;

import javax.swing.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabs;

    public MainFrame() {
        setTitle("SpeedFast - Sistema de Gestión");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tabs = new JTabbedPane();
        tabs.addTab("Repartidores", new RepartidorPanel());
        tabs.addTab("Pedidos", new PedidoPanel());
        tabs.addTab("Entregas", new EntregaPanel());
        add(tabs);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame ventana = new MainFrame();
            ventana.setVisible(true);
        });
    }
}