import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PredictorTest {
    private AsciiImage asciiImage0, asciiImage1;
    private Predictor predictor = new Predictor();
    private Map<Integer, AsciiImage> map = new HashMap<>();

    @BeforeEach
    void setUp() {
        double[] zeroContent = new double[]{1, 1, 1, 1, 0, 1, 1, 1, 1};
        AsciiImage zeroImage = new AsciiImage(zeroContent, 3, 3, new char[0]);
        map.put(0, zeroImage);

        double[] oneContent = new double[]{0, 1, 0, 0, 1, 0, 0, 1, 0};
        AsciiImage oneImage = new AsciiImage(oneContent, 3, 3, new char[0]);
        map.put(1, oneImage);

        asciiImage0 = new AsciiImage(new double[]{0.7, 1, 0.7, 1, 0.1, 1, 0.7, 1, 1}, 3, 3, new char[0]);
        asciiImage1 = new AsciiImage(new double[]{0, 0.9, 0.4, 0, 1, 0.2, 0, 1, 0}, 3, 3, new char[0]);
    }

    @AfterEach
    void tearDown() {
        predictor = new Predictor();
        asciiImage0 = null;
        asciiImage1 = null;
    }

    @Test
    void updateMap() {
        predictor.updateMap(map);
        assertEquals(
            map.keySet(),
            ((Map<Integer, AsciiImage>)Whitebox.getInternalState(predictor, "map")).keySet()
        );
    }

    @Test
    void predict() {
        predictor.updateMap(map);
        assertEquals(0, predictor.predict(asciiImage0));
        assertEquals(1, predictor.predict(asciiImage1));
    }
}