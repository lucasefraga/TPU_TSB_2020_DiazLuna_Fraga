package Negocio;

import Importadores.*;
import clases.TSBHashtableDA;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2RTFDTM;

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

        for (Object x:
             postulaciones)
        {
            Postulantes p = (Postulantes) x;
            this.total.put(Integer.valueOf(p.getCodigoAgrupacion()), new Acumulador());
        }
    }

    public void addSubRegion(String k, Region r)
    {
        subRegion.put(k,r);
    }

    public Region getSubRegion(String k)
    {
        return subRegion.get(k);
    }

    public boolean existSubRegion(String k)
    {
        return subRegion.containsKey(k);
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void sumarVotos(Integer codigoAgrupacion, int votos)
    {
        total.get(codigoAgrupacion).add(votos);
    }

    public Integer getVotos(int x)
    {
        return total.get(x).getAcumulador();
    }

    public TSBHashtableDA<Integer, Acumulador> getTotal()
    {
        return total;
    }

    @Override
    public String toString()
    {
        return nombre;
    }
}
