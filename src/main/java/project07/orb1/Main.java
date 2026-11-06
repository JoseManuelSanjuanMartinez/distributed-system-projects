package project07.orb1;

/**
 * Práctica 7 · Parte 1 · El programa cliente.
 *
 * Usa la agenda a través de la interfaz IRepository. La única línea que nombra
 * una clase concreta es la que crea la agenda: con new Agenda() el objeto
 * estaría en este mismo proceso; con new Agenda_proxy() está en el servidor.
 */
public class Main {

    public static void main(String[] args) {
        IRepository agenda = new Agenda_proxy();

        agenda.write("Iris", 111111);
        agenda.write("Alba", 222222);
        agenda.write("Rodrigo", 333333);

        System.out.println("Rodrigo: " + agenda.read("Rodrigo"));
        System.out.println("Alba: " + agenda.read("Alba"));
        System.out.println("Iris: " + agenda.read("Iris"));

        agenda.write("Alba", 444444);

        System.out.println("Rodrigo: " + agenda.read("Rodrigo"));
        System.out.println("Alba: " + agenda.read("Alba"));
        System.out.println("Iris: " + agenda.read("Iris"));
    }
}
