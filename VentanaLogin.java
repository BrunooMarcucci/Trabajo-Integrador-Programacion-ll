package hotel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VentanaLogin extends JFrame {

    private JTextField txtNombre;
    private JTextField txtDni;
    private JButton btnIngresar;

    public VentanaLogin() {
        setTitle("Ingreso de empleado");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Nombre del empleado:"));
        txtNombre = new JTextField();
        panel.add(txtNombre);

        panel.add(new JLabel("DNI:"));
        txtDni = new JTextField();
        panel.add(txtDni);

        btnIngresar = new JButton("Iniciar sesion");
        panel.add(new JLabel(""));
        panel.add(btnIngresar);

        add(panel, BorderLayout.CENTER);

        btnIngresar.addActionListener(e -> iniciarSesion());

        setVisible(true);
    }

    private void iniciarSesion() {
        try {
            String nombre = txtNombre.getText().trim();
            String dni = txtDni.getText().trim();

            validarNombre(nombre);
            validarDni(dni);

            // Si pasa las validaciones, se guarda el empleado o se continúa
            Empleado empleado = new Recepcionista(0, nombre, dni);
            System.out.println("Inicio de sesion correcto: " + empleado.getNombre());

            // Abrir la ventana principal
            new VentanaPrincipal(empleado).setVisible(true);
            this.dispose();

        } catch (NombreInvalidoException | DniInvalidoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de validacion", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage());
        }
    }

    private void validarNombre(String nombre) throws NombreInvalidoException {
        if (nombre.isEmpty()) {
            throw new NombreInvalidoException("El nombre no puede estar vacio.");
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new NombreInvalidoException("El nombre solo puede contener letras.");
        }
    }

    private void validarDni(String dni) throws DniInvalidoException {
        if (dni.isEmpty()) {
            throw new DniInvalidoException("El DNI no puede estar vacio.");
        }
        if (!dni.matches("\\d+")) {
            throw new DniInvalidoException("El DNI solo puede contener numeros.");
        }
        if (dni.length() < 7 || dni.length() > 10) {
            throw new DniInvalidoException("El DNI debe tener entre 7 y 10 digitos.");
        }
    }
}

