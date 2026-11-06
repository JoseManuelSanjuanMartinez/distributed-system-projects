package project07.orb2;

/**
 * Práctica 7 · Parte 2 · El programa cliente, con dos agendas.
 *
 * Como en la parte 1, usa las agendas a través de IRepository. Crea dos
 * agendas y escribe en las dos la clave Iris con valores distintos: cada una
 * debe conservar el suyo.
 */
public class Main {

    public static void main(String[] args) {
        IRepository agenda1 = new Agenda_proxy();
        agenda1.write("Iris", 111111);
        agenda1.write("Rodrigo", 222222);
        agenda1.write("Alba", 333333);
        System.out.println("agenda1");
        System.out.println("Alba: " + agenda1.read("Alba"));
        System.out.println("Rodrigo: " + agenda1.read("Rodrigo"));
        System.out.println("Iris: " + agenda1.read("Iris"));

        IRepository agenda2 = new Agenda_proxy();
        agenda2.write("Iris", 0);
        agenda2.write("Pablo", 444444);
        agenda2.write("Laura", 555555);
        agenda1.write("Iris", 666666);
        System.out.println();
        System.out.println("agenda2");
        System.out.println("Laura: " + agenda2.read("Laura"));
        System.out.println("Pablo: " + agenda2.read("Pablo"));
        System.out.println("Iris: " + agenda2.read("Iris"));

        System.out.println();
        System.out.println("agenda1");
        System.out.println("Alba: " + agenda1.read("Alba"));
        System.out.println("Rodrigo: " + agenda1.read("Rodrigo"));
        System.out.println("Iris: " + agenda1.read("Iris"));
    }
}
