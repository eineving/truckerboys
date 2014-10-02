package truckerboys.otto.driver;

import java.util.List;
import java.util.ArrayList;

import truckerboys.otto.data.TimeBank;

/**
 * Created by Martin on 17/09/2014.
 * This class is singleton.
 */
public class User {

    private static User instance;
    private SessionHistory history = new SessionHistory();
    private Session currentSession;

    private User(){
    }


    public User getInstance() {
        if(instance == null) {
            instance = new User();
        }

        return instance;

    }

    /**
     * Starts a new Session.
     */
    public void startNewSession(){
        if(currentSession.isActive()){
            endSession();
        }
        currentSession = new Session();
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
