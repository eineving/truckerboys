package truckerboys.otto.driver;

import java.util.List;
import java.util.ArrayList;

import truckerboys.otto.data.TimeBank;

/**
 * Created by Martin on 17/09/2014.
 */
public class User {

    private List history = new ArrayList<Session>();
    private Session currentSession;

    public User(){

    }

    /**
     * Returns a list of past sessions.
     * @return The list of the users past sessions.
     */
    public List<Session> getHistory(){
        return history;
    }

}
