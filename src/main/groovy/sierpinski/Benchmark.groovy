package sierpinski

import sierpinski.recurrence.SierpinskiRecurrence
import sierpinski.recurrence.SierpinskiRecurrenceStatic

class Benchmark {

    static void test() {
        def IMAGE_SIZE = 8

        def image = new FractalImage(IMAGE_SIZE)
        def recurence = new SierpinskiRecurrence(image)

        Timer.time("Sierpinski Recurrence") {
            recurence.render()
        }

        image = new FractalImage(IMAGE_SIZE)
        def recurenceStatic = new SierpinskiRecurrenceStatic(image)

        Timer.time("Sierpinski Recurrence Static") {
            recurenceStatic.render()
        }

        //image.write(new File("image-recurence.png"))
    }

    public static void main(String[] args) {
        println "Heating up..."

        Timer.disable()

        // heat the vm up
        test()

        println "Starting the benchmark"

        // and make the proper tests
        Timer.enable()

        test()
    }
}
