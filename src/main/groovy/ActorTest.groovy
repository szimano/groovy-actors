import groovyx.gpars.actor.Actor
import groovyx.gpars.actor.DefaultActor

class ActorTest {
    static final max = 100

    public static void main(String[] args) {
        def master = new GameMaster().start()
        def player = new Player(name: 'Player', server: master).start()
        def cleverPlayer = new CleverPlayer(name: 'Tomek', server: master).start()

        [master, player, cleverPlayer]*.join()
    }
}

class GameMaster extends DefaultActor {
    int secretNum

    void afterStart() {
        secretNum = new Random().nextInt(ActorTest.max)
    }

    void act() {
        loop {
            react { int num ->
                if (num > secretNum)
                    reply 'too large'
                else if (num < secretNum)
                    reply 'too small'
                else {
                    reply 'you win'
                }
            }
        }
    }
}

class Player extends DefaultActor {
    String name
    Actor server
    int myNum
    int round = 0

    void act() {
        loop {
            myNum = new Random().nextInt(ActorTest.max)
            server.send myNum
            react {
                switch (it) {
                    case 'too large':
                        //println "$name: $myNum was too large"
                        break
                    case 'too small':
                        //println "$name: $myNum was too small"
                        break
                    case 'you win':
                        println "$name round $round: I won $myNum"; terminate()
                }
            }

            round++
        }
    }
}

class CleverPlayer extends DefaultActor {
    String name
    Actor server
    int numMin = 0, numMax = ActorTest.max - 1
    int round = 0

    void act() {
        loop {
            int myNum = (numMax - numMin == 0) ? numMax : numMin + new Random().nextInt(numMax - numMin)
            server.send myNum
            react {
                switch (it) {
                    case 'too large':
//                        println "clever $name: $myNum was too large"
                        numMax = myNum - 1
                        break
                    case 'too small':
//                        println "clever $name: $myNum was too small"
                        numMin = myNum + 1
                        break
                    case 'you win':
                        println "clever $name round $round: I won $myNum"; terminate()
                }
            }

            round++
        }
    }
}