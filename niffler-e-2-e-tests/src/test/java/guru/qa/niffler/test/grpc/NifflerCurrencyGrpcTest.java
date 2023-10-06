package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.grpc.niffler.grpc.CalculateRequest;
import guru.qa.grpc.niffler.grpc.CalculateResponse;
import guru.qa.grpc.niffler.grpc.Currency;
import guru.qa.grpc.niffler.grpc.CurrencyResponse;
import guru.qa.grpc.niffler.grpc.CurrencyValues;

import java.util.List;
import java.util.stream.Stream;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static guru.qa.grpc.niffler.grpc.CurrencyValues.*;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class NifflerCurrencyGrpcTest extends BaseGrpcTest {

    static Stream<Arguments> getAllCurrenciesTest() {
        return Stream.of(
                Arguments.of(RUB, 0.015),
                Arguments.of(KZT, 0.0021),
                Arguments.of(EUR, 1.08),
                Arguments.of(USD, 1.0)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "The currency {0} has a rate {1}")
    void getAllCurrenciesTest(CurrencyValues currency,
                              double expectedRate) {
        CurrencyResponse allCurrencies = step("Get all currencies", () ->
                currencyStub.getAllCurrencies(Empty.getDefaultInstance())
        );
        final List<Currency> currenciesList = allCurrencies.getAllCurrenciesList();

        step("Check that response contains the currency", () ->
                assertTrue(currenciesList.stream()
                        .map(Currency::getCurrency)
                        .anyMatch(c -> c == currency))
        );
        step("Check the currency rate", () -> {
            Currency foundCurrency = currenciesList.stream()
                    .filter(c -> c.getCurrency() == currency)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Currency not found in the response"));
            assertEquals(expectedRate, foundCurrency.getCurrencyRate());
        });
    }


    static Stream<Arguments> calculateRateTest() {
        return Stream.of(
                Arguments.of(USD, RUB, 10.0, 666.67),
                Arguments.of(USD, USD, 10.0, 10.0),
                Arguments.of(RUB, USD, 10.0, 0.15),
                Arguments.of(KZT, USD, 10.0, 0.02),
                Arguments.of(USD, KZT, 10.0, 4761.9),
                Arguments.of(KZT, EUR, 10.0, 0.02),
                Arguments.of(EUR, EUR, 10.0, 10.0),
                Arguments.of(EUR, USD, 10.0, 10.8),
                Arguments.of(EUR, RUB, 10.0, 720.0)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "When recalculating from {0} to {1} the sum of {2}, the result {3} should be returned")
    void calculateRateTest(CurrencyValues spendCurrency,
                           CurrencyValues desiredCurrency,
                           double amount,
                           double expected) {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();

        final CalculateResponse calculateResponse = step("Get all currencies", () ->
                currencyStub.calculateRate(request)
        );

        step("Check calculated rate", () ->
                assertEquals(expected, calculateResponse.getCalculatedAmount())
        );
    }

    static Stream<Arguments> calculateRateFroNegativeValuesTest() {
        return Stream.of(
                Arguments.of(USD, RUB, -10.0, -666.67),
                Arguments.of(USD, USD, -10.0, -10.0),
                Arguments.of(RUB, USD, -10.0, -0.15),
                Arguments.of(KZT, USD, -10.0, -0.02),
                Arguments.of(USD, KZT, -10.0, -4761.9),
                Arguments.of(KZT, EUR, -10.0, -0.02),
                Arguments.of(EUR, EUR, -10.0, -10.0),
                Arguments.of(EUR, USD, -10.0, -10.8),
                Arguments.of(EUR, RUB, -10.0, -720.0)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "When recalculating for negative values from {0} to {1} the sum of {2}," +
            " the result {3} should be returned")
    void calculateRateFroNegativeValuesTest(CurrencyValues spendCurrency,
                           CurrencyValues desiredCurrency,
                           double amount,
                           double expected) {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();

        final CalculateResponse calculateResponse = step("Get all currencies", () ->
                currencyStub.calculateRate(request)
        );

        step("Check calculated rate", () ->
                assertEquals(expected, calculateResponse.getCalculatedAmount())
        );
    }

    @Test
    void calculateRateWithUnspecifiedCurrencyTest() {
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(10.0)
                .setSpendCurrency(UNSPECIFIED)
                .setDesiredCurrency(RUB)
                .build();

        step("Check calculate rate with unspecified currency", () -> {
            assertThrows(StatusRuntimeException.class, () -> currencyStub.calculateRate(request));
        });
    }

    @Test
    void calculateRateZeroTest() {
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(0)
                .setSpendCurrency(USD)
                .setDesiredCurrency(RUB)
                .build();

        final CalculateResponse calculateResponse = step("Get all currencies", () ->
                currencyStub.calculateRate(request)
        );

        step("Check calculated rate", () ->
                assertEquals(0, calculateResponse.getCalculatedAmount())
        );
    }
}
