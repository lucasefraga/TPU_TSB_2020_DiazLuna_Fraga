package Negocio;

import Importadores.IPostulaciones;

import java.util.ArrayList;

public class Resultados {

    private Integer codigo;
    private String nombre;
    private Integer votos;
    private ArrayList postulantes;

    public Resultados(int codigoAgrupacion)
    {
        IPostulaciones ipo = new IPostulaciones();
        postulantes = ipo.getPostulaciones();

        codigo = codigoAgrupacion;

        for (Object x:
             postulantes) {
            Postulantes pos = (Postulantes) x;
            if (pos.getCodigoAgrupacion().equals(String.valueOf(codigoAgrupacion)))
            {
                nombre = pos.getNombreAgrupacion();
            }
        }
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getVotos() {
        return votos;
    }

    public void setVotos(Integer votos) {
        this.votos = votos;
    }

    @Override
    public String toString() {
        return "Resultados{" +
                "codigo=" + codigo +
                ", nombre='" + nombre + '\'' +
                ", votos=" + votos +
                '}';
    }
}
