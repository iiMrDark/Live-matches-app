package nemosofts.online.live.interfaces;

import java.util.ArrayList;

import nemosofts.online.live.item.ItemPost;

public interface HomeListener {
    void onStart();
    void onEnd(String success, String message, ArrayList<ItemPost> arrayListPost);
}
