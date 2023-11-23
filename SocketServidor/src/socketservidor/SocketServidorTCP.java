package socketservidor;

import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class SocketServidorTCP {

    ServerSocket serverSocket;
    Socket clientSocket;
    PrintWriter out;
    BufferedReader in;
    String comando;
    JantarDosFilosofos jantarDosFilosofos;
    public static final int NUMFILO = 5;
    public static Semaphore[] hashi = new Semaphore[NUMFILO];
    public static Semaphore saleiro = new Semaphore(1);

    static {
        // Inicializa os semáforos no bloco estático
        for (int i = 0; i < NUMFILO; i++) {
            hashi[i] = new Semaphore(1);
        }
    }

    public SocketServidorTCP(int porta) {
        try {
            /* Inicializacao do server socket TCP */
            serverSocket = new ServerSocket(porta);
            System.out.println("Servidor executando ... ");
            jantarDosFilosofos = new JantarDosFilosofos();
            while (true) {
                /* Espera por um cliente */
                clientSocket = serverSocket.accept();
                System.out.println("Novo cliente: " + clientSocket.toString());

                /* Preparacao dos fluxos de entrada e saida */
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                /* Recuperacao dos comandos */
                while ((comando = in.readLine()) != null) {
                    System.out.println("Comando recebido: [" + comando + "]");
                    /* Se comando for "HORA" */
                    if (comando.equals("HORA")) {
                        String hora = new SimpleDateFormat("d MMM yyyy HH:mm:ss").format(new Date());
                        out.println(hora);
                    } else if (comando.equals("JANTAR")) {
                        out.println("Jantar iniciado!");
                        iniciarJantar();
                        out.println("Jantar finalizado!");
                    } else if (comando.equals("FIM")) {
                        break;
                    } else {
                        out.println("Comando Desconhecido");
                    }
                }
                /* Finaliza tudo */
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

    private void iniciarJantar() {
        Thread[] filosofos = new Thread[NUMFILO];

        for (int i = 0; i < NUMFILO; i++) {
            filosofos[i] = new Thread(jantarDosFilosofos.new Filosofo(i));
            filosofos[i].start();
        }

        try {
            // Deixe o jantar dos filósofos ocorrer por um tempo arbitrário
            Thread.sleep(10000); // 10 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Interrompe os filósofos para encerrar o jantar
        for (Thread filosofo : filosofos) {
            filosofo.interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        SocketServidorTCP socketServidorTCP = new SocketServidorTCP(6789);
    }

    public class JantarDosFilosofos {

        public class Filosofo implements Runnable {

            public int id;
            public int dir;
            public int esq;

            public Filosofo(int id) {
                this.id = id;
                this.dir = id;
                this.esq = (id + 1) % NUMFILO;
            }

            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        meditar();
                        saleiro.acquire(); // pega saleiro
                        hashi[dir].acquire(); // pega palito direito
                        hashi[esq].acquire(); // pega palito esquerdo
                        saleiro.release(); // devolve saleiro
                        comer();
                        hashi[dir].release(); // devolve palito direito
                        hashi[esq].release(); // devolve palito esquerdo
                    }
                } catch (InterruptedException e) {
               
                }
            }

            public void meditar() throws InterruptedException {
                out.println("Filósofo " + id + " meditando.");
                // Lógica de meditação
                Thread.sleep(1000); // pausa de 1 segundo
            }

            public void comer() throws InterruptedException {
                out.println("Filósofo " + id + " comendo.");
                // Lógica de comer
                Thread.sleep(2000); // pausa de 2 segundos
            }
        }
    }
}
