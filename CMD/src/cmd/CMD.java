/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cmd;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author CarlosXl
 */
public class CMD {

    private File carpetaActual;

    public CMD() {
        carpetaActual = new File(System.getProperty("user.dir"));
    }

    public String getPrompt() {
        return carpetaActual.getAbsolutePath() + ">";
    }

    public String Ejecutar(String entrada) {
        if (entrada == null) {
            entrada = " ";
        }
        entrada = entrada.trim();
        if (entrada.isEmpty()) {
            return "";
        }
        String[] datos = entrada.split("\\s+", 2);
        String comando = datos[0].toLowerCase();
        String parametro = (datos.length > 1) ? datos[1] : "";

        switch (comando) {
            case "mkdir":
                return crearCarpeta(parametro);
            case "mfile":
                return crearArchivo(parametro);
            case "rm":
                return borrar(parametro);
            case "cd":
                return mover(parametro);
            case "..":
                return subir();
            case "dir":
                return listar();
            case "date":
                return fecha();
            case "time":
                return hora();
            case "leer":
                return leerArchivo(parametro);
            default:
                return "Comando no valido";
        }
    }

    private String crearCarpeta(String nombre) {
        if (nombre.isBlank()) {
            return "Comando no valido";
        }

        File nueva = new File(carpetaActual, nombre);

        if (nueva.exists()) {
            return "La carpeta \"" + nueva.getName() + "\" ya existe";
        }

        return nueva.mkdir() ? "Carpeta creada " + nueva.getName() : "No se pudo crear la carpeta.";
    }

    private String crearArchivo(String nombre) {
        if (nombre.isBlank()) {
            return "Comando no valido";
        }

        try {
            File archivo = new File(carpetaActual, nombre);
            return archivo.createNewFile() ? "Archivo creado: " + archivo.getName()
                    : "El archivo ya existe.";
        } catch (IOException e) {
            return "Error al crear el archivo: "+e.getMessage();
        }
    }
    
    private String borrar(String nombre){
        if (nombre.isBlank()) 
            return "Comando no valido";
        

        File objetivo = new File(carpetaActual, nombre);

        if (objetivo.exists()) 
            return "No existe";
        
        return eliminarTodo(objetivo) ? "Eliminado: " + nombre 
                                      : "No se pudo eliminar";
    }
    
    private boolean eliminarTodo(File f) {
        if (f.isDirectory()) {
            File[] hijos = f.listFiles();
            if (hijos != null) {
                for (File h : hijos) {
                    if (!eliminarTodo(h)) return false;
                }
            }
        }
        return f.delete();
    }
    
    private String mover(String nombre) {
        if (nombre.isBlank()) return "Comando no valido";

        File nuevaRuta = new File(nombre);

        if (!nuevaRuta.isAbsolute()) {
            nuevaRuta = new File(carpetaActual, nombre);
        }

        if (nuevaRuta.exists() && nuevaRuta.isDirectory()) {
            carpetaActual = nuevaRuta;
            return "";
        }

        return "Directorio no encontrado";
    }

}
