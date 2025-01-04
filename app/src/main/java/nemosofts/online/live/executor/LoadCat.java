package nemosofts.online.live.executor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.interfaces.CategoryListener;
import nemosofts.online.live.item.ItemCat;
import nemosofts.online.live.utils.ApplicationUtil;
import nemosofts.online.live.utils.AsyncTaskExecutor;
import okhttp3.RequestBody;

public class LoadCat extends AsyncTaskExecutor<String, String, String> {

    private final RequestBody requestBody;
    private final CategoryListener catListener;
    private final ArrayList<ItemCat> arrayList = new ArrayList<>();
    private String verifyStatus = "0";
    private String message = "";

    public LoadCat(CategoryListener catListener, RequestBody requestBody) {
        this.catListener = catListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        catListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Callback.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (!obj.has(Callback.TAG_SUCCESS)) {
                    String id = obj.getString("cid");
                    String name = obj.getString("category_name");
                    String image = obj.getString("category_image").replace(" ", "%20");
                    if (image.isEmpty()) {
                        image = "null";
                    }
                    ItemCat objItem = new ItemCat(id, name, image);
                    arrayList.add(objItem);
                } else {
                    verifyStatus = obj.getString(Callback.TAG_SUCCESS);
                    message = obj.getString(Callback.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        catListener.onEnd(s, verifyStatus, message, arrayList);
    }
}