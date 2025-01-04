package nemosofts.online.live.executor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.interfaces.EventListener;
import nemosofts.online.live.item.ItemEvent;
import nemosofts.online.live.utils.ApplicationUtil;
import nemosofts.online.live.utils.AsyncTaskExecutor;
import okhttp3.RequestBody;

public class LoadEvent extends AsyncTaskExecutor<String, String, String> {

    private final EventListener listener;
    private final ArrayList<ItemEvent> arrayList = new ArrayList<>();
    private final RequestBody requestBody;
    private String verifyStatus = "0";
    private String message = "";

    public LoadEvent(EventListener listener, RequestBody requestBody) {
        this.listener = listener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected  String doInBackground(String strings)  {
        String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
        try {
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(Callback.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(Callback.TAG_SUCCESS)) {

                    String id = objJson.getString("id");
                    String postID = objJson.getString("post_id");
                    String title = objJson.getString("event_title");
                    String time = objJson.getString("event_time");
                    String date = objJson.getString("event_date");


                    String eventCheckLive = objJson.getString("eventCheckLive");
                    if (eventCheckLive.isEmpty()) {
                        eventCheckLive = "null";
                    }
                    String commentator = objJson.getString("commentator");
                    if (commentator.isEmpty()) {
                        commentator = "null";
                    }
                    String category = objJson.getString("category");
                    if (category.isEmpty()) {
                        category = "null";
                    }
                    String channel = objJson.getString("channel");
                    if (channel.isEmpty()) {
                        channel = "null";
                    }

                    String titleOne = objJson.getString("team_title_one");
                    String thumbOne = objJson.getString("team_one_thumbnail").replace(" ", "%20");
                    if (thumbOne.isEmpty()) {
                        thumbOne = "null";
                    }
                    String titleTwo = objJson.getString("team_title_two");
                    String thumbTwo = objJson.getString("team_two_thumbnail").replace(" ", "%20");
                    if (thumbTwo.isEmpty()) {
                        thumbTwo = "null";
                    }


                    ItemEvent objItem = new ItemEvent(id, postID, title, time, date, titleOne, thumbOne, titleTwo, thumbTwo, eventCheckLive, commentator, category, channel);
                    arrayList.add(objItem);

                } else {
                    verifyStatus = objJson.getString(Callback.TAG_SUCCESS);
                    message = objJson.getString(Callback.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s, verifyStatus, message, arrayList);
    }
}

