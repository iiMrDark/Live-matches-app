package nemosofts.online.live.interfaces;

import java.util.ArrayList;

import nemosofts.online.live.item.ItemData;
import nemosofts.online.live.item.ItemLiveTv;

public interface LiveIDListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemLiveTv> arrayListLive, ArrayList<ItemData> arrayListRelated);
}
