package hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VentanaPrincipal extends JFrame {

    private JTextField txtNombreHuesped, txtDni, txtInicio, txtFin;
    private JCheckBox chkVip;
    private JComboBox<String> cmbHabitacion;
    private JButton btnAgregar, btnEliminar, btnListar, btnLimpiar, btnModificar;
    private JTable tablaReservas;
    private DefaultTableModel modeloTabla;
    private Empleado empleadoActivo;

    public VentanaPrincipal(Empleado empleado) {
        this.empleadoActivo = empleado;

        setTitle("Sistema de gestion hotelera - Empleado: " + empleado.getNombre());
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelIzquierdo = new JPanel(new GridLayout(9, 2, 5, 5));
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelIzquierdo.add(new JLabel("Nombre huesped:"));
        txtNombreHuesped = new JTextField();
        panelIzquierdo.add(txtNombreHuesped);

        panelIzquierdo.add(new JLabel("DNI huesped:"));
        txtDni = new JTextField();
        panelIzquierdo.add(txtDni);

        panelIzquierdo.add(new JLabel("Tipo de habitacion:"));
        cmbHabitacion = new JComboBox<>(new String[]{"Simple", "Doble", "Suite", "SuitePresidencial"});
        panelIzquierdo.add(cmbHabitacion);

        panelIzquierdo.add(new JLabel("Fecha inicio (YYYY-MM-DD):"));
        txtInicio = new JTextField();
        panelIzquierdo.add(txtInicio);

        panelIzquierdo.add(new JLabel("Fecha fin (YYYY-MM-DD):"));
        txtFin = new JTextField();
        panelIzquierdo.add(txtFin);

        chkVip = new JCheckBox("Es huesped VIP (30% desc)");
        panelIzquierdo.add(chkVip);
        panelIzquierdo.add(new JLabel(""));

        btnAgregar = new JButton("Agregar reserva");
        btnEliminar = new JButton("Eliminar reserva");
        btnListar = new JButton("Listar reservas");
        btnLimpiar = new JButton("Limpiar campos");
        btnModificar = new JButton("Modificar reserva");

        panelIzquierdo.add(btnAgregar);
        panelIzquierdo.add(btnEliminar);
        panelIzquierdo.add(btnListar);
        panelIzquierdo.add(btnLimpiar);
        panelIzquierdo.add(btnModificar);

        // Tabla de la derecha
        modeloTabla = new DefaultTableModel();
        tablaReservas = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaReservas);

        add(panelIzquierdo, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        // Eventos de botones
        btnAgregar.addActionListener(e -> agregarReserva());
        btnListar.addActionListener(e -> listarReservas());
        btnEliminar.addActionListener(e -> eliminarReserva());
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnModificar.addActionListener(e -> modificarReserva());

        setVisible(true);
    }

    private void agregarReserva() {
        try {
            String nombre = txtNombreHuesped.getText().trim();
            String dni = txtDni.getText().trim();
            String tipo = (String) cmbHabitacion.getSelectedItem();
            String fechaInicioStr = txtInicio.getText().trim();
            String fechaFinStr = txtFin.getText().trim();
            boolean vip = chkVip.isSelected();

            try {
                validarNombre(nombre);
                validarDni(dni);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error de validación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (nombre.isEmpty() || dni.isEmpty() || fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos.");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicio = sdf.parse(fechaInicioStr);
            Date fechaFin = sdf.parse(fechaFinStr);

            if (fechaFin.before(fechaInicio)) {
                JOptionPane.showMessageDialog(this, "La fecha de fin no puede ser anterior a la de inicio.");
                return;
            }

            Huesped huesped = vip ? new HuespedVIP(0, nombre, dni) : new Huesped(0, nombre, dni);
            huesped.guardar();

            if (huesped.getId() == 0) {
                try (Connection conn = ConexionSQLite.conectar(); PreparedStatement ps = conn.prepareStatement("SELECT id FROM Huesped WHERE dni = ?")) {
                    ps.setString(1, dni);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            huesped.setId(rs.getInt("id"));
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error al obtener ID de huésped: " + ex.getMessage());
                }
            }

            double precioBase = switch (tipo) {
                case "Simple" ->
                    2000;
                case "Doble" ->
                    3500;
                case "Suite" ->
                    5000;
                case "SuitePresidencial" ->
                    8000;
                default ->
                    0;
            };

            if (vip) {
                precioBase *= 0.7;
            }

            Habitacion habitacion;

            switch (tipo) {
                case "Simple" ->
                    habitacion = new Simple(0, 101, precioBase, true);
                case "Doble" ->
                    habitacion = new Doble(0, 101, precioBase, true, 1);
                case "Suite" ->
                    habitacion = new Suite(0, 101, precioBase, true, true);
                case "SuitePresidencial" ->
                    habitacion = new SuitePresidencial(0, 101, precioBase, true, true, true);
                default ->
                    throw new IllegalArgumentException("Tipo de habitacion invalido: " + tipo);
            }

            habitacion.guardar(tipo);

            Reserva reserva = new Reserva(0, fechaInicio, fechaFin, huesped, habitacion);
            reserva.guardar();

            JOptionPane.showMessageDialog(this, "Reserva agregada correctamente.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al agregar reserva: " + e.getMessage());
        }
    }

    private void listarReservas() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);

        String sql = """
        SELECT 
            r.id AS ID,
            r.fechaInicio AS FechaInicio,
            r.fechaFin AS FechaFin,
            r.costoTotal AS CostoTotal,
            h.nombre AS Huesped,
            hab.tipo AS Habitacion
        FROM Reserva r
        JOIN Huesped h ON r.idHuesped = h.id
        JOIN Habitacion hab ON r.idHabitacion = hab.id
        ORDER BY r.id;
        """;

        try (Connection conn = ConexionSQLite.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            for (int i = 1; i <= colCount; i++) {
                modeloTabla.addColumn(meta.getColumnName(i));
            }

            boolean hayDatos = false;
            while (rs.next()) {
                Object[] fila = new Object[colCount];
                for (int i = 1; i <= colCount; i++) {
                    fila[i - 1] = rs.getObject(i);
                }
                modeloTabla.addRow(fila);
                hayDatos = true;
            }

            if (!hayDatos) {
                JOptionPane.showMessageDialog(this, "No hay reservas registradas.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al listar reservas: " + e.getMessage());
        }
    }

    private void eliminarReserva() {
        int filaSeleccionada = tablaReservas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva para eliminar.");
            return;
        }

        int id = (int) tablaReservas.getValueAt(filaSeleccionada, 0);

        String sql = "DELETE FROM Reserva WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Reserva eliminada correctamente.");
            listarReservas();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar reserva: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNombreHuesped.setText("");
        txtDni.setText("");
        txtInicio.setText("");
        txtFin.setText("");
        cmbHabitacion.setSelectedIndex(0);
        chkVip.setSelected(false);

        JOptionPane.showMessageDialog(this, "Campos limpiados");
    }

    private void modificarReserva() {
        try {
            String dni = txtDni.getText().trim();
            String fechaInicioStr = txtInicio.getText().trim();
            String fechaFinStr = txtFin.getText().trim();

            if (dni.isEmpty() || fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar DNI y nuevas fechas.");
                return;
            }

            // Confirmar que existe un huésped con ese DNI
            int idHuesped = -1;
            try (Connection conn = ConexionSQLite.conectar(); PreparedStatement ps = conn.prepareStatement("SELECT id FROM Huesped WHERE dni = ?")) {
                ps.setString(1, dni);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idHuesped = rs.getInt("id");
                    }
                }
            }

            if (idHuesped == -1) {
                JOptionPane.showMessageDialog(this, "No se encontró un huesped con ese DNI.");
                return;
            }

            // Actualizar fechas de reserva
            try (Connection conn = ConexionSQLite.conectar(); PreparedStatement ps = conn.prepareStatement(
                    "UPDATE Reserva SET fechaInicio = ?, fechaFin = ? WHERE idHuesped = ?")) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                ps.setString(1, fechaInicioStr);
                ps.setString(2, fechaFinStr);
                ps.setInt(3, idHuesped);

                int filas = ps.executeUpdate();

                if (filas > 0) {
                    JOptionPane.showMessageDialog(this, "Reserva modificada correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró una reserva para ese huesped.");
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al modificar reserva: " + e.getMessage());
        }
    }

    //Las validaciones 
    private void validarNombre(String nombre) throws Exception {
        if (nombre.isEmpty()) {
            throw new Exception("El nombre no puede estar vacío.");
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new Exception("El nombre solo puede contener letras.");
        }
    }

    private void validarDni(String dni) throws Exception {
        if (dni.isEmpty()) {
            throw new Exception("El DNI no puede estar vacío.");
        }
        if (!dni.matches("\\d+")) {
            throw new Exception("El DNI solo puede contener números.");
        }
        if (dni.length() < 7 || dni.length() > 10) {
            throw new Exception("El DNI debe tener entre 7 y 10 dígitos.");
        }
    }
}
