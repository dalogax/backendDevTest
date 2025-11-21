package com.example.demo.shared.service;

import com.example.demo.shared.exceptions.GenericException;
import com.example.demo.shared.valueObject.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@Slf4j
public class ResponseHandlerService {

    /**
     * Ejecuta una acción y devuelve un ResponseDTO con manejo de excepciones y logging.
     * @param action nombre de la acción (para logs e infoMessages)
     * @param supplier lógica de negocio que devuelve datos
     * @param <T> tipo de dato de la respuesta
     * @return ResponseDTO<T>
     */
    public <T> ResponseDTO<T> execute(String action, Supplier<T> supplier) {
        ResponseDTO<T> response = new ResponseDTO<>();
        try {
            T data = supplier.get();
            response.setData(data);
            response.addInfo(action + "_OK");
        } catch (GenericException e) {
            response.setErrorMessages(e.getErrorMessages());
            log.warn("{} failed with business exception", action, e);
        } catch (Exception e) {
            response.addError(e.getMessage());
            log.error("{} failed", action, e);
        }
        return response;
    }
}
