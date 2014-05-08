package sierpinski;

import java.io.File;
import java.util.Date;

public class SierpinskiJavaRecurrence {

    FractalImage image;

    public SierpinskiJavaRecurrence(FractalImage image) {
        this.image = image;
    }

    void render() {
        draw(0, 0, image.getSize());
    }

    void draw(int xOffset, int yOffset, int length) {
        if (length > 1) {
            if (length % 3 != 0) {
                throw new IllegalArgumentException("Cannot slice square, which length is not multiplication od 3");
            }

            int dividedLength = (int)(length / 3);

            for (int x = xOffset + dividedLength; x < xOffset + 2 * dividedLength; x++) {
                for (int y = yOffset + dividedLength; y < yOffset + 2 * dividedLength; y++) {
                    image.setPixel(x, y, true);
                }
            }

            draw(xOffset, yOffset, dividedLength);
            draw(xOffset + dividedLength, yOffset, dividedLength);
            draw(xOffset + 2 * dividedLength, yOffset, dividedLength);

            draw(xOffset, yOffset + dividedLength, dividedLength);
            draw(xOffset + 2 * dividedLength, yOffset  + dividedLength, dividedLength);

            draw(xOffset, yOffset + 2 * dividedLength, dividedLength);
            draw(xOffset + dividedLength, yOffset + 2 * dividedLength, dividedLength);
            draw(xOffset + 2 * dividedLength, yOffset + 2 * dividedLength, dividedLength);
        }
        // otherwise finish up - nothing to do here
    }

    public static void main(String[] args) {

        FractalImage image = new FractalImage(8);
        SierpinskiJavaRecurrence recurence = new SierpinskiJavaRecurrence(image);

        Date start = new Date();
        recurence.render();
        System.out.println("Done in "+(new Date().getTime() - start.getTime()));

        image.write(new File("image-recurence-java.png"));
    }
}
