package sierpinski.recurrence

import sierpinski.FractalImage

class SierpinskiRecurrence {

    FractalImage image

    SierpinskiRecurrence(FractalImage image) {
        this.image = image
    }

    void render() {
        draw(0, 0, image.size)
    }

    void renderThreaded() {
        List<Thread> threads = []

        int xOffset = 0
        int yOffset = 0

        int dividedLength = image.size / 3

        (xOffset + dividedLength..xOffset + 2 * dividedLength - 1).each { int x ->
            (yOffset + dividedLength..yOffset + 2 * dividedLength - 1).each { int y ->
               // image.setPixel(x, y, true)
            }
        }

        threads << Thread.start {
            draw(xOffset, yOffset, dividedLength)
            draw(xOffset + 2 * dividedLength, yOffset + dividedLength, dividedLength)

        }
        threads << Thread.start {
            draw(xOffset + dividedLength, yOffset, dividedLength)
            draw(xOffset, yOffset + 2 * dividedLength, dividedLength)

        }
        threads << Thread.start {
            draw(xOffset + 2 * dividedLength, yOffset, dividedLength)
            draw(xOffset + dividedLength, yOffset + 2 * dividedLength, dividedLength)

        }
        threads << Thread.start {
            draw(xOffset, yOffset + dividedLength, dividedLength)
            draw(xOffset + 2 * dividedLength, yOffset + 2 * dividedLength, dividedLength)

        }
//        threads << Thread.start {
//        }
//        threads << Thread.start {
//        }
//        threads << Thread.start {
//        }
//        threads << Thread.start {
//        }

        threads*.join()
    }

    void draw(int xOffset, int yOffset, int length) {
        if (length > 1) {
            if (length % 3 != 0) {
                throw new IllegalArgumentException("Cannot slice square, which length is not multiplication od 3")
            }

            int dividedLength = (int) (length / 3)

//            sierpinski.Timer.time("Drawing pixels") {
                (xOffset + dividedLength..xOffset + 2 * dividedLength - 1).each { int x ->
                    (yOffset + dividedLength..yOffset + 2 * dividedLength - 1).each { int y ->
//                        image.setPixel(x, y, true)
                    }
                }
//            }

            draw(xOffset, yOffset, dividedLength)
            draw(xOffset + dividedLength, yOffset, dividedLength)
            draw(xOffset + 2 * dividedLength, yOffset, dividedLength)

            draw(xOffset, yOffset + dividedLength, dividedLength)
            draw(xOffset + 2 * dividedLength, yOffset + dividedLength, dividedLength)

            draw(xOffset, yOffset + 2 * dividedLength, dividedLength)
            draw(xOffset + dividedLength, yOffset + 2 * dividedLength, dividedLength)
            draw(xOffset + 2 * dividedLength, yOffset + 2 * dividedLength, dividedLength)
        }
        // otherwise finish up - nothing to do here
    }

    public static void main(String[] args) {

        def image = new FractalImage(8)
        def recurence = new SierpinskiRecurrence(image)

        sierpinski.Timer.time("Sierpinski Recurrence") {
            recurence.render()
        }

//        image.write(new File("image-recurence.png"))

        image.clearImage()

        sierpinski.Timer.time("Sierpinski Recurrence Threaded") {
            recurence.renderThreaded()
        }

//        image.write(new File("image-recurence-threaded.png"))
    }

}
