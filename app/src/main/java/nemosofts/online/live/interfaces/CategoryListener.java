package nemosofts.online.live.interfaces;

import java.util.ArrayList;

import nemosofts.online.live.item.ItemCat;

public interface CategoryListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayListCat);
}