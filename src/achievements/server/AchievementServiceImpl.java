package achievements.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import achievements.client.AchievementService;
import achievements.shared.Medal;
import achievements.shared.Veteran;

@SuppressWarnings("serial")
public class AchievementServiceImpl extends RemoteServiceServlet implements AchievementService
{
  PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");

  @SuppressWarnings("unchecked")
  public List<Veteran> getAllVeterans(){

    ArrayList<Veteran> list = null;

    PersistenceManager pm = pmfInstance.getPersistenceManager();

    try{
      String query = "select from " + Veteran.class.getName();
      list = new ArrayList<Veteran>((Collection<Veteran>)pm.newQuery(query).execute());
    }
    finally{
      pm.close();
    }

    return list;
  }

  @SuppressWarnings("unchecked")
  public List<Medal> getAllMedals(){

    ArrayList<Medal> list = null;

    PersistenceManager pm = pmfInstance.getPersistenceManager();

    try{
      String query = "select from " + Medal.class.getName();
      list = new ArrayList<Medal>((Collection<Medal>)pm.newQuery(query).execute());
    }
    finally{
      pm.close();
    }

    return list;
  }

  /* Do this better.  Having a delete in here is just asking for it to escape into prod and wreak havok. */
  public String debug(){
    /*
        PersistenceManager pm = pmfInstance.getPersistenceManager();

        try{
            String query = "select from " + Veteran.class.getName();
            Collection c = (Collection)pm.newQuery(query).execute();
            pm.deletePersistentAll(c);

            query = "select from " + Medal.class.getName();
            c = (Collection)pm.newQuery(query).execute();
            pm.deletePersistentAll(c);

            Veteran test1 = new Veteran();
            test1.shortName = "Bob";
            test1.medals = "fired:this is star one:this is star two|nocnoc";

            Veteran test2 = new Veteran();
            test2.shortName = "Doug";
            test2.medals = "quit:a|guitar:a:b|pinball:a:b:c|tiki:a:b:c:d";

            pm.makePersistentAll(test1);            
            pm.makePersistentAll(test2);

            Medal[] medals = {createMedal("fired", "Fired", "you are fired"),
                              createMedal("tiki", "Tiki", "this is the tiki"),
                              createMedal("burningman", null, null),
                              createMedal("hate", null, null),
                              createMedal("christmas", null, null),
                              createMedal("dating", null, null),
                              createMedal("deck", null, null),
                              createMedal("pirate", null, null),
                              createMedal("laidoff", null, null),
                              createMedal("pinball", null, null),
                              createMedal("guitar", null, null),
                              createMedal("nocnoc", null, null),
                              createMedal("underwater", null, null),
                              createMedal("band", null, null),
                              createMedal("cardiac", null, null),
                              createMedal("sketchy", null, null),
                              createMedal("banjo", null, null),
                              createMedal("why", null, null),
                              createMedal("trojan", null, null),
                              createMedal("dnd", null, null),
                              createMedal("health", null, null),
                              createMedal("hotdog", null, null),
                              createMedal("ramp", null, null),
                              createMedal("dimsum", null, null),
                              createMedal("pto", null, null),
                              createMedal("acquired", null, null),
                              createMedal("maru", null, null),
                              createMedal("carousel", null, null),
                              createMedal("flood", null, null),
                              createMedal("obsolete", null, null),
                              createMedal("coffee", null, null),
                              createMedal("cry", null, null),
                              createMedal("garbage", null, null),
                              createMedal("raise", null, null),
                              createMedal("seconds", null, null),
                              createMedal("beta", null, null),
                              createMedal("sarbox", null, null),
                              createMedal("man", null, null),
                              createMedal("quit", null, null)};


            pm.makePersistentAll(medals);

            do{
                query = "select from " + Medal.class.getName();
                c = (Collection)pm.newQuery(query).execute();
            }
            while(c.size()<medals.length);
        }
        finally{
            pm.close();
        }
     */
    return null;
  }
}
