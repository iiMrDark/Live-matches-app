package nemosofts.online.live.executor;


import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.interfaces.LoginListener;
import nemosofts.online.live.utils.ApplicationUtil;
import nemosofts.online.live.utils.AsyncTaskExecutor;
import okhttp3.RequestBody;

public class LoadLogin extends AsyncTaskExecutor<String, String, String> {

    private final RequestBody requestBody;
    private final LoginListener listener;
    private String success = "0";
    private String message = "";
    private String userID="";
    private String userName = "";
    private String userGender = "";
    private String profile = "";
    private String userPhone = "";

    public LoadLogin(LoginListener listener, RequestBody requestBody) {
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
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                success = jsonObject.getString(Callback.TAG_SUCCESS);
                if(success.equals("1")) {
                    userID = jsonObject.getString("user_id");
                    userName = jsonObject.getString("user_name");
                    userPhone = jsonObject.getString("user_phone");
                    userGender = jsonObject.getString("user_gender");
                    profile = jsonObject.getString("profile_img");
                }
                message = jsonObject.getString(Callback.TAG_MSG);
            }
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onEnd(s, success, message, userID, userName, userGender, userPhone, profile);
    }
}