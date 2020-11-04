package Negocio;

public class Postulantes {
    private String codigoAgrupacion;
    private String nombreAgrupacion;
    private String codigoLista;
    private String nombreLista;

    public Postulantes(String codigoAgrupacion, String nombreAgrupacion, String codigoLista, String nombreLista)
    {
        this.codigoAgrupacion = codigoAgrupacion;
        this.nombreAgrupacion = nombreAgrupacion;
        this.codigoLista = codigoLista;
        this.nombreLista = nombreLista;
    }

    public String getCodigoAgrupacion() {
        return codigoAgrupacion;
    }

    public void setCodigoAgrupacion(String codigoAgrupacion) {
        this.codigoAgrupacion = codigoAgrupacion;
    }

    public String getNombreAgrupacion() {
        return nombreAgrupacion;
    }

    public void setNombreAgrupacion(String nombreAgrupacion) {
        this.nombreAgrupacion = nombreAgrupacion;
    }

    public String getCodigoLista() {
        return codigoLista;
    }

    public void setCodigoLista(String codigoLista) {
        this.codigoLista = codigoLista;
    }

    public String getNombreLista() {
        return nombreLista;
    }

    public void setNombreLista(String nombreLista) {
        this.nombreLista = nombreLista;
    }
}
