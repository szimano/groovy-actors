package sierpinski.actors

import groovyx.gpars.actor.DefaultActor
import groovyx.gpars.actor.impl.MessageStream
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
        def image = new FractalImage(8)
        def actors = new SierpinskiActors(image)

        sierpinski.Timer.time("Sierpinski Actors") {
            actors.render()
        }

        image.write(new File("image-actors.png"))
    }
}

class SlicingActor extends DefaultActor {
    FractalImage image
    List<MessageStream> workers = []
    List<SquareCoords> tasks = []

    int totalTasks = 1
    int totalMessagesReceived = 0

    int generator = 0

    int numberOfWorkers = 8

    SlicingActor(FractalImage image) {
        this.image = image

        tasks << new SquareCoords(0, 0, image.size)

        (0..numberOfWorkers - 1).each {
            new DrawingActor(this, image, generator++).start()
        }

    }

    void act() {
        loop {
            react { message ->
                totalMessagesReceived++

                switch (message) {
                    case GiveMeSlice:
                        workers << sender

                        if (tasks.size() == 0) {

                            if (workers.size() == numberOfWorkers) {
                                workers*.send(DrawingActor.TERMINATE)

                                terminate()
                                println "Total tasks processed: ${totalTasks}"
                                println "Total msg received ${totalMessagesReceived}"
                            }
                        } else {

                            while (!tasks.empty && !workers.empty) {
                                def tasksToReturn = Math.max((int) 1, (int) Math.min((int) 10000, (int) tasks.size() / workers.size()))

                                MessageStream worker = workers.remove(0)

                                worker.send(tasks.take(tasksToReturn))

                                this.tasks = tasks.drop(tasksToReturn)
                            }
                        }

                        break
                    case List:
                        List<SquareCoords> newCoords = (List) message

                        tasks.addAll(newCoords)

                        totalTasks += newCoords.size()

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
    int id

    DrawingActor(SlicingActor slicer, FractalImage image, int id) {
        this.slicer = slicer
        this.image = image
        this.id = id
    }

    void act() {
        loop {
            slicer << new GiveMeSlice()

            react { message ->
                switch (message) {
                    case List:
                        List<SquareCoords> reply = []

                        sierpinski.Timer.time("Actor ${id} processing ${message.size()} tasks") {
                            ((List<SquareCoords>) message).each { SquareCoords coords ->

                                if (coords.length > 1) {
                                    int dividedLength = (int) (coords.length / 3)

                                    sierpinski.Timer.time("Drawing pixels in ${id} for length ${coords.length}") {
                                        (coords.xOffset + dividedLength..coords.xOffset + 2 * dividedLength - 1).each { int x ->
                                            (coords.yOffset + dividedLength..coords.yOffset + 2 * dividedLength - 1).each { int y ->
                                                image.setPixel(x, y, true)
                                            }
                                        }
                                    }

                                    reply.add(new SquareCoords(coords.xOffset, coords.yOffset, dividedLength))
                                    reply.add(new SquareCoords(coords.xOffset + dividedLength, coords.yOffset, dividedLength))
                                    reply.add(new SquareCoords(coords.xOffset + 2 * dividedLength, coords.yOffset, dividedLength))

                                    reply.add(new SquareCoords(coords.xOffset, coords.yOffset + dividedLength, dividedLength))
                                    reply.add(new SquareCoords(coords.xOffset + 2 * dividedLength, coords.yOffset + dividedLength, dividedLength))

                                    reply.add(new SquareCoords(coords.xOffset, coords.yOffset + 2 * dividedLength, dividedLength))
                                    reply.add(new SquareCoords(coords.xOffset + dividedLength, coords.yOffset + 2 * dividedLength, dividedLength))
                                    reply.add(new SquareCoords(coords.xOffset + 2 * dividedLength, coords.yOffset + 2 * dividedLength, dividedLength))
                                }
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