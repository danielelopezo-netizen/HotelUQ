package co.edu.uniquindio.hotel.patterns.creational;

import co.edu.uniquindio.hotel.model.Administrador;
import co.edu.uniquindio.hotel.model.Cliente;
import co.edu.uniquindio.hotel.model.Persona;

public class SesionManager {
    private static SesionManager instance;
    private Persona usuarioAutenticado;

    private SesionManager() {
    }

    public static synchronized SesionManager getInstance() {
        if (instance == null) {
            instance = new SesionManager();
        }
        return instance;
    }

    public synchronized void iniciarSesion(Persona persona) {
        this.usuarioAutenticado = persona;
    }

    public synchronized void cerrarSesion() {
        this.usuarioAutenticado = null;
    }

    public Persona getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    public boolean haySesionActiva() {
        return usuarioAutenticado != null;
    }

    public boolean esCliente() {
        return usuarioAutenticado instanceof Cliente;
    }

    public boolean esAdministrador() {
        return usuarioAutenticado instanceof Administrador;
    }

    public Cliente getClienteActual() {
        return esCliente() ? (Cliente) usuarioAutenticado : null;
    }

    public Administrador getAdministradorActual() {
        return esAdministrador() ? (Administrador) usuarioAutenticado : null;
    }
}

