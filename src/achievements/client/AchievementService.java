package achievements.client;

import java.util.List;

import achievements.shared.Medal;
import achievements.shared.Veteran;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("achievements")
public interface AchievementService extends RemoteService
{
  List<Veteran> getAllVeterans();

  List<Medal> getAllMedals();

  String debug();
}
