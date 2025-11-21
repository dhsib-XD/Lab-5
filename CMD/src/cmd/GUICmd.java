package cmd;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author marye
 */
public class GUICmd {

    private CMD cmd = new CMD();
    private int posPrompt;

    private boolean esperandoTexto = false;
    private String textoPendiente = null;

    private JTextArea txtArea = new JTextArea();

    public GUICmd() {
        initComponents();
    }

    public void initComponents() {
        JFrame JVentanaCMD = new JFrame("Administrador: Commando Prompt");
        JVentanaCMD.setSize(820, 520);
        JVentanaCMD.setLocationRelativeTo(null);
        JVentanaCMD.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JVentanaCMD.setLayout(new BorderLayout());

        txtArea.setFont(new Font("Consola", Font.PLAIN, 16));
        txtArea.setBackground(Color.BLACK);
        txtArea.setForeground(Color.WHITE);
        txtArea.setCaretColor(Color.WHITE);
        txtArea.setLineWrap(false);

        JVentanaCMD.add(new JScrollPane(txtArea), BorderLayout.CENTER);

        imprimirLinea("Microsoft Windows [Version 10.0.22621.521]");
        imprimirLinea("(c) Microsoft Corporation. All rights reserved.");
        imprimirLinea("");
        imprimirPrompt();

        ((AbstractDocument) txtArea.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void remove(FilterBypass fb, int off, int len) throws BadLocationException {
                if (off < posPrompt) {
                    return;
                }
                super.remove(fb, off, len);
            }

            @Override
            public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (off < posPrompt) {
                    return;
                }
                super.replace(fb, off, len, text, attrs);
            }

            @Override
            public void insertString(FilterBypass fb, int off, String str, AttributeSet attr)
                    throws BadLocationException {
                if (off < posPrompt) {
                    txtArea.setCaretPosition(txtArea.getDocument().getLength());
                    return;
                }
                super.insertString(fb, off, str, attr);
            }
        });

        txtArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_HOME) {
                    txtArea.setCaretPosition(posPrompt);
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    ejecutarComando();
                }
            }
        });

        JVentanaCMD.setVisible(true);
    }

    private void imprimirLinea(String linea) {
        txtArea.append(linea + "\n");
    }

    private void imprimirPrompt() {
        txtArea.append(cmd.getPrompt());
        posPrompt = txtArea.getDocument().getLength();
        txtArea.setCaretPosition(posPrompt);
    }

    private void ejecutarComando() {
        try {
            Document doc = txtArea.getDocument();
            String linea = doc.getText(posPrompt, doc.getLength() - posPrompt).trim();
            txtArea.append("\n");

            if (linea.isEmpty()) {
                imprimirPrompt();
                return;
            }

            if (esperandoTexto) {
                String txtIngresado = linea;
                String salida = cmd.escribir(textoPendiente, txtIngresado);

                if (!salida.isEmpty()) {
                    imprimirLinea(salida);
                }

                esperandoTexto = false;
                textoPendiente = null;
                imprimirPrompt();
                return;
            }

            String[] parts = linea.split("\\s+", 2);
            String comando = parts[0].toLowerCase();
            String args;

            if (parts.length > 1) {
                args = parts[1];
            } else {
                args = "";
            }

            if ("escribir".equals(comando)) {

                if (args.isBlank()) {
                    imprimirLinea("Comando no valido.");
                } else {

                    int espacio = args.indexOf(' ');
                    if (espacio >= 0) {
                        String archivo = args.substring(0, espacio).trim();
                        String texto = args.substring(espacio + 1);
                        String salida = cmd.escribir(archivo, texto);

                        if (!salida.isEmpty()) {
                            imprimirLinea(salida);
                        }

                    } else {
                        textoPendiente = args;
                        esperandoTexto = true;
                        imprimirLinea("Escriba el contenido y presione Enter:");
                    }
                }
            } else {

                String salida = cmd.Ejecutar(linea);
                if (!salida.isEmpty()) {
                    imprimirLinea(salida);
                }
            }

            imprimirPrompt();

        } catch (BadLocationException ignored) {
        }

    }
}
