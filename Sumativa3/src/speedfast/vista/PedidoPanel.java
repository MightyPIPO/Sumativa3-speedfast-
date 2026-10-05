package speedfast.vista;

import speedfast.dao.PedidoDAO;
import speedfast.modelo.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PedidoPanel extends JPanel {

    private JTextField txtDireccion;
    private JComboBox<String> cmbTipo;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbFiltroTipo;
    private JComboBox<String> cmbFiltroEstado;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private PedidoDAO dao;
    private int idSeleccionado = -1;

    public PedidoPanel() {
        dao = new PedidoDAO();
        setLayout(new BorderLayout(10, 10));
        crearFormulario();
        crearTabla();
        cargarTabla();
    }

    private void crearFormulario() {
        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(
            superior,
            BoxLayout.Y_AXIS
        ));
        JPanel formulario = new JPanel(
            new FlowLayout(FlowLayout.LEFT)
        );
        formulario.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField(18);
        formulario.add(txtDireccion);
        formulario.add(new JLabel("Tipo:"));
        cmbTipo = new JComboBox<>(
            new String[]{
                "COMIDA",
                "ENCOMIENDA",
                "EXPRESS"
            }
        );
        formulario.add(cmbTipo);
        formulario.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(
            new String[]{
                "PENDIENTE",
                "EN_REPARTO",
                "ENTREGADO"
            }
        );
        formulario.add(cmbEstado);

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
            BorderFactory.createTitledBorder("Filtros")
        );
        filtros.add(new JLabel("Estado:"));
        cmbFiltroEstado = new JComboBox<>(
            new String[]{
                "TODOS",
                "PENDIENTE",
                "EN_REPARTO",
                "ENTREGADO"
            }
        );
        filtros.add(cmbFiltroEstado);
        filtros.add(new JLabel("Tipo:"));
        cmbFiltroTipo = new JComboBox<>(
            new String[]{
                "TODOS",
                "COMIDA",
                "ENCOMIENDA",
                "EXPRESS"
            }
        );
        filtros.add(cmbFiltroTipo);

        JButton btnFiltrar = new JButton("Filtrar");
        filtros.add(btnFiltrar);

        JButton btnMostrarTodos = new JButton("Mostrar todos");
        filtros.add(btnMostrarTodos);

        superior.add(filtros);
        add(superior, BorderLayout.NORTH);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnFiltrar.addActionListener(e -> aplicarFiltros());
        btnMostrarTodos.addActionListener(e -> {
            cmbFiltroEstado.setSelectedIndex(0);
            cmbFiltroTipo.setSelectedIndex(0);
            cargarTabla();
        });
    }

    private void crearTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[]{
                "ID",
                "Dirección",
                "Tipo",
                "Estado"
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
                        idSeleccionado =
                            Integer.parseInt(
                                modeloTabla.getValueAt(
                                        fila, 0
                                ).toString()
                            );
                        txtDireccion.setText(
                            modeloTabla.getValueAt(
                                fila, 1
                            ).toString()
                        );
                        cmbTipo.setSelectedItem(
                            modeloTabla.getValueAt(
                                fila, 2
                            ).toString()
                        );
                        cmbEstado.setSelectedItem(
                            modeloTabla.getValueAt(
                                fila, 3
                            ).toString()
                        );
                    }
                }
            });
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Pedido pedido : dao.readAll()) {
            agregarFila(pedido);
        }
    }

    private void aplicarFiltros() {
        String estado =
            cmbFiltroEstado.getSelectedItem().toString();
        String tipo =
            cmbFiltroTipo.getSelectedItem().toString();
        if (estado.equals("TODOS")) {
            estado = null;
        }
        if (tipo.equals("TODOS")) {
            tipo = null;
        }
        modeloTabla.setRowCount(0);
        for (Pedido pedido : dao.readAll(estado, tipo)) {
            agregarFila(pedido);
        }
    }

    private void agregarFila(Pedido pedido) {
        modeloTabla.addRow(new Object[]{
            pedido.getId(),
            pedido.getDireccion(),
            pedido.getTipo(),
            pedido.getEstado()
        });
    }

    private boolean validar() {
        if (txtDireccion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "La dirección es obligatoria."
            );
            txtDireccion.requestFocus();
            return false;
        }
        return true;
    }

    private void guardar() {
        if (!validar()) {
            return;
        }
        Pedido pedido = new Pedido(
            txtDireccion.getText().trim(),
            cmbTipo.getSelectedItem().toString(),
            cmbEstado.getSelectedItem().toString()
        );
        if (dao.create(pedido)) {
            JOptionPane.showMessageDialog(
                this,
                "Pedido registrado correctamente."
            );
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo registrar el pedido.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona un pedido."
            );
            return;
        }
        if (!validar()) {
            return;
        }
        Pedido pedido = new Pedido(
            idSeleccionado,
            txtDireccion.getText().trim(),
            cmbTipo.getSelectedItem().toString(),
            cmbEstado.getSelectedItem().toString()
        );
        if (dao.update(pedido)) {
            JOptionPane.showMessageDialog(
                this,
                "Pedido actualizado correctamente."
            );
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo actualizar el pedido.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona un pedido."
            );
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Deseas eliminar el pedido seleccionado?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        );
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        if (dao.delete(idSeleccionado)) {
            JOptionPane.showMessageDialog(
                this,
                "Pedido eliminado correctamente."
            );
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo eliminar el pedido.\n"
                        + "Puede tener una entrega asociada.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        txtDireccion.setText("");
        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        tabla.clearSelection();
    }
}
