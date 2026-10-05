package speedfast.vista;

import speedfast.dao.RepartidorDAO;
import speedfast.modelo.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RepartidorPanel extends JPanel {

    private JTextField txtNombre;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private RepartidorDAO dao;
    private int idSeleccionado = -1;

    public RepartidorPanel() {
        dao = new RepartidorDAO();
        setLayout(new BorderLayout(10, 10));
        crearFormulario();
        crearTabla();
        cargarTabla();
    }

    private void crearFormulario() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(20);
        panel.add(txtNombre);

        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        panel.add(btnGuardar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnLimpiar);

        add(panel, BorderLayout.NORTH);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
    }

    private void crearTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    idSeleccionado =
                        Integer.parseInt(
                            modeloTabla.getValueAt(fila, 0)
                                .toString()
                            );
                    txtNombre.setText(
                        modeloTabla.getValueAt(fila, 1).toString()
                    );
                }
            }
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        for (Repartidor repartidor : dao.readAll()) {
            modeloTabla.addRow(new Object[]{
                repartidor.getId(),
                repartidor.getNombre()
            });
        }
    }

    private boolean validar() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "El nombre es obligatorio.",
                "Validación",
                JOptionPane.WARNING_MESSAGE
            );
            txtNombre.requestFocus();
            return false;
        }
        return true;
    }

    private void guardar() {
        if (!validar()) {
            return;
        }
        Repartidor repartidor =
            new Repartidor(txtNombre.getText().trim());
        if (dao.create(repartidor)) {
            JOptionPane.showMessageDialog(
                this,
                "Repartidor registrado correctamente."
            );
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo registrar el repartidor.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona un repartidor."
            );
            return;
        }
        if (!validar()) {
            return;
        }
        Repartidor repartidor =
            new Repartidor(
                idSeleccionado,
                txtNombre.getText().trim()
                );
        if (dao.update(repartidor)) {
            JOptionPane.showMessageDialog(
                this,
                "Repartidor actualizado correctamente."
            );
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo actualizar el repartidor.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecciona un repartidor."
            );
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Deseas eliminar el repartidor seleccionado?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        );
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        if (dao.delete(idSeleccionado)) {
            JOptionPane.showMessageDialog(
                this,
                "Repartidor eliminado correctamente."
            );
            limpiar();
            cargarTabla();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "No se pudo eliminar el repartidor.\n"
                        + "Puede que tenga entregas asociadas.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        txtNombre.setText("");
        tabla.clearSelection();
    }
}