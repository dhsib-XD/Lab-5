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
            case "...":
                return subir();
            case "dir":
                return dir();
            case "date":
                return fecha();
            case "time":
                return hora();
            case "escribir":
                return escribir(parametro);
            case "leer":
                return leerArchivo(parametro);
            default:
                return "Comando no valido";
        }
    }
    
    private String crearCarpeta(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Comando no valido";
        }
        File nueva = new File(carpetaActual, nombre);
        if (nueva.exists()) {
            return "La carpeta \"" + nueva.getName() + "\" ya existe";
        }
        return nueva.mkdir() ? "Carpeta creada " + nueva.getName() : "No se pudo crear la carpeta.";
    }
    
    private String crearArchivo(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Comando no valido";
        }
        try {
            File archivo = new File(carpetaActual, nombre);
            return archivo.createNewFile() ? "Archivo creado: " + archivo.getName()
                    : "El archivo ya existe.";
        } catch (IOException e) {
            return "Error al crear el archivo: " + e.getMessage();
        }
    }
    
    private String borrar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Comando no valido";
        }
        File objetivo = new File(carpetaActual, nombre);
        if (!objetivo.exists()) {
            return "No existe";
        }
        return eliminarTodo(objetivo) ? "Eliminado: " + nombre
                : "No se pudo eliminar";
    }
    
    private boolean eliminarTodo(File f) {
        if (f.isDirectory()) {
            File[] hijos = f.listFiles();
            if (hijos != null) {
                for (File h : hijos) {
                    if (!eliminarTodo(h)) {
                        return false;
                    }
                }
            }
        }
        return f.delete();
    }
    
    private String mover(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Comando no valido";
        }
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
    
    private String subir() {
        File padre = carpetaActual.getParentFile();
        if (padre != null) {
            carpetaActual = padre;
            return "";
        }
        return "Ya estas en la raiz";
    }
    
    private String dir() {
        File[] lista = carpetaActual.listFiles();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder salida = new StringBuilder();
        salida.append("\nDirectorio de: ").append(carpetaActual.getAbsolutePath()).append("\n\n");
        
        salida.append(String.format("%-19s  %-6s  %-10s  %s%n", "Modificacion", "Tipo", "Tamano", "Nombre"));
        salida.append("---------------------------------------------------------\n");
        if (lista != null) {
            for (File f : lista) {
                String fecha = formato.format(new Date(f.lastModified()));
                String tipo = f.isDirectory() ? "<DIR>" : "FILE";
                String tam = f.isDirectory() ? "-" : convertirTam(f.length());
                String nombre = f.getName();
                salida.append(String.format("%-19s  %-6s  %-10s  %s%n", fecha, tipo, tam, nombre));
            }
        }
        return salida.toString();
    }
    
    private String convertirTam(Long b) {
        if (b < 1024) {
            return b + " B";
        }
        double temp = b;
        String[] unidades = {"KB", "MB", "GB", "TB", "PB"};
        int pos = 0;
        temp /= 1024.0;
        while (temp >= 1024.0 && pos < unidades.length - 1) {
            temp /= 1024.0;
            pos++;
        }
        int t = (int) Math.round(temp * 10);
        int entero = t / 10;
        int decimal = t % 10;
        String numero = (decimal == 0) ? ("" + entero) : (entero + "." + decimal);
        return numero + " " + unidades[pos]; // añadida separacion
    }
    
    private String espacio(String txt, int ancho) {
        if (txt == null) {
            txt = "";
        }
        int dif = ancho - txt.length();
        if (dif <= 0) {
            return txt;
        }
        StringBuilder esp = new StringBuilder();
        for (int i = 0; i < dif; i++) {
            esp.append(' ');
        }
        return txt + esp.toString();
    }
    
    private String fecha() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
    
    private String hora() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
    
    public String escribir(String nombre, String texto) {
        if (nombre == null || nombre.isBlank()) {
            return "Comando no valido";
        }
        if (texto == null) {
            texto = "";
        }
        try (FileWriter fw = new FileWriter(new File(carpetaActual, nombre), true)) {
            fw.write(texto + System.lineSeparator());
            return "Texto escrito en " + nombre;
        } catch (IOException e) {
            return "Error al escribir: " + e.getMessage();
        }
    }
    
    private String escribir(String parametros) {
        if (parametros == null || parametros.isBlank()) {
            return "Formato: escribir archivo.txt texto";
        }
        String[] partes = parametros.split("\\s+", 2);
        if (partes.length < 2) {
            return "Debe colocar archivo y texto";
        }
        String archivo = partes[0];
        String texto = partes[1];
        return escribir(archivo, texto);
    }
    
    public String leerArchivo(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Comando no valido";
        }
        File archivo = new File(carpetaActual, nombre);
        if (!archivo.exists()) {
            return "Archivo no encontrado.";
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            StringBuilder contenido = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea).append(System.lineSeparator());
            }
            return contenido.toString();
        } catch (IOException e) {
            return "Error al leer: " + e.getMessage();
        }
    }
}
