package project05.comun;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Una línea de consola con la hora y quién habla: «[17:02:41.318] taquilla 2: cierro». */
public final class Registro {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private Registro() {
    }

    public static void linea(String quien, String que) {
        System.out.println("[" + LocalTime.now().format(HORA) + "] " + quien + ": " + que);
    }
}
