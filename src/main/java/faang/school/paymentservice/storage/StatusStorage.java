package faang.school.paymentservice.storage;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

@Slf4j
public abstract class StatusStorage<T> {
    private final Map<String, CompletableFuture<T>> futures = new ConcurrentHashMap<>();
    private static final long TIMEOUT = 20;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;

    public T awaitStatus(String operationId, BiFunction<String, String, T> functionError) {
        CompletableFuture<T> future = new CompletableFuture<>();

        futures.put(operationId, future);
        log.info("Wait response {}", operationId);
        try {
            return future.get(TIMEOUT, UNIT);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            return functionError.apply(e.getMessage(), operationId);
        } finally {
            cleanMaps(operationId);
        }
    }

    public void updateResponse(T responseDto, String operationId) {
        if (futures.containsKey(operationId)) {
            log.info("Response received {}", operationId);
            futures.get(operationId).complete(responseDto);
        }
    }

    private void cleanMaps(String operationId) {
        futures.remove(operationId);
    }
}
