package sierpinski

import sierpinski.actors.SierpinskiActors
import sierpinski.actors.SierpinskiActorsStatic
import sierpinski.recurrence.SierpinskiRecurrence
import sierpinski.recurrence.SierpinskiRecurrenceStatic

class Benchmark {

    static void test() {
        def IMAGE_SIZE = 6

        def image = new FractalImage(IMAGE_SIZE)
        def renderer = new SierpinskiRecurrence(image)

        Timer.time("Sierpinski Recurrence") {
            renderer.render()
        }

        image.clearImage()
        renderer = new SierpinskiRecurrenceStatic(image)

        Timer.time("Sierpinski Recurrence Static") {
            renderer.render()
        }

        image.clearImage()
        renderer = new SierpinskiActors(image)

        Timer.time("Sierpinski Actors") {
            renderer.render()
        }

        image.clearImage()
        renderer = new SierpinskiActorsStatic(image)

        Timer.time("Sierpinski Actors Static") {
            renderer.render()
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
