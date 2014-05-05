package sierpinski

import groovy.transform.CompileStatic

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@CompileStatic
class FractalImage {

    int size

    boolean[][] backingArray

    FractalImage(int squareSize) {
        this.size = (int)Math.pow(3, squareSize)

        backingArray = new boolean[size][size]
    }

    void clearImage() {
        (0..size -1).each {int x ->
            (0..size -1).each {int y ->
                backingArray[x][y] = false
            }
        }
    }
    void setPixel(int x, int y, boolean value) {
        backingArray[x][y] = value
    }

    void write(File toFile) {
        toFile.withOutputStream {OutputStream it ->
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_BINARY)

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    image.setRGB(i, j, backingArray[i][j] ? 0xFFFFFF : 0)
                }
            }

            ImageIO.write(image, "PNG", it)
        }
    }

    public static void main(String[] args) {
        FractalImage image = new FractalImage(8)

        image.write(new File("image.png"))
    }
}
