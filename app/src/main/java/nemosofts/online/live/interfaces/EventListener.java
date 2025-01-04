package nemosofts.online.live.interfaces;

import java.util.ArrayList;

import nemosofts.online.live.item.ItemEvent;

public interface EventListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemEvent> arrayListData);
}