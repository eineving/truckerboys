package truckerboys.otto.data;

import org.joda.time.Duration;

/**
 * Created by Nilsson on 17/09/2014.
 */
public class TimeBank {
    private Duration session;
    private Duration day;
    private Duration week;

    private int tenhourdaysthisweek = 0;

    public TimeBank(){

    }

    public TimeBank(Duration s, Duration d, Duration w){
        this.session = s;
        this.day = d;
        this.week = w;
    }
}
