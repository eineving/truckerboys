package truckerboys.otto.driver;

import android.content.Context;
import android.util.Log;

/**
 * Created by Martin on 17/09/2014.
 * This class is singleton.
 */
public class User {

    private SessionHistory history;
    private Session currentSession;

    private HistoryOpenHelper historyDB;

    public User(Context context){
        history = new SessionHistory();

        historyDB = new HistoryOpenHelper(context);
        int i = 0;
        for(Session s : historyDB.getAllSessions()){
            history.addSession(s);
            i++;
        }
        Log.w("SESSION", "LOADED " + history.getSessions().size()+ " past sessions. " + i);

        if(history.getSessions().size() > 0 && history.getSessions().get(0).isActive()){
            currentSession = history.getSessions().get(0);
            Log.w("SESSION", "CONTINUED ");
        }
    }

    /**
     * Starts a new Session.
     */
    public void startNewSession(SessionType type){
        if(currentSession != null && currentSession.isActive()){
            endSession();
        }
        Log.w("SESSIONS", "NEW SESSION STARTED");
        currentSession = new Session(type);
        history.addSession(currentSession);
        historyDB.addSession(currentSession);
    }

    /**
     * Ends the current session and adds the Session to the users history.
     */
    public void endSession(){
        currentSession.end();
        historyDB.overwriteSession(currentSession);
    }


    /**
     * Returns the users history of sessions.
     * @return The SessionHistory of the user.
     */
    public SessionHistory  getHistory(){
        return history;
    }

}
