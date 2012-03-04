package achievements.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
@SuppressWarnings("serial")
public class Veteran implements Serializable
{
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  public Long id;

  @Persistent
  public String shortName;

  @Persistent
  public String fullName;

  @Persistent
  public String medals;

  @NotPersistent
  public HashMap<Medal, Set<String>> awards = new HashMap<Medal, Set<String>>();

  @Override
  public String toString(){
    return shortName;
  }

  // needed for GWT serialization
  public Veteran(){}
}
