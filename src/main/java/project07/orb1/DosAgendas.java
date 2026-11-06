package project07.orb1;

/**
 * Práctica 7 · Parte 1 · Dos agendas en el mismo programa cliente.
 *
 * Crea dos proxies y escribe la misma clave en cada uno. Con dos objetos
 * Agenda locales, cada uno guardaría su propio valor. Ejecútalo contra el
 * servidor de la parte 1 antes de empezar la parte 2.
 */
public class DosAgendas {

    public static void main(String[] args) {
        IRepository agenda1 = new Agenda_proxy();
        IRepository agenda2 = new Agenda_proxy();

        agenda1.write("Iris", 111111);
        agenda2.write("Iris", 222222);

        System.out.println("agenda1, Iris: " + agenda1.read("Iris"));
        System.out.println("agenda2, Iris: " + agenda2.read("Iris"));
    }
}
