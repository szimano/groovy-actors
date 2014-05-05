package sierpinski.actors

import groovy.transform.CompileStatic
import groovyx.gpars.actor.DefaultActor
import sierpinski.FractalImage

@CompileStatic
class SierpinskiActorsStatic {

    FractalImage image

    SierpinskiActorsStatic(FractalImage image) {
        this.image = image
    }

    void render() {
        def slicing = new SlicingActor(image).start()

        slicing.join()
    }

    public static void main(String[] args) {
        def image = new FractalImage(7)
        def actors = new SierpinskiActorsStatic(image)

        sierpinski.Timer.time("Sierpinski Actors Static") {
            actors.render()
        }

        image.write(new File("image-actors-static.png"))
    }
}

@CompileStatic
class SlicingActorStatic extends DefaultActor {
    FractalImage image
    int workers = 1
    List<SquareCoordsStatic> tasks = []

    SlicingActorStatic(FractalImage image) {
        this.image = image

        tasks << new SquareCoordsStatic(0, 0, image.size)

        new DrawingActorStatic(this, image).start()
    }

    void act() {
        loop {
            react { message ->
                switch (message) {
                    case GiveMeSliceStatic:
                        if (tasks.size() == 0) {
                            reply DrawingActorStatic.TERMINATE

                            workers--

                            if (workers == 0) {
                                terminate()
                            }
                        } else {
                            def tasksToReturn = 10000

                            reply tasks.take(tasksToReturn)
                            this.tasks = tasks.drop(tasksToReturn)
                        }

                        break
                    case List:
                        List<SquareCoordsStatic> newCoords = (List<SquareCoordsStatic>)message

                        tasks.addAll(newCoords)

                        if (tasks.size() > 20 && workers < Runtime.getRuntime().availableProcessors() * 2) {
                            new DrawingActorStatic(this, image).start()
                            workers++
                        }

                        break
                }
                //println "Tasks outstanding: ${tasks.size()}; using ${workers} workers"
            }
        }
    }
}

@CompileStatic
class DrawingActorStatic extends DefaultActor {
    static final Object TERMINATE = new Object()

    FractalImage image
    SlicingActorStatic slicer

    DrawingActorStatic(SlicingActorStatic slicer, FractalImage image) {
        this.slicer = slicer
        this.image = image
    }

    void act() {
        loop {
            slicer << new GiveMeSlice()

            react { message ->
                switch (message) {
                    case List:
                        List<SquareCoordsStatic> reply = []

                        ((List<SquareCoordsStatic>) message).each { SquareCoordsStatic coords ->
                            coords.with {
//                            println "Working in ${this}"
                                if (length > 1) {
                                    int dividedLength = (int) (length / 3)

                                    (xOffset + dividedLength..xOffset + 2 * dividedLength - 1).each { int x ->
                                        (yOffset + dividedLength..yOffset + 2 * dividedLength - 1).each { int y ->
                                            image.setPixel(x, y, true)
                                        }
                                    }


                                    reply.add(new SquareCoordsStatic(xOffset, yOffset, dividedLength))
                                    reply.add(new SquareCoordsStatic(xOffset + dividedLength, yOffset, dividedLength))
                                    reply.add(new SquareCoordsStatic(xOffset + 2 * dividedLength, yOffset, dividedLength))

                                    reply.add(new SquareCoordsStatic(xOffset, yOffset + dividedLength, dividedLength))
                                    reply.add(new SquareCoordsStatic(xOffset + 2 * dividedLength, yOffset + dividedLength, dividedLength))

                                    reply.add(new SquareCoordsStatic(xOffset, yOffset + 2 * dividedLength, dividedLength))
                                    reply.add(new SquareCoordsStatic(xOffset + dividedLength, yOffset + 2 * dividedLength, dividedLength))
                                    reply.add(new SquareCoordsStatic(xOffset + 2 * dividedLength, yOffset + 2 * dividedLength, dividedLength))
                                }
//                            println "Finished in ${this}"
                            }
                        }

                        slicer << reply

                        break

                    case TERMINATE:
                        terminate()
                        break;
                }

            }
        }
    }
}

@CompileStatic
class GiveMeSliceStatic {}

@CompileStatic
class SquareCoordsStatic {
    int xOffset
    int yOffset
    int length

    SquareCoordsStatic(int xOffset, int yOffset, int length) {
        this.xOffset = xOffset
        this.yOffset = yOffset
        this.length = length
    }
}