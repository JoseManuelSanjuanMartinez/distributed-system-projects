package project07.orb2;

import java.util.Hashtable;
import java.util.NoSuchElementException;

/**
 * Práctica 7 · La implementación de verdad del servicio (el «sirviente»).
 *
 * Guarda las tuplas en una tabla hash; podría guardarlas igual en un fichero o
 * en una base de datos. No tiene nada de red: en el servidor la invoca Server,
 * y un programa cualquiera podría usarla en local.
 */
public class Agenda implements IRepository {

    private final Hashtable<String, Integer> ht = new Hashtable<>();

    @Override
    public void write(String key, int value) {
        ht.put(key, value);
    }

    @Override
    public int read(String key) {
        Integer value = ht.get(key);
        if (value == null) {
            throw new NoSuchElementException(
                    "no hay ninguna tupla con la clave " + key);
        }
        return value;
    }
}
