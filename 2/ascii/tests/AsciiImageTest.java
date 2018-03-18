import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.awt.image.BufferedImage;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AsciiImageTest {
    private AsciiImage asciiImage;
    private final int height = 3;
    private final int width = 3;
    private final double threshold = 0.01;
    private double[] content = new double[width * height];

    @BeforeEach
    void setUp() {
        for (int i = 0; i < width / 2; i++) {
            for (int j = 0; j < height; j++) {
                content[i * height + j] = 0.5;
            }
        }

        for (int j = 0; j < height; j++) {
            content[height * width / 2 + j - 1] = threshold;
        }

        for (int i = width / 2 + 1; i < height; i++) {
            for (int j = 0; j < height; j++) {
                content[i * height + j] = 0.5;
            }
        }
        this.asciiImage = new AsciiImage(content, width, height, null);
    }

    @AfterEach
    void tearDown() {
        this.asciiImage = null;
    }

    @Test
    void constructor() {
        AsciiImage newImage = new AsciiImage(content, 3, 3, new char[0]);
        assertArrayEquals(
                new double[]{0.5,0.5,0.5,0.01,0.01,0.01,0.5,0.5,0.5},
                (double[]) Whitebox.getInternalState(newImage, "content")
        );
    }

    @Test
    void updateImage() {
        int[] pixels = new int[this.width * this.height / 2];
        int len = this.width * this.height / 2;

        for (int i = 0; i < len; ++i) {
            int pixel = (int)(this.content[i] * 255);
            pixels[i] = pixel << 16 | pixel << 8 | pixel;
        }
        BufferedImage image = new BufferedImage(this.width, this.height / 2,
                BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, this.width, this.height / 2, pixels, 0, this.width);
        this.asciiImage.updateImage(image);

        assertEquals(height / 2, asciiImage.getHeight());
        assertEquals(width, asciiImage.getWidth());
    }

    @Test
    void setPalette() {
        char[] palette = new char[]{' ', '.', '*', '#'};
        this.asciiImage.setPalette(palette);
        assertEquals(palette, Whitebox.getInternalState(this.asciiImage, "ascii_palette"));
    }

    @Test
    void getAsciiImage() {
        String expectedResult = "lll\nMMM\nlll\n";
        assertEquals(expectedResult, this.asciiImage.getAsciiImage());
    }

    @Test
    void getWidth() {
        assertEquals(width, this.asciiImage.getWidth());
    }

    @Test
    void getHeight() {
        assertEquals(height, this.asciiImage.getHeight());
    }

    @Test
    void getRectangle() {
        double[] cont = (double[]) Whitebox.getInternalState(
                this.asciiImage.getRectangle(0, 0, 1, 1), "content"
        );
        assertArrayEquals(new double[]{0.5}, cont);
    }

    @Test
    void invert() {
        double[] invertedContent = new double[width * height];
        for (int i = 0; i < content.length; i++) {
            invertedContent[i] = 1 - content[i];
        }
        AsciiImage newImage = this.asciiImage.invert();
        double[] cont = (double[]) Whitebox.getInternalState(newImage, "content");
        assertArrayEquals(invertedContent, cont);
    }

    @Test
    void splitByThreshold() {
        Vector<AsciiImage> vAsciiImage = this.asciiImage.splitByThreshold(threshold);
        assertEquals(3, vAsciiImage.size());

        double[] cont0 = new double[0];
        assertEquals(0, (int) Whitebox.getInternalState(vAsciiImage.get(0), "height"));
        assertEquals(3, (int) Whitebox.getInternalState(vAsciiImage.get(0), "width"));
        assertArrayEquals(cont0, (double[]) Whitebox.getInternalState(vAsciiImage.get(0), "content"));

        double[] cont1 = new double[]{0.5, 0.5, 0.5, 0.01, 0.01, 0.01};
        assertEquals(2, (int) Whitebox.getInternalState(vAsciiImage.get(1), "height"));
        assertEquals(3, (int) Whitebox.getInternalState(vAsciiImage.get(1), "width"));
        assertArrayEquals(cont1, (double[]) Whitebox.getInternalState(vAsciiImage.get(1), "content"));

        double[] cont2 = new double[]{0.5, 0.5, 0.5};
        assertEquals(1, (int) Whitebox.getInternalState(vAsciiImage.get(2), "height"));
        assertEquals(3, (int) Whitebox.getInternalState(vAsciiImage.get(2), "width"));
        assertArrayEquals(cont2, (double[]) Whitebox.getInternalState(vAsciiImage.get(2), "content"));
    }

    @Test
    void resize() {
        AsciiImage resizedImage = this.asciiImage.resize(1, 1);
        assertEquals(1, (int) Whitebox.getInternalState(resizedImage, "height"));
        assertEquals(1, (int) Whitebox.getInternalState(resizedImage, "width"));
        assertArrayEquals(new double[]{0.5}, (double[]) Whitebox.getInternalState(resizedImage, "content"));
    }

    @Test
    void scale() {
        AsciiImage resizedImage = this.asciiImage.scale(1. / width);
        assertEquals(1, (int) Whitebox.getInternalState(resizedImage, "height"));
        assertEquals(1, (int) Whitebox.getInternalState(resizedImage, "width"));
        assertArrayEquals(new double[]{0.5}, (double[]) Whitebox.getInternalState(resizedImage, "content"));
    }

    @Test
    void convertToImage() {
        BufferedImage image = this.asciiImage.convertToImage();
        assertEquals(height, image.getHeight());
        assertEquals(width, image.getWidth());
    }

    @Test
    void distance() {
        double[] emptyImageContent = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        AsciiImage emptyImage = new AsciiImage(emptyImageContent, 3, 3, new char[0]);
        assertEquals(1.5003, AsciiImage.distance(this.asciiImage, emptyImage));
    }
}