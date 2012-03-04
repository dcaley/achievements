package achievements.shared;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
@SuppressWarnings("serial")
public class Medal implements Serializable
{
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  public Long id;

  @Persistent
  public String shortName;

  @Persistent
  public String name;

  @Persistent
  public String description;

  @Override
  public String toString(){
    return shortName;
  }

  // needed for GWT serialization
  public Medal(){}
}
