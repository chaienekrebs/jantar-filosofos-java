/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketcliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.io.IOException;

public class SocketClienteTCP {

    Socket socket;
    BufferedReader in;
    PrintWriter out;
    BufferedReader inReader;

    String resposta;

    public SocketClienteTCP(String servidor, int porta, String mensagemEnviar) {
        try {
            socket = new Socket(servidor, porta);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            inReader = new BufferedReader(new InputStreamReader(System.in));

            if ("FIM".equals(mensagemEnviar)) {
                System.out.println("Encerrando o cliente.");
                out.close();
                in.close();
                socket.close();
                return;
            }

            out.println(mensagemEnviar);

            respostaLoop:
            while ((resposta = in.readLine()) != null) {
                if (resposta == null) {
                    break respostaLoop;
                }

                if ("Jantar finalizado!".equals(resposta)) {
                    System.out.println("Jantar finalizado!");

                } else {
                    System.out.println("Retornou: [" + resposta + "]");
                }
            }

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

}
