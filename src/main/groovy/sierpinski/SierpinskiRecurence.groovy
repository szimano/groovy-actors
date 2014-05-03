package sierpinski

class SierpinskiRecurence {

    FractalImage image

    SierpinskiRecurence(FractalImage image) {
        this.image = image
    }

    void render() {
        draw(0, 0, image.size)
    }

    void draw(int xOffset, int yOffset, int length) {
        if (length > 1) {
            if (length % 3 != 0) {
                throw new IllegalArgumentException("Cannot slice square, which length is not multiplication od 3")
            }

            int dividedLength = length / 3

            (xOffset + dividedLength  - 1.. xOffset + 2 * dividedLength - 1).each { x ->
                (yOffset + dividedLength - 1 .. yOffset + 2 * dividedLength - 1).each { y ->
                    image.setPixel(x, y, true)
                }
            }

            draw(xOffset, yOffset, dividedLength)
            draw(xOffset + dividedLength, yOffset, dividedLength)
            draw(xOffset + 2 * dividedLength, yOffset, dividedLength)

            draw(xOffset, yOffset + dividedLength, dividedLength)
            draw(xOffset + 2 * dividedLength, yOffset  + dividedLength, dividedLength)

            draw(xOffset, yOffset + 2 * dividedLength, dividedLength)
            draw(xOffset + dividedLength, yOffset + 2 * dividedLength, dividedLength)
            draw(xOffset + 2 * dividedLength, yOffset + 2 * dividedLength, dividedLength)
        }
        // otherwise finish up - nothing to do here
    }

    public static void main(String[] args) {
        def image = new FractalImage(7)
        def recurence = new SierpinskiRecurence(image)

        recurence.render()

        image.write(new File("image-recurence.png"))
    }
}
