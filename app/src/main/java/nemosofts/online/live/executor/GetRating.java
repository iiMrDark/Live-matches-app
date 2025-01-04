package nemosofts.online.live.executor;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.interfaces.RatingListener;
import nemosofts.online.live.utils.ApplicationUtil;
import nemosofts.online.live.utils.AsyncTaskExecutor;
import okhttp3.RequestBody;

public class GetRating extends AsyncTaskExecutor<String, String, Boolean> {

    private String rate = "0";
    private String message = "";
    private final RatingListener ratingListener;
    private final RequestBody requestBody;

    public GetRating(RatingListener ratingListener, RequestBody requestBody) {
        this.ratingListener = ratingListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        ratingListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String strings) {
        String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
        try {
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(Callback.TAG_ROOT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                rate = c.getString("total_rate");
                message = c.getString("message");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean s) {
        ratingListener.onEnd(String.valueOf(s), "1",message, Integer.parseInt(rate));
    }
}
