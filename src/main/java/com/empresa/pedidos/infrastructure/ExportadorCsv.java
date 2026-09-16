package com.empresa.pedidos.infrastructure;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * PATRON: Template Method (variante execute-around, con lambda)
 * Rol: el metodo fija el ESQUELETO invariable -> abrir recurso, escribir
 * cabecera, cerrar, gestionar IOException. El cliente aporta unicamente la
 * parte variable (que filas escribir) mediante un Consumer.
 *
 * Por que asi y no con herencia: el Template Method clasico obliga a crear una
 * subclase por variacion. Con un Consumer se logra lo mismo sin jerarquia, que
 * es el estilo que usa el propio Spring en JdbcTemplate o TransactionTemplate.
 *
 * Ventaja real: es IMPOSIBLE que un cliente olvide cerrar el writer o duplique
 * el manejo de errores de E/S.
 */
@Component
public class ExportadorCsv {

    public String exportar(String cabecera, Consumer<BufferedWriter> escritorDeFilas) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        // try-with-resources: el cierre esta garantizado aqui, una sola vez.
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(salida, StandardCharsets.UTF_8))) {

            writer.write(cabecera);   // paso fijo 1
            writer.newLine();

            escritorDeFilas.accept(writer);   // <-- PASO VARIABLE (el "hueco")

            writer.flush();           // paso fijo 2
        } catch (IOException e) {
            // El cliente nunca ve una checked exception de E/S.
            throw new UncheckedIOException("Error exportando CSV", e);
        }
        return salida.toString(StandardCharsets.UTF_8);
    }

    /** Utilidad para que las lambdas cliente no lidien con IOException. */
    public static void escribirLinea(BufferedWriter writer, String linea) {
        try {
            writer.write(linea);
            writer.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
