package guru.qa.niffler.model;

public enum CurrencyValues {
    RUB, USD, EUR, KZT;

    public static CurrencyValues convertToEnum(String value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case "USD" -> CurrencyValues.USD;
            case "EUR" -> CurrencyValues.EUR;
            case "GBP" -> CurrencyValues.KZT;
            case "RUB" -> CurrencyValues.RUB;
            default -> throw new IllegalArgumentException("Unsupported currency value: " + value);
        };
    }

}

