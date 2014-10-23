package truckerboys.otto.driver;

import android.content.Context;
import android.util.Log;

import org.joda.time.Instant;
import org.joda.time.Duration;


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

        for(Session s : historyDB.getAllSessions()){
            if(s.getStartTime().isBefore((new Instant()).minus(Duration.standardDays(31)))){
                historyDB.deleteSession(s); //Remove old sessions.
            }else{
                history.addSession(s);
            }
        }
        if(history.getSessions().size() == 0){
            Instant i = new Instant();
            history.addSession(new Session(SessionType.RESTING, i.minus(Duration.standardMinutes(45)),new Instant()));
            history.addSession(new Session(SessionType.DRIVING, i.minus(Duration.standardMinutes(225)),i.minus(Duration.standardMinutes(45))));
            history.addSession(new Session(SessionType.WORKING, i.minus(Duration.standardMinutes(285)),i.minus(Duration.standardMinutes(225))));
            history.addSession(new Session(SessionType.RESTING, i.minus(Duration.standardMinutes(1000)),i.minus(Duration.standardMinutes(285))));
            history.addSession(new Session(SessionType.DRIVING, i.minus(Duration.standardMinutes(1187)),i.minus(Duration.standardMinutes(1000))));
            Log.w("HISTORY", "ADDED DUMMY HISTORY");
        }

        if(history.getSessions().size() > 0 && history.getSessions().get(0).isActive()){
            currentSession = history.getSessions().get(0);
        }
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
