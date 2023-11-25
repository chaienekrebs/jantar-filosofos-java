package socketservidor;

import classes.JantarDosFilosofos;
import classes.JantarDosFilosofosConcorrente;
import java.io.*;
import java.net.*;

public class SocketServidorTCP {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String comando;
    private JantarDosFilosofos jantarDosFilosofos;
    private JantarDosFilosofosConcorrente jantarDosFilosofosConcorrente;

    public static void main(String[] args) {
        SocketServidorTCP socketServidorTCP = new SocketServidorTCP(6789);
    }

    public SocketServidorTCP(int porta) {
        try {
            serverSocket = new ServerSocket(porta);
            System.out.println("Servidor executando ... ");
            jantarDosFilosofos = new JantarDosFilosofos();
            jantarDosFilosofosConcorrente = new JantarDosFilosofosConcorrente();
            
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Novo cliente: " + clientSocket.toString());

                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while ((comando = in.readLine()) != null) {
                    System.out.println("Comando recebido: [" + comando + "]");

                    if (comando.equals("SOLUCAO")) {
                        out.println("Jantar iniciado!");
                        jantarDosFilosofos.iniciarJantar(out);
                        out.println("Jantar finalizado!");
                    } else if (comando.equals("PROBLEMA")) {
                        out.println("Jantar iniciado!");
                        jantarDosFilosofosConcorrente.iniciarJantar(out);
                        out.println("Jantar finalizado!");
                    } else if (comando.equals("FIM")) {
                        break;
                    } else {
                        out.println("Comando Desconhecido");
                    }
                }

                System.out.print("Cliente desconectando... ");
                out.close();
                in.close();
                clientSocket.close();
                System.out.println("ok");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
