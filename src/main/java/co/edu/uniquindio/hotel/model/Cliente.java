package co.edu.uniquindio.hotel.model;

public class Cliente extends Persona {
    private TarjetaVirtual tarjetaVirtual;
    private CuentaPuntos cuentaPuntos;

    public Cliente() {
        super();
        this.tarjetaVirtual = new TarjetaVirtual();
        this.cuentaPuntos = new CuentaPuntos();
    }

    public Cliente(String identificacion, String nombre, String email, String telefono, String password) {
        super(identificacion, nombre, email, telefono, password);
        this.tarjetaVirtual = new TarjetaVirtual("TV-" + identificacion);
        this.cuentaPuntos = new CuentaPuntos(identificacion);
    }

    public TarjetaVirtual getTarjetaVirtual() {
        return tarjetaVirtual;
    }

    public void setTarjetaVirtual(TarjetaVirtual tarjetaVirtual) {
        this.tarjetaVirtual = tarjetaVirtual;
    }

    public CuentaPuntos getCuentaPuntos() {
        return cuentaPuntos;
    }

    public void setCuentaPuntos(CuentaPuntos cuentaPuntos) {
        this.cuentaPuntos = cuentaPuntos;
    }

    @Override
    public String toString() {
        return nombre + " (" + identificacion + ")";
    }
}

