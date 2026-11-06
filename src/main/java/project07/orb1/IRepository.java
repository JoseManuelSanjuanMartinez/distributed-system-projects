package project07.orb1;

/**
 * Práctica 7 · El contrato del servicio: un repositorio de tuplas <clave, valor>.
 *
 * La clave es una cadena y el valor, un entero. No puede haber dos tuplas con
 * la misma clave; dos claves distintas sí pueden tener el mismo valor. Lo
 * conocen los dos nodos: en el servidor lo implementa Agenda y en el cliente,
 * Agenda_proxy.
 */
public interface IRepository {

    /** Asigna el valor value a la clave key (y sustituye el anterior). */
    void write(String key, int value);

    /** Devuelve el valor de la clave key. */
    int read(String key);
}
