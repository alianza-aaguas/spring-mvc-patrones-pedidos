package com.empresa.pedidos.controller;

import com.empresa.pedidos.exception.EstadoInvalidoException;
import com.empresa.pedidos.exception.PagoRechazadoException;
import com.empresa.pedidos.exception.PedidoNoEncontradoException;
import com.empresa.pedidos.exception.RecursoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * PATRON: Chain of Responsibility / Interceptor (aportado por Spring MVC)
 * Rol: punto UNICO de traduccion excepcion de dominio -> respuesta HTTP.
 *
 * Por que: sin esto, cada metodo del controlador acabaria plagado de
 * try/catch repetidos. Aqui la regla se declara una vez y aplica a todos
 * los controladores.
 *
 * Se usa ProblemDetail (RFC 7807), el estandar de Spring 6 para errores.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({PedidoNoEncontradoException.class, RecursoNoEncontradoException.class})
    public ProblemDetail noEncontrado(RuntimeException e) {
        return problema(HttpStatus.NOT_FOUND, "Recurso no encontrado", e.getMessage());
    }

    /** Regla de negocio violada -> 409 Conflict, no 500. */
    @ExceptionHandler(EstadoInvalidoException.class)
    public ProblemDetail estadoInvalido(EstadoInvalidoException e) {
        return problema(HttpStatus.CONFLICT, "Estado invalido", e.getMessage());
    }

    @ExceptionHandler(PagoRechazadoException.class)
    public ProblemDetail pagoRechazado(PagoRechazadoException e) {
        return problema(HttpStatus.PAYMENT_REQUIRED, "Pago rechazado", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail argumentoInvalido(IllegalArgumentException e) {
        return problema(HttpStatus.BAD_REQUEST, "Peticion invalida", e.getMessage());
    }

    /** Errores de Bean Validation agregados en un solo mensaje. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacion(MethodArgumentNotValidException e) {
        String detalle = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return problema(HttpStatus.BAD_REQUEST, "Datos invalidos", detalle);
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String detalle) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalle);
        pd.setTitle(titulo);
        return pd;
    }
}
