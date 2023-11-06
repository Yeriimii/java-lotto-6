package lotto.system.validator;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern ARABIC_NUMBER_PATTERN = Pattern.compile("^[0-9]?$");

    private enum ExceptionMessage {
        EMPTY_VALUE_NOT_ALLOWED("빈 값을 입력할 수 없습니다."),
        INVALID_ARABIC_NUMBER("아라비아 숫자만 입력할 수 있습니다.");
        private final String message;

        ExceptionMessage(String message) {
            this.message = message;
        }
    }

    public void validateNotEmpty(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.EMPTY_VALUE_NOT_ALLOWED.message);
        }
    }

    public void validateArabicNumber(String input) {
        if (!ARABIC_NUMBER_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_ARABIC_NUMBER.message);
        }
    }
}
