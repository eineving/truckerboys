package truckerboys.otto.driver;

import java.util.List;
import java.util.ArrayList;

import truckerboys.otto.data.TimeBank;

/**
 * Created by Martin on 17/09/2014.
 */
public class User {

    private SessionHistory history = new SessionHistory();
    private Session currentSession;

    public User(){

    }

    /**
     * Starts a new Session.
     */
    public void startNewSession(SessionType type){
        if(currentSession.isActive()){
            endSession();
        }
        currentSession = new Session(type);
        history.addSession(currentSession);
    }

    /**
     * Ends the current session and adds the Session to the users history.
     */
    public void endSession(){
        currentSession.end();
    }


    /**
     * Returns the users history of sessions.
     * @return The SessionHistory of the user.
     */
    public SessionHistory  getHistory(){
        return history;
    }

}
