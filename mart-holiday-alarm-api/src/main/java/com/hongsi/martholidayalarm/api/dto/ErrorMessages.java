package com.hongsi.martholidayalarm.api.dto;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class ErrorMessages {

    private final List<ErrorMessage> errors = new ArrayList<>();

    public ErrorMessages(BindingResult bindingResult) {
        errors.addAll(parseFieldErrors(bindingResult));
    }

    private List<ErrorMessage> parseFieldErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(ErrorMessage::new)
                .collect(Collectors.toList());
    }

    @Getter
    @ToString
    public static class ErrorMessage {

        private final String field;
        private final String message;

        public ErrorMessage(FieldError fieldError) {
            this.field = fieldError.getField();
            this.message = getMessageFromError(fieldError);
        }

        private String getMessageFromError(FieldError filedError) {
            try {
                // TypeMismatchException 은 메시지를 알아볼 수 없기 때문에 알아볼 수 있는 메세지로 별도 처리
                TypeMismatchException exception = filedError.unwrap(TypeMismatchException.class);
                return String.format("%s 타입만 가능합니다.", exception.getRequiredType().getSimpleName());
            } catch (Exception e) {
                // 그 외는 메세지 기본 설정된 메세지 사용
                return filedError.getDefaultMessage();
            }
        }
    }
}
