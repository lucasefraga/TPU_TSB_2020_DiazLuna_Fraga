package Importadores;

import Negocio.Region;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class IRegiones {
    private Region argentina;
    private String codigoDistrito;
    private String codigoSeccion;
    private String codigoCircuito;
    private Region distrito = null;
    private Region seccion = null;

    public IRegiones()
    {
        argentina = new Region("Argentina");
    }

    public void importarR()
    {
        File f = new File("descripcion_regiones.dsv");
        Scanner sc = null;

        try {
            sc = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.err.println("No se encontro el archivo!");
        }
        assert sc != null;
        sc.nextLine();
        while (sc.hasNextLine())
        {
            String[] fila = sc.nextLine().split("|");
            String codigoRegion = (fila[0]);
            String nombreRegion = (fila[1]);

            switch (codigoRegion.length())
            {
                case 2:
                    codigoDistrito = codigoRegion;
                    break;

                case 5:
                    codigoDistrito = codigoRegion.substring(0,2);
                    codigoSeccion = codigoRegion.substring(2,5);
                    break;

                case 11:
                    codigoDistrito = codigoRegion.substring(0,2);
                    codigoSeccion = codigoRegion.substring(2,5);
                    codigoCircuito = codigoRegion.substring(5,11);
                    break;
            }

            if (!codigoDistrito.isEmpty())
            {
                if (!argentina.existSubRegion(codigoDistrito))
                {
                    argentina.addSubRegion(codigoDistrito, new Region(nombreRegion));
                    distrito = argentina.getSubRegion(codigoDistrito);
                    if (codigoRegion.length() == 2)
                    {
                        distrito.setNombre(nombreRegion);
                    }
                }
            }
            if (!codigoSeccion.isEmpty())
            {
                if (!distrito.existSubRegion(codigoSeccion))
                {
                    distrito.addSubRegion(codigoSeccion, new Region(nombreRegion));
                }
                seccion = distrito.getSubRegion(codigoSeccion);
                if (codigoRegion.length() == 5)
                {
                    seccion.setNombre(nombreRegion);
                }
            }
            if (!codigoCircuito.isEmpty())
            {
                if (!seccion.existSubRegion(codigoCircuito))
                {
                    seccion.addSubRegion(codigoCircuito, new Region(nombreRegion));
                }
            }
        }
    }




    public Region getArgentina()
    {
        return argentina;
    }
}
