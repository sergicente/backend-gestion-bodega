package cava.model.dto;

public class LoginResponse {
    private String mensaje;
    private String rol;
    private String nombre;
    private String token;

    public LoginResponse(String mensaje) {
        this.mensaje = mensaje;
    }
    public String getMensaje() {
        return mensaje;
    }
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
