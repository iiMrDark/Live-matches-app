package nemosofts.online.live.executor;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.interfaces.ProfileListener;
import nemosofts.online.live.utils.ApplicationUtil;
import nemosofts.online.live.utils.AsyncTaskExecutor;
import okhttp3.RequestBody;

public class LoadProfile extends AsyncTaskExecutor<String, String, String> {

    private final RequestBody requestBody;
    private final ProfileListener profileListener;
    private String success = "0";
    private String userId = "";
    private String userName = "";
    private String userEmail = "";
    private String userPhone = "";
    private String userGender = "";
    private String profileImage = "";

    public LoadProfile(ProfileListener profileListener, RequestBody requestBody) {
        this.profileListener = profileListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        profileListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                success = c.getString(Callback.TAG_SUCCESS);

                userId = c.getString("user_id");
                userName = c.getString("user_name");
                userEmail = c.getString("user_email");

                if (c.has("user_phone")){
                    userPhone = c.getString("user_phone");
                }
                if (c.has("user_gender")){
                    userGender = c.getString("user_gender");
                }
                if (c.has("profile_img")){
                    profileImage = c.getString("profile_img");
                }
            }
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        profileListener.onEnd(s, success, "", userId, userName, userEmail, userPhone,
                userGender, profileImage
        );
    }
}