package truckerboys.otto.clock;

import org.joda.time.Duration;

/**
 * Created by Simon on 2014-09-25.
 */
public class RestStop {
    private Duration timeLeft;
    private String name;

    public RestStop(Duration tL, String name){
        timeLeft = tL;
        this.name = name;
    }

    public Duration getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(Duration nTL){
        timeLeft = nTL;
    }

    public String getName() {
        return name;
    }
}
