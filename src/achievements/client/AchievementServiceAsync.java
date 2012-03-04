package achievements.client;

import java.util.List;

import achievements.shared.Medal;
import achievements.shared.Veteran;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AchievementServiceAsync
{
  void getAllVeterans(AsyncCallback<List<Veteran>> callback);

  void getAllMedals(AsyncCallback<List<Medal>> callback);

  void debug(AsyncCallback<String> callback);
}
