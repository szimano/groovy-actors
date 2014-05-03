package sierpinski

import groovy.time.TimeCategory

class Timer {

    static private boolean enabled = true

    static void time(String id, Closure c) {
        Date start = new Date()

        c()

        if (enabled) println "$id took: " + TimeCategory.minus(new Date(), start)
    }

    static enable() {
        enabled = true
    }

    static disable() {
        enabled = false
    }
}
