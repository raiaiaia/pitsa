package com.ufcg.psoft.pitsa.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    private CustomErrorType defaultCustomErrorTypeConstruct(String message) {
        return CustomErrorType.builder()
                .timestamp(LocalDateTime.now())
                .errors(new ArrayList<>())
                .message(message)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        CustomErrorType customErrorType = defaultCustomErrorTypeConstruct(
                "Erros de validacao encontrados"
        );
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            customErrorType.getErrors().add(fieldError.getDefaultMessage());
        }
        return customErrorType;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        if (ex.getMostSpecificCause().toString().contains("No enum constant") && ex.getLocalizedMessage().contains("com.ufcg.psoft.pitsa.model.enums.")) {
            return onInvalidTypeException(new TipoInvalidoException(ex.getPropertyName()));
        }
        return defaultCustomErrorTypeConstruct("Valor invalido para parametro " + ex.getPropertyName());
    }

    @ExceptionHandler(EntidadeNaoExisteException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public CustomErrorType onEntityNotFoundException(EntidadeNaoExisteException e) {
        return defaultCustomErrorTypeConstruct(e.getMessage());
    }

    @ExceptionHandler(RelacionamentoNaoExisteException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public CustomErrorType onRelationNotFoundException(RelacionamentoNaoExisteException e) {
        return defaultCustomErrorTypeConstruct(e.getMessage());
    }

    @ExceptionHandler(CodigoDeAcessoInvalidoException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public CustomErrorType onAccessCodeInvalidException(CodigoDeAcessoInvalidoException e) {
        return defaultCustomErrorTypeConstruct(e.getMessage());
    }

    @ExceptionHandler(TipoInvalidoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType onInvalidTypeException(TipoInvalidoException e) {
        return defaultCustomErrorTypeConstruct(e.getMessage());
    }

    @ExceptionHandler(AssociacaoJaExisteException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public CustomErrorType onExistingAssociationException(AssociacaoJaExisteException e) {
        return defaultCustomErrorTypeConstruct(e.getMessage());
    }

    @ExceptionHandler(IdentificadorInvalidoException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public CustomErrorType onInvalidIdentifierException(IdentificadorInvalidoException e) {
        return defaultCustomErrorTypeConstruct(e.getMessage());
    }

    @ExceptionHandler(OperacaoInvalidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType onInvalidOperationException(OperacaoInvalidaException e) {
        return defaultCustomErrorTypeConstruct(e.getMessage());
    }

    @ExceptionHandler(QuantidadeDeSaboresInvalidaException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public CustomErrorType onInvalidFlavorsQuantityException(QuantidadeDeSaboresInvalidaException e) {
        return defaultCustomErrorTypeConstruct(e.getMessage());
    }

    @ExceptionHandler(SaborNaoEstaDisponivelException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType onEFlavorNotAvailableException(SaborNaoEstaDisponivelException e) {
        return defaultCustomErrorTypeConstruct(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType onConstraintViolation(ConstraintViolationException e) {
        CustomErrorType customErrorType = defaultCustomErrorTypeConstruct(
                "Erros de validacao encontrados"
        );
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            customErrorType.getErrors().add(violation.getMessage());
        }
        return customErrorType;
    }

    @ExceptionHandler(InteresseBloqueadoException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType onBlockedInterestException(InteresseBloqueadoException e) {
        return defaultCustomErrorTypeConstruct(e.getMessage());
    }


    @ExceptionHandler(PitsAException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType onCommerceException(PitsAException e) {
        return defaultCustomErrorTypeConstruct(
                e.getMessage()
        );
    }

}
