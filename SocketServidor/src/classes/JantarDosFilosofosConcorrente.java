/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

/**
 *
 * @author chayk
 */
import java.io.PrintWriter;
import java.util.concurrent.Semaphore;

public class JantarDosFilosofosConcorrente {

    private Thread[] filosofos;
    private static final int NUMFILO = 5;
    private static Semaphore[] hashi;
    private static Semaphore saleiro = new Semaphore(1);
    private PrintWriter clienteOut;

    public void iniciarJantar(PrintWriter out) {
        this.clienteOut = out;
        // Inicializa os semáforos no bloco estático
        hashi = new Semaphore[NUMFILO];
        for (int i = 0; i < NUMFILO; i++) {
            hashi[i] = new Semaphore(1);
        }

        filosofos = new Thread[NUMFILO];

        for (int i = 0; i < NUMFILO; i++) {
            filosofos[i] = new Thread(new Filosofo(i));
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

    public class Filosofo implements Runnable {

        private int id;
        private int dir;
        private int esq;

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
                    if (saleiro.tryAcquire()) { // tenta pegar o saleiro
                        if (hashi[dir].tryAcquire()) { // tenta pegar palito direito
                            if (hashi[esq].tryAcquire()) { // tenta pegar palito esquerdo
                                saleiro.release(); // devolve saleiro
                                comer();
                                hashi[dir].release(); // devolve palito direito
                                hashi[esq].release(); // devolve palito esquerdo
                            } else {
                                saleiro.release(); // libera o saleiro se não conseguiu o palito esquerdo
                            }
                        } else {
                            saleiro.release(); // libera o saleiro se não conseguiu o palito direito
                        }
                    }
                }
            } catch (InterruptedException e) {
                // A interrupção é esperada ao finalizar o jantar
            }
        }

        private void meditar() throws InterruptedException {
            clienteOut.println("Filosofo " + id + " meditando.");
            // Lógica de meditação
            Thread.sleep(1000); // pausa de 1 segundo
        }

        private void comer() throws InterruptedException {
            clienteOut.println("Filosofo " + id + " comendo.");
            // Lógica de comer
            Thread.sleep(2000); // pausa de 2 segundos
        }
    }
}
