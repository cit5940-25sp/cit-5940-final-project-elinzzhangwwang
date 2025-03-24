import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldTest {

    @Test
    public void testSayHello() {
        assertEquals("Hello, World!", HelloWorld.sayHello());
    }

    @Test
    public void testNothing() {
        assertEquals(3, 4);
    }

    @Test
    public void testNothing2() {
        assertEquals(2, 3);
    }
}
