package nemosofts.online.live.interfaces;

import java.util.ArrayList;

import nemosofts.online.live.item.ItemNotify;


public interface NotifyListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemNotify> notificationArrayList);
}