package nemosofts.online.live.interfaces;

public interface AboutListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message);
}