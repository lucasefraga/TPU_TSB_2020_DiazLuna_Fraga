package Importadores;

import Negocio.Postulantes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class IPostulaciones {
    private static ArrayList<Postulantes> postulaciones = new ArrayList<>();

    public IPostulaciones()
    {
        if (postulaciones.size() == 0)
        {
            this.importar();
            System.out.println("Las postulaciones se han importado");
        }
    }

    public void importar() {
        File f = new File("descripcion_postulaciones.dsv");
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
            String codigoCategoria = (fila[0]);
            if (codigoCategoria.equals("000100000000000"))
            {
                String codigoAgrupacion = (fila[2]);
                String nombreAgrupacion = (fila[3]);
                String codigoLista = (fila[4]);
                String nombreLista = (fila[5]);

                Postulantes p = new Postulantes(codigoAgrupacion,nombreAgrupacion,codigoLista,nombreLista);
                postulaciones.add(p);
            }
        }

        public static ArrayList<Postulantes> getPostulaciones()
        {
            return postulaciones;    //no se porque no funciona esto, lo copie tal cual. me dice q la clase importar tiene que ser tipo arraylist, pero no se q onda
        }
    }

}
