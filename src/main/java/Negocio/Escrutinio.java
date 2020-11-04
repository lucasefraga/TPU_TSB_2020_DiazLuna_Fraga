package Negocio;

import Importadores.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class Escrutinio {
    private Region region;
    private ArrayList postulantes;
    private String codDistrito;
    private String codCircuito;
    private String codSeccion;
    private int lista;
    private int votos;


    public Escrutinio() {
        this.region = new Region("Argentina");
        generarRegion();
    }

    private void generarRegion() {
        IPostulaciones iPos = new IPostulaciones();
        postulantes = iPos.getPostulaciones();

        IRegiones iReg = new IRegiones();
        iReg.importarR();

        System.out.println("Carga de Regiones");
        region = iReg.getArgentina();
        System.out.println("fin de carga de Regiones");
        System.out.println("Comienzo de Escrutinio");
        this.recuento();
        System.out.println("Fin de Escrutinio");
    }

    private void recuento() {
        File f = new File("mesas_totales_agrp_politica.dsv");
        Scanner sc = null;

        try {
            sc = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.err.println("No se encontro el archivo!");
        }
        assert sc != null;
        sc.nextLine();
        while (sc.hasNextLine()) {
            String[] fila = sc.nextLine().split("|");
            String codigoCategoria = (fila[4]);
            if (codigoCategoria.equals("000100000000000")) {
                String codigoCircuito = (fila[2]);
                String codigoMesa = (fila[3]);
                String codigoAgrupacion = (fila[5]);
                String votosAgrupacion = (fila[6]);

                codDistrito = codCircuito.substring(0,2);
                codSeccion = codCircuito.substring(2,5);
                codCircuito = codCircuito.substring(5,11);

                lista = Integer.parseInt(codigoAgrupacion);
                votos = Integer.parseInt(votosAgrupacion);

                //Suma de votos
                region.sumarVotos(lista, Integer.parseInt(votosAgrupacion));
                region.getSubRegion(codDistrito).sumarVotos(lista, votos);
                region.getSubRegion(codDistrito).getSubRegion(codSeccion).sumarVotos(lista, votos);
                region.getSubRegion(codDistrito).getSubRegion(codSeccion).getSubRegion(codCircuito).sumarVotos(lista, votos);

                Region mesa = region.getSubRegion(codDistrito).getSubRegion(codSeccion).getSubRegion(codCircuito);
                if (!mesa.existSubRegion(codigoMesa))
                {
                    mesa.addSubRegion(codigoMesa, new Region(codigoMesa));
                }
                region.getSubRegion(codDistrito).getSubRegion(codSeccion).getSubRegion(codCircuito).getSubRegion(codigoMesa).sumarVotos(lista,votos);
            }
        }
    }

    public Collection getDistritos(Region r)
    {
        ArrayList<Region> nombreDistritos= new ArrayList<>();
        nombreDistritos.addAll(r.subRegion.values());
        ObservableList<Region> opciones = FXCollections.observableArrayList(nombreDistritos);
        return opciones;
    }

    public Region getRegion() {
        return region;
    }
}
