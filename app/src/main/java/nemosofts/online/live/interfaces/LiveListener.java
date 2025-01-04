package nemosofts.online.live.interfaces;

import java.util.ArrayList;

import nemosofts.online.live.item.ItemData;

public interface LiveListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemData> arrayListData);
}