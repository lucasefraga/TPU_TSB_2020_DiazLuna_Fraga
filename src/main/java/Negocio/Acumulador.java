package Negocio;

public class Acumulador {
    private int acumulador;


    public Acumulador()
    {
        acumulador = 0;
    }

    public void add(int x)
    {
        acumulador += x;
    }

    public void setAcumulador(int acumulador) {
        this.acumulador = acumulador;
    }

    public int getAcumulador() {
        return acumulador;
    }
}
