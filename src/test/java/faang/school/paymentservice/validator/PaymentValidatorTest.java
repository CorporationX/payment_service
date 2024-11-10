package faang.school.paymentservice.validator;

import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.payment.PaymentRepository;
import faang.school.paymentservice.validator.payment.PaymentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentValidatorTest {

    private static final String UNIQUE_IDEMPOTENCY_KEY = "unique-key";
    private static final String EXISTING_IDEMPOTENCY_KEY = "existing-key";
    private static final UUID SOURCE_ACCOUNT_ID = UUID.randomUUID();
    private static final UUID TARGET_ACCOUNT_ID = UUID.randomUUID();

    @InjectMocks
    private PaymentValidator paymentValidator;

    @Mock
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setSourceAccountId(SOURCE_ACCOUNT_ID);
        payment.setTargetAccountId(TARGET_ACCOUNT_ID);
    }

    @Nested
    @DisplayName("Idempotency Key Validation Tests")
    class ValidateIdempotencyKeyTests {

        @Test
        @DisplayName("Success when idempotency key is unique")
        void whenIdempotencyKeyIsUnique_thenNoException() {
            Mockito.when(paymentRepository.findByIdempotencyKey(UNIQUE_IDEMPOTENCY_KEY)).thenReturn(Optional.empty());

            assertDoesNotThrow(() -> paymentValidator.validateIdempotencyKeyIsUnique(UNIQUE_IDEMPOTENCY_KEY));
        }

        @Test
        @DisplayName("Error when idempotency key is null")
        void whenIdempotencyKeyIsNull_thenThrowException() {
            DataValidationException exception = assertThrows(
                    DataValidationException.class,
                    () -> paymentValidator.validateIdempotencyKeyIsUnique(null)
            );

            assertEquals("Idempotency key cannot be null.", exception.getMessage());
        }

        @Test
        @DisplayName("Error when idempotency key already exists")
        void whenIdempotencyKeyExists_thenThrowException() {
            Mockito.when(paymentRepository.findByIdempotencyKey(EXISTING_IDEMPOTENCY_KEY)).thenReturn(Optional.of(payment));

            DataValidationException exception = assertThrows(
                    DataValidationException.class,
                    () -> paymentValidator.validateIdempotencyKeyIsUnique(EXISTING_IDEMPOTENCY_KEY)
            );

            assertEquals("Payment with idempotency key 'existing-key' already exists.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Different Accounts Validation Tests")
    class ValidateDifferentAccountsTests {

        @Test
        @DisplayName("Success when source and target accounts are different")
        void whenSourceAndTargetAccountsAreDifferent_thenNoException() {
            assertDoesNotThrow(() -> paymentValidator.validateDifferentAccounts(payment));
        }

        @Test
        @DisplayName("Error when source and target accounts are the same")
        void whenSourceAndTargetAccountsAreSame_thenThrowException() {
            payment.setTargetAccountId(SOURCE_ACCOUNT_ID);

            DataValidationException exception = assertThrows(
                    DataValidationException.class,
                    () -> paymentValidator.validateDifferentAccounts(payment)
            );

            assertEquals("Source and target account IDs cannot be the same.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Update Eligibility Validation Tests")
    class ValidateStatusForUpdateEligibilityTests {

        @Test
        @DisplayName("Success when payment status is updatable")
        void whenPaymentStatusIsUpdatable_thenNoException() {
            payment.setStatus(PaymentStatus.AUTH_SUCCESS);

            assertDoesNotThrow(() -> paymentValidator.validateStatusForUpdateEligibility(payment));
        }

        @Test
        @DisplayName("Error when payment status is not updatable")
        void whenPaymentStatusIsNotUpdatable_thenThrowException() {
            payment.setStatus(PaymentStatus.CANCEL_PENDING);

            DataValidationException exception = assertThrows(
                    DataValidationException.class,
                    () -> paymentValidator.validateStatusForUpdateEligibility(payment)
            );

            assertEquals("Payments with status 'CANCEL_PENDING' cannot be updated.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Cancelable Status Validation Tests")
    class ValidateCancelableStatusTests {

        @Test
        @DisplayName("Success when payment status is AUTH_SUCCESS")
        void whenPaymentStatusIsAuthSuccess_thenNoException() {
            payment.setStatus(PaymentStatus.AUTH_SUCCESS);

            assertDoesNotThrow(() -> paymentValidator.validateCancelableStatus(payment));
        }

        @Test
        @DisplayName("Error when payment status is not AUTH_SUCCESS")
        void whenPaymentStatusIsNotAuthSuccess_thenThrowException() {
            payment.setStatus(PaymentStatus.CONFIRM_PENDING);

            DataValidationException exception = assertThrows(
                    DataValidationException.class,
                    () -> paymentValidator.validateCancelableStatus(payment)
            );

            assertEquals("Only payments with 'AUTH_SUCCESS' status can be canceled.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Confirmable Status Validation Tests")
    class ValidateConfirmableStatusTests {

        @Test
        @DisplayName("Success when payment status is AUTH_SUCCESS")
        void whenPaymentStatusIsAuthSuccess_thenNoException() {
            payment.setStatus(PaymentStatus.AUTH_SUCCESS);

            assertDoesNotThrow(() -> paymentValidator.validateConfirmableStatus(payment));
        }

        @Test
        @DisplayName("Error when payment status is not AUTH_SUCCESS")
        void whenPaymentStatusIsNotAuthSuccess_thenThrowException() {
            payment.setStatus(PaymentStatus.CANCEL_PENDING);

            DataValidationException exception = assertThrows(
                    DataValidationException.class,
                    () -> paymentValidator.validateConfirmableStatus(payment)
            );

            assertEquals("Only payments with 'AUTH_SUCCESS' status can be confirmed.", exception.getMessage());
        }
    }
}
