package Negocio;

import Importadores.*;
import clases.TSBHashtableDA;

import java.util.ArrayList;

public class Region {
    private TSBHashtableDA<Integer, Acumulador> total;
    private TSBHashtableDA<String, Region> subRegion;
    private String nombre;

    public Region(String nombre)
    {
        this.nombre = nombre;
        total = new TSBHashtableDA<>();
        this.cargarCodigoAgrupacion();
        subRegion = new TSBHashtableDA<>();
    }

    private void cargarCodigoAgrupacion()
    {
        ArrayList postulaciones = IPostulaciones.getPostulaciones();
    }


}
