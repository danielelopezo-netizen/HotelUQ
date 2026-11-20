package co.edu.uniquindio.hotel.controller;

import co.edu.uniquindio.hotel.MainApp;
import co.edu.uniquindio.hotel.exceptions.AutenticacionException;
import co.edu.uniquindio.hotel.exceptions.HotelException;
import co.edu.uniquindio.hotel.model.Administrador;
import co.edu.uniquindio.hotel.model.Cliente;
import co.edu.uniquindio.hotel.model.Persona;
import co.edu.uniquindio.hotel.patterns.structural.HotelFachada;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private TextField txtEmailLogin;
    @FXML private PasswordField txtPasswordLogin;
    @FXML private Label lblMensajeLogin;

    @FXML private VBox boxLogin;
    @FXML private VBox boxRegistro;

    @FXML private TextField txtRegIdentificacion;
    @FXML private TextField txtRegNombre;
    @FXML private TextField txtRegEmail;
    @FXML private TextField txtRegTelefono;
    @FXML private PasswordField txtRegPassword;
    @FXML private Label lblMensajeRegistro;

    private final HotelFachada fachada = HotelFachada.getInstance();

    @FXML
    public void initialize() {
        mostrarPanelLogin();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = txtEmailLogin.getText();
        String pass = txtPasswordLogin.getText();

        try {
            Persona persona = fachada.autenticar(email, pass);
            if (persona instanceof Administrador) {
                MainApp.cargarVista("/co/edu/uniquindio/hotel/views/AdminDashboardView.fxml", "HotelUQ - Panel de Administración");
            } else if (persona instanceof Cliente) {
                MainApp.cargarVista("/co/edu/uniquindio/hotel/views/ClienteDashboardView.fxml", "HotelUQ - Portal de Huéspedes");
            }
        } catch (AutenticacionException e) {
            mostrarErrorLogin(e.getMessage());
        } catch (Exception e) {
            mostrarErrorLogin("Error al iniciar sesión: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegistro(ActionEvent event) {
        String id = txtRegIdentificacion.getText();
        String nombre = txtRegNombre.getText();
        String email = txtRegEmail.getText();
        String tel = txtRegTelefono.getText();
        String pass = txtRegPassword.getText();

        try {
            Cliente nuevo = fachada.registrarCliente(id, nombre, email, tel, pass);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Registro Exitoso", 
                    "Bienvenido a HotelUQ, " + nuevo.getNombre() + ".\nSu tarjeta virtual (" 
                    + nuevo.getTarjetaVirtual().getNumeroTarjeta() + ") ha sido creada.");
            txtEmailLogin.setText(email);
            txtPasswordLogin.setText(pass);
            mostrarPanelLogin();
        } catch (HotelException e) {
            lblMensajeRegistro.setText(e.getMessage());
        }
    }

    @FXML
    private void handleDemoAdmin() {
        txtEmailLogin.setText("admin@hoteluq.com");
        txtPasswordLogin.setText("admin123");
        handleLogin(null);
    }

    @FXML
    private void handleDemoCliente1() {
        txtEmailLogin.setText("carlos@gmail.com");
        txtPasswordLogin.setText("123");
        handleLogin(null);
    }

    @FXML
    private void handleDemoCliente2() {
        txtEmailLogin.setText("laura@gmail.com");
        txtPasswordLogin.setText("123");
        handleLogin(null);
    }

    @FXML
    private void mostrarPanelRegistro() {
        boxLogin.setVisible(false);
        boxLogin.setManaged(false);
        boxRegistro.setVisible(true);
        boxRegistro.setManaged(true);
        lblMensajeLogin.setText("");
    }

    @FXML
    private void mostrarPanelLogin() {
        boxRegistro.setVisible(false);
        boxRegistro.setManaged(false);
        boxLogin.setVisible(true);
        boxLogin.setManaged(true);
        lblMensajeRegistro.setText("");
    }

    private void mostrarErrorLogin(String msg) {
        lblMensajeLogin.setText(msg);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

