package hotel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        setSize(950, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Si quieres borrar hotel.db antes de crear tablas, hacelo en Main antes de instanciar esto.
        DBIniciador.crearTablas();
        Habitacion.inicializarHabitaciones();

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

        modeloTabla = new DefaultTableModel();
        tablaReservas = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaReservas);

        add(panelIzquierdo, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);

        // Eventos
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

            validarNombre(nombre);
            validarDni(dni);

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

            // Buscar habitación disponible del tipo en BD
            Habitacion habitacion = Habitacion.obtenerDisponible(tipo);
            if (habitacion == null) {
                JOptionPane.showMessageDialog(this, "No hay habitaciones disponibles del tipo " + tipo + ".");
                return;
            }

            long diferenciaMillis = fechaFin.getTime() - fechaInicio.getTime();
            int dias = (int) (diferenciaMillis / (1000 * 60 * 60 * 24));
            if (dias <= 0) dias = 1;

            Huesped huesped = vip ? new HuespedVIP(0, nombre, dni) : new Huesped(0, nombre, dni);
            huesped.guardar(); // ahora setea su id

            // calcular costo total (habitacion tiene id real y precioBase)
            double costoTotal;
            if (vip) costoTotal = (habitacion.calcularPrecio() * dias) * 0.7;
            else costoTotal = habitacion.calcularPrecio() * dias;

            Reserva reserva = new Reserva(0, fechaInicio, fechaFin, huesped, habitacion, costoTotal);
            reserva.guardar();
            empleadoActivo.agregarReserva(reserva);

            JOptionPane.showMessageDialog(this,
                    "Reserva agregada correctamente.\n\n" +
                            "Huésped: " + nombre +
                            "\nHabitación: " + tipo + " #" + habitacion.getNumero() +
                            "\nDías: " + dias +
                            "\nCosto total: $" + costoTotal);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al agregar reserva: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listarReservas() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);

        String sql = """
        SELECT 
            r.id AS ID,
            h.nombre AS Huesped,
            h.dni AS DNI,
            hab.tipo AS Habitacion,
            hab.numero AS Numero,
            r.fechaInicio AS FechaInicio,
            r.fechaFin AS FechaFin,
            r.costoTotal AS CostoTotal
        FROM Reserva r
        LEFT JOIN Huesped h ON r.idHuesped = h.id
        LEFT JOIN Habitacion hab ON r.idHabitacion = hab.id
        ORDER BY r.id;
        """;

        try (Connection conn = ConexionSQLite.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            for (int i = 1; i <= colCount; i++) modeloTabla.addColumn(meta.getColumnName(i));

            boolean hayDatos = false;
            while (rs.next()) {
                Object[] fila = new Object[colCount];
                for (int i = 1; i <= colCount; i++) fila[i - 1] = rs.getObject(i);
                modeloTabla.addRow(fila);
                hayDatos = true;
            }

            if (!hayDatos) JOptionPane.showMessageDialog(this, "No hay reservas registradas.");

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
        int numeroHab = (int) tablaReservas.getValueAt(filaSeleccionada, 4); // columna Numero

        // liberar habitacion por numero
        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM Habitacion WHERE numero = ?")) {
            ps.setInt(1, numeroHab);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idHab = rs.getInt("id");
                    PreparedStatement up = conn.prepareStatement("UPDATE Habitacion SET disponible = 1 WHERE id = ?");
                    up.setInt(1, idHab);
                    up.executeUpdate();
                    up.close();
                }
            }
        } catch (Exception ex) {
            System.err.println("Error liberando habitacion al eliminar: " + ex.getMessage());
        }

        String sql = "DELETE FROM Reserva WHERE id = ?";

        try (Connection conn = ConexionSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

            int idHuesped = -1;
            try (Connection conn = ConexionSQLite.conectar();
                 PreparedStatement ps = conn.prepareStatement("SELECT id FROM Huesped WHERE dni = ?")) {
                ps.setString(1, dni);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) idHuesped = rs.getInt("id");
                }
            }

            if (idHuesped == -1) {
                JOptionPane.showMessageDialog(this, "No se encontró un huesped con ese DNI.");
                return;
            }

            try (Connection conn = ConexionSQLite.conectar();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE Reserva SET fechaInicio = ?, fechaFin = ? WHERE idHuesped = ?")) {

                ps.setString(1, fechaInicioStr);
                ps.setString(2, fechaFinStr);
                ps.setInt(3, idHuesped);

                int filas = ps.executeUpdate();
                if (filas > 0) JOptionPane.showMessageDialog(this, "Reserva modificada correctamente.");
                else JOptionPane.showMessageDialog(this, "No se encontró una reserva para ese huesped.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al modificar reserva: " + e.getMessage());
        }
    }

    // Validaciones
    private void validarNombre(String nombre) throws Exception {
        if (nombre.isEmpty()) throw new Exception("El nombre no puede estar vacío.");
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) throw new Exception("El nombre solo puede contener letras.");
    }

    private void validarDni(String dni) throws Exception {
        if (dni.isEmpty()) throw new Exception("El DNI no puede estar vacío.");
        if (!dni.matches("\\d+")) throw new Exception("El DNI solo puede contener números.");
        if (dni.length() < 7 || dni.length() > 10) throw new Exception("El DNI debe tener entre 7 y 10 dígitos.");
    }
}

