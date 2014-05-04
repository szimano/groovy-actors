package sierpinski

import groovy.transform.CompileStatic

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

@CompileStatic
class FractalImage {

    int size

    BufferedImage image

    FractalImage(int squareSize) {
        this.size = (int)Math.pow(3, squareSize)

        image = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_BINARY)
    }

    void clearImage() {
        (0..size -1).each {int x ->
            (0..size -1).each {int y ->
                image.setRGB(x, y, 0)
            }
        }
    }
    void setPixel(int x, int y, boolean value) {
        image.setRGB(x, y, value ? 0xFFFFFF : 0)
    }

    void write(File toFile) {
        toFile.withOutputStream { OutputStream it ->
            ImageIO.write(image, "PNG", it)
        }
    }
}
