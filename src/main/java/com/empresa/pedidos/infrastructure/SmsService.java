package com.empresa.pedidos.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    public void enviar(String movil, String texto) {
        log.info("[SMS] a {} -> {}", movil, texto);
    }
}
