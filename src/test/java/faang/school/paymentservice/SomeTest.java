package faang.school.paymentservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SomeTest {

    @Test
    public void primaryTest() {
        String hello = "Hello World";
        assertEquals(hello, "Hello World");
    }

    @Test
    public void secondaryTest() {
        assertEquals(2, 1 + 3);
    }
}