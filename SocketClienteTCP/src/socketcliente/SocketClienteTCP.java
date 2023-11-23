/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketcliente;

import java.io.*;
import java.net.*;

public class SocketClienteTCP {

    Socket socket;
    BufferedReader in;
    PrintWriter out;
    BufferedReader inReader;
    String mensagemEnviar;
    String resposta;

    public SocketClienteTCP(String servidor, int porta) {

        try {
            /* Inicialização de socket TCP */
            socket = new Socket(servidor, porta);

            /* Inicialização dos fluxos de entrada e saída */
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            /* Abertura da entrada padrão */
            inReader = new BufferedReader(new InputStreamReader(System.in));

            clienteLoop:
            while (true) {
                System.out.print("Msg: ");
                while ((mensagemEnviar = inReader.readLine()) != null) {

                    if ("FIM".equals(mensagemEnviar)) {
                        System.out.println("Encerrando o cliente.");
                        // Encerra os recursos antes de sair
                        out.close();
                        in.close();
                        socket.close();
                        return; // Sai do método main e encerra o programa
                    }

                    /* Envio da mensagem */
                    out.println(mensagemEnviar);

                    respostaLoop:
                    while ((resposta = in.readLine()) != null) {
                        if (resposta == null) {
                            break respostaLoop;
                        }

                        if ("Jantar finalizado!".equals(resposta)) {
                            // Reinicia o bloco interno
                            System.out.println("Jantar finalizado! Reiniciando o bloco interno.");
                            continue clienteLoop;
                        } else {
                            /* Imprime na tela o retorno */
                            System.out.println("Retornou: [" + resposta + "]");
                            
                        }
                    }
                }
            }
//            /* Finaliza tudo */
//            out.close();
//            in.close();
//            socket.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public static void main(String[] args) {
        SocketClienteTCP socketClienteTCP = new SocketClienteTCP("localhost", 6789);
    }
}
