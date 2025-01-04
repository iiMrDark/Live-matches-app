package nemosofts.online.live.interfaces;

public interface RatingListener {
    void onStart();
    void onEnd(String success, String rateSuccess, String message, int rating);
}
