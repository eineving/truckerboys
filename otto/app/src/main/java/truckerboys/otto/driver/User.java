package truckerboys.otto.driver;

import android.content.Context;

/**
 * Created by Martin on 17/09/2014.
 * This class is singleton.
 */
public class User {

    private SessionHistory history;
    private Session currentSession;

    public User(Context context){
        history = new SessionHistory(context);
    }

    /**
     * Starts a new Session.
     */
    public void startNewSession(SessionType type){
        if(currentSession != null && currentSession.isActive()){
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
