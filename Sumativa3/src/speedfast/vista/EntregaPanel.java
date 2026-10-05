package speedfast.vista;

import speedfast.dao.EntregaDAO;
import speedfast.dao.PedidoDAO;
import speedfast.dao.RepartidorDAO;
import speedfast.modelo.Entrega;
import speedfast.modelo.Pedido;
import speedfast.modelo.Repartidor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class EntregaPanel extends JPanel {

    private JComboBox<Pedido> cmbPedido;
    private JComboBox<Repartidor> cmbRepartidor;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JComboBox<Pedido> cmbFiltroPedido;
    private JComboBox<Repartidor> cmbFiltroRepartidor;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private EntregaDAO entregaDAO;
    private PedidoDAO pedidoDAO;
    private RepartidorDAO repartidorDAO;

    private int idSeleccionado = -1;

    public EntregaPanel() {
        entregaDAO = new EntregaDAO();
        pedidoDAO = new PedidoDAO();
        repartidorDAO = new RepartidorDAO();
        setLayout(new BorderLayout(10, 10));
        crearFormulario();
        crearTabla();
        cargarCombos();
        cargarTabla();
    }

    private void crearFormulario() {
        JPanel superior = new JPanel();
        superior.setLayout(
            new BoxLayout(
                superior,
                BoxLayout.Y_AXIS
            )
        );
        JPanel formulario = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );
        formulario.add(new JLabel("Pedido:"));
        cmbPedido = new JComboBox<>();
        cmbPedido.setPreferredSize(
            new Dimension(180, 25)
        );
        formulario.add(cmbPedido);
        formulario.add(new JLabel("Repartidor:"));
        cmbRepartidor = new JComboBox<>();
        cmbRepartidor.setPreferredSize(
            new Dimension(180, 25)
        );
        formulario.add(cmbRepartidor);
        formulario.add(new JLabel("Fecha:"));
        txtFecha = new JTextField(10);
        txtFecha.setToolTipText("Formato: yyyy-MM-dd");
        formulario.add(txtFecha);
        formulario.add(new JLabel("Hora:"));
        txtHora = new JTextField(8);
        txtHora.setToolTipText("Formato: HH:mm:ss");
        formulario.add(txtHora);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        formulario.add(btnGuardar);
        formulario.add(btnActualizar);
        formulario.add(btnEliminar);
        formulario.add(btnLimpiar);
        superior.add(formulario);

        JPanel filtros = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );
        filtros.setBorder(
            BorderFactory.createTitledBorder(
                "Filtros de entregas"
            )
        );
        filtros.add(new JLabel("Pedido:"));
        cmbFiltroPedido = new JComboBox<>();
        filtros.add(cmbFiltroPedido);
        filtros.add(new JLabel("Repartidor:"));
        cmbFiltroRepartidor = new JComboBox<>();
        filtros.add(cmbFiltroRepartidor);
        JButton btnFiltrar = new JButton("Filtrar");
        filtros.add(btnFiltrar);
        JButton btnTodos = new JButton("Mostrar todos");
        filtros.add(btnTodos);
        superior.add(filtros);
        add(superior, BorderLayout.NORTH);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        btnTodos.addActionListener(e -> {
            cmbFiltroPedido.setSelectedIndex(0);
            cmbFiltroRepartidor.setSelectedIndex(0);
            cargarTabla();
        });
    }

    private void crearTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[]{
                "ID",
                "Pedido",
                "Repartidor",
                "Fecha",
                "Hora"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(
                int row,
                int column) {
            return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.getSelectionModel()
            .addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int fila = tabla.getSelectedRow();
                    if (fila >= 0) {
                        seleccionarEntrega(fila);
                    }
                }
            });
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void cargarCombos() {
        cmbPedido.removeAllItems();
        cmbRepartidor.removeAllItems();
        cmbFiltroPedido.removeAllItems();
        cmbFiltroRepartidor.removeAllItems();
        cmbFiltroPedido.addItem(null);
        cmbFiltroRepartidor.addItem(null);
        for (Pedido pedido : pedidoDAO.readAll()) {
            cmbPedido.addItem(pedido);
            cmbFiltroPedido.addItem(pedido);
        }
        for (Repartidor repartidor :
            repartidorDAO.readAll()) {
            cmbRepartidor.addItem(repartidor);
            cmbFiltroRepartidor.addItem(repartidor);
        }
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Entrega entrega :
            entregaDAO.readAll()) {
            agregarFila(entrega);
        }
    }

    private void agregarFila(Entrega entrega) {
        String pedidoTexto =
            buscarPedido(entrega.getIdPedido());
        String repartidorTexto =
            buscarRepartidor(
                entrega.getIdRepartidor()
            );
        modeloTabla.addRow(new Object[]{
            entrega.getId(),
            pedidoTexto,
            repartidorTexto,
            entrega.getFecha(),
            entrega.getHora()
        });
    }

    private String buscarPedido(int id) {
        for (int i = 0;
             i < cmbPedido.getItemCount();
             i++) {
            Pedido pedido =
                cmbPedido.getItemAt(i);
            if (pedido.getId() == id) {
                return pedido.toString();
            }
        }
        return String.valueOf(id);
    }

    private String buscarRepartidor(int id) {
        for (int i = 0;
             i < cmbRepartidor.getItemCount();
             i++) {
            Repartidor repartidor =
                cmbRepartidor.getItemAt(i);
            if (repartidor.getId() == id) {
                return repartidor.toString();
            }
        }
        return String.valueOf(id);
    }

    private void seleccionarEntrega(int fila) {
        idSeleccionado =
            Integer.parseInt(
                modeloTabla
                    .getValueAt(fila, 0)
                    .toString()
            );
        String pedidoTexto =
            modeloTabla
                .getValueAt(fila, 1)
                .toString();
        String repartidorTexto =
            modeloTabla
                .getValueAt(fila, 2)
                .toString();
        for (int i = 0;
             i < cmbPedido.getItemCount();
             i++) {
            Pedido pedido =
                cmbPedido.getItemAt(i);
            if (pedido.toString()
                .equals(pedidoTexto)) {
                cmbPedido.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0;
             i < cmbRepartidor.getItemCount();
             i++) {
            Repartidor repartidor =
                cmbRepartidor.getItemAt(i);
            if (repartidor.toString()
                .equals(repartidorTexto)) {
                cmbRepartidor.setSelectedIndex(i);
                break;
            }
        }
        txtFecha.setText(
            modeloTabla
                .getValueAt(fila, 3)
                .toString()
        );
        txtHora.setText(
            modeloTabla
                .getValueAt(fila, 4)
                .toString()
        );
    }

    private boolean validar() {
        if (cmbPedido.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                this,
                "Debes seleccionar un pedido."
            );
            return false;
        }
        if (cmbRepartidor.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                this,
                "Debes seleccionar un repartidor."
            );
            return false;
        }
        if (txtFecha.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "La fecha es obligatoria."
            );
            txtFecha.requestFocus();
            return false;
        }
        if (txtHora.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "La hora es obligatoria."
            );
            txtHora.requestFocus();
            return false;
        }
        try {
            Date.valueOf(txtFecha.getText().trim());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                this,
                "La fecha debe tener el formato yyyy-MM-dd."
            );
            return false;
        }
        try {
            Time.valueOf(txtHora.getText().trim());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                this,
                "La hora debe tener el formato HH:mm:ss."
            );
            return false;
        }
        return true;
    }

    private void guardar() {
        if (!validar()) {
            return;
        }
        Pedido pedido =
            (Pedido) cmbPedido.getSelectedItem();
        Repartidor repartidor =
            (Repartidor)
                cmbRepartidor.getSelectedItem();
        Entrega entrega = new Entrega(
            pedido.getId(),
            repartidor.getId(),
            Date.valueOf(txtFecha.getText().trim()),
            Time.valueOf(txtHora.getText().trim())
        );
        if (entregaDAO.create(entrega)) {
            JOptionPane.showMessageDialog(
                this,
                "Entrega registrada correctamente."
            );
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo registrar la entrega.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona una entrega."
            );
            return;
        }
        if (!validar()) {
            return;
        }
        Pedido pedido =
            (Pedido) cmbPedido.getSelectedItem();
        Repartidor repartidor =
            (Repartidor)
                cmbRepartidor.getSelectedItem();
        Entrega entrega = new Entrega(
            idSeleccionado,
            pedido.getId(),
            repartidor.getId(),
            Date.valueOf(txtFecha.getText().trim()),
            Time.valueOf(txtHora.getText().trim())
        );
        if (entregaDAO.update(entrega)) {
            JOptionPane.showMessageDialog(
                this,
                "Entrega actualizada correctamente."
            );
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo actualizar la entrega.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona una entrega."
            );
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Deseas eliminar la entrega seleccionada?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
        );
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        if (entregaDAO.delete(idSeleccionado)) {
            JOptionPane.showMessageDialog(
                this,
                "Entrega eliminada correctamente."
            );
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo eliminar la entrega.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void aplicarFiltros() {
        Pedido pedido =
            (Pedido)
                cmbFiltroPedido.getSelectedItem();
        Repartidor repartidor =
            (Repartidor)
                cmbFiltroRepartidor.getSelectedItem();
        modeloTabla.setRowCount(0);
        for (Entrega entrega :
            entregaDAO.readAll()) {
            boolean coincidePedido =
                pedido == null ||
                    entrega.getIdPedido()
                        == pedido.getId();
            boolean coincideRepartidor =
                repartidor == null ||
                    entrega.getIdRepartidor()
                        == repartidor.getId();
            if (coincidePedido && coincideRepartidor) {
                agregarFila(entrega);
            }
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        if (cmbPedido.getItemCount() > 0) {
            cmbPedido.setSelectedIndex(0);
        }
        if (cmbRepartidor.getItemCount() > 0) {
            cmbRepartidor.setSelectedIndex(0);
        }
        txtFecha.setText("");
        txtHora.setText("");
        tabla.clearSelection();
    }
}
