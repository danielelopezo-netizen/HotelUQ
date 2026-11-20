package co.edu.uniquindio.hotel.model;

public class Administrador extends Persona {
    private String cargo;

    public Administrador() {
        super();
    }

    public Administrador(String identificacion, String nombre, String email, String telefono, String password, String cargo) {
        super(identificacion, nombre, email, telefono, password);
        this.cargo = cargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    @Override
    public String toString() {
        return nombre + " [" + cargo + "]";
    }
}
