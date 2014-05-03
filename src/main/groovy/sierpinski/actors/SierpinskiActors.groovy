package sierpinski.actors

import groovyx.gpars.actor.DefaultActor
import sierpinski.FractalImage

class SierpinskiActors {

    FractalImage image

    SierpinskiActors(FractalImage image) {
        this.image = image
    }

    void render() {
        def slicing = new SlicingActor(image).start()

        slicing.join()
    }

    public static void main(String[] args) {
        def image = new FractalImage(2)
        def actors = new SierpinskiActors(image)

        sierpinski.Timer.time("Sierpinski Actors") {
            actors.render()
        }

        image.write(new File("image-actors.png"))
    }
}

class SlicingActor extends DefaultActor {
    FractalImage image
    int workers = 1
    List<SquareCoords> tasks = []

    SlicingActor(FractalImage image) {
        this.image = image

        tasks << new SquareCoords(0, 0, image.size)

        new DrawingActor(this, image).start()
    }

    void act() {
        loop {
            react { message ->
                switch (message) {
                    case GiveMeSlice:
                        if (tasks.size() == 0) {
                            reply DrawingActor.TERMINATE

                            workers--

                            if (workers == 0) {
                                terminate()
                            }
                        } else {
                            reply tasks.remove(0)
                        }

                        break
                    case SquareCoords:
                        tasks << message

                        if (tasks.size() > 20 && workers < Runtime.getRuntime().availableProcessors() - 1) {
                            new DrawingActor(this, image).start()
                            workers++
                        }

                        break
                }
                //println "Tasks outstanding: ${tasks.size()}; using ${workers} workers"
            }
        }
    }
}

class DrawingActor extends DefaultActor {
    static final Object TERMINATE = new Object()

    FractalImage image
    SlicingActor slicer

    DrawingActor(SlicingActor slicer, FractalImage image) {
        this.slicer = slicer
        this.image = image
    }

    void act() {
        loop {
            slicer << new GiveMeSlice()

            react { message ->
                switch (message) {
                    case SquareCoords:
                        ((SquareCoords)message).with {
                            if (length > 1) {
                                int dividedLength = (int) (length / 3)

                                (xOffset + dividedLength .. xOffset + 2 * dividedLength - 1).each { int x ->
                                    (yOffset + dividedLength ..yOffset + 2 * dividedLength - 1).each { int y ->
                                        image.setPixel(x, y, true)
                                    }
                                }

                                slicer.send(new SquareCoords(xOffset, yOffset, dividedLength))
                                slicer.send(new SquareCoords(xOffset + dividedLength, yOffset, dividedLength))
                                slicer.send(new SquareCoords(xOffset + 2 * dividedLength, yOffset, dividedLength))

                                slicer.send(new SquareCoords(xOffset, yOffset + dividedLength, dividedLength))
                                slicer.send(new SquareCoords(xOffset + 2 * dividedLength, yOffset + dividedLength, dividedLength))

                                slicer.send(new SquareCoords(xOffset, yOffset + 2 * dividedLength, dividedLength))
                                slicer.send(new SquareCoords(xOffset + dividedLength, yOffset + 2 * dividedLength, dividedLength))
                                slicer.send(new SquareCoords(xOffset + 2 * dividedLength, yOffset + 2 * dividedLength, dividedLength))
                            }
                        }
                        break

                    case TERMINATE:
                        terminate()
                        break;
                }

            }
        }
    }
}

class GiveMeSlice {}

class SquareCoords {
    int xOffset
    int yOffset
    int length

    SquareCoords(int xOffset, int yOffset, int length) {
        this.xOffset = xOffset
        this.yOffset = yOffset
        this.length = length
    }
}