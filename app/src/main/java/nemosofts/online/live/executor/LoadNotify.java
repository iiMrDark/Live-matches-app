package nemosofts.online.live.executor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.interfaces.NotifyListener;
import nemosofts.online.live.item.ItemNotify;
import nemosofts.online.live.utils.ApplicationUtil;
import nemosofts.online.live.utils.AsyncTaskExecutor;
import okhttp3.RequestBody;

public class LoadNotify extends AsyncTaskExecutor<String, String, String> {

    private final NotifyListener listener;
    private final RequestBody requestBody;
    private final ArrayList<ItemNotify> arrayList = new ArrayList<>();

    public LoadNotify(NotifyListener listener, RequestBody requestBody) {
        this.listener = listener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray(Callback.TAG_ROOT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String id = obj.getString("id");
                String title = obj.getString("notification_title");
                String msg = obj.getString("notification_msg");
                String notificationOn = obj.getString("notification_on");

                ItemNotify item = new ItemNotify(id, title, msg, notificationOn);
                arrayList.add(item);
            }
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s, arrayList);
    }
}
