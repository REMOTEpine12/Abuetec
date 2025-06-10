package com.example.aplicacion;

public class Medicamento {
    public Medicamento(String nombre, String dosis, String frecuencia, String hora, int dosisTotales) {
        this.nombre = nombre;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.hora = hora;
        this.dosisTotales = dosisTotales;
        this.dosisTomadas = 0;
    }
    public Medicamento() {
        // Requerido por daoMedicamentos
    }

    private int id;
    private String nombre;
    private String dosis;
    private String frecuencia;
    private String hora;
    private int dosisTotales;
    private int dosisTomadas;

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }

    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public int getDosisTotales() { return dosisTotales; }
    public void setDosisTotales(int dosisTotales) { this.dosisTotales = dosisTotales; }

    public int getDosisTomadas() { return dosisTomadas; }
    public void setDosisTomadas(int dosisTomadas) { this.dosisTomadas = dosisTomadas; }
}
