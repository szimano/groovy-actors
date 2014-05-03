package sierpinski

import sierpinski.actors.SierpinskiActors
import sierpinski.actors.SierpinskiActorsStatic
import sierpinski.recurrence.SierpinskiRecurrence
import sierpinski.recurrence.SierpinskiRecurrenceStatic

class Benchmark {

    static void test() {
        def IMAGE_SIZE = 6

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

        image = new FractalImage(IMAGE_SIZE)
        def actors = new SierpinskiActors(image)

        Timer.time("Sierpinski Actors") {
            actors.render()
        }

        image = new FractalImage(IMAGE_SIZE)
        def actorsStatic = new SierpinskiActorsStatic(image)

        Timer.time("Sierpinski Actors Static") {
            actorsStatic.render()
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
