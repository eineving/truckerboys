package truckerboys.otto.planner;

import org.joda.time.Duration;

/**
 * Created by Andreas Pegelow on 2014-09-22.
 */
public class TimeLeft {
   private Duration timeLeft, extendedTime;

    public TimeLeft(Duration timeLeft, Duration supplementTime) {
        if(timeLeft.isShorterThan(Duration.ZERO)){
            this.timeLeft = Duration.ZERO;
        } else {
            this.timeLeft = timeLeft;
        }

        if(supplementTime.isShorterThan(Duration.ZERO)){
            this.extendedTime = Duration.ZERO;
        } else {
            this.extendedTime = supplementTime;
        }

    }

    /**
     * Returns the remaining time in a normal scenario.
     *
     * @return The remaining time.
     */
    public Duration getTimeLeft() {
        return timeLeft;
    }

    /**
     * Returns the remaining time of the extended time.
     * This is applied when regulations allow to extend a driving session.
     *
     * @return The remaining extended time.
     */
    public Duration getExtendedTimeLeft() {
        return extendedTime;
    }
}
