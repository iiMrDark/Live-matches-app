package nemosofts.online.live.dialog;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;

import nemosofts.online.live.R;


public class Toasty {

    public static final int ERROR = 0;
    public static final int SUCCESS = 1;

    private Toasty() {
        throw new IllegalStateException("Utility class");
    }

    public static void makeText(Activity activity, String message) {
        makeText(activity, message, SUCCESS);
    }

    @SuppressLint("SetTextI18n")
    public static void makeText(Activity activity, String message, int toastType) {
        try {
            if (!activity.isFinishing()) { // Check if activity is not finishing
                Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_toast);
                dialog.findViewById(R.id.iv_toast_close).setOnClickListener(view -> dialog.dismiss());
                RelativeLayout toastBg = dialog.findViewById(R.id.ll_toast_bg);
                ImageView toastIcon = dialog.findViewById(R.id.iv_toast_icon);
                TextView toastTitle = dialog.findViewById(R.id.tv_toast_title);
                TextView toastMessage = dialog.findViewById(R.id.tv_toast_message);
                if (toastType == ERROR) {
                    toastTitle.setText("Error!");
                    toastIcon.setImageResource(R.drawable.ic_error_toast);
                    toastIcon.setBackgroundResource(R.drawable.toast_icon_error_bg);
                    toastBg.setBackgroundResource(R.drawable.toast_error_bg);
                } else {
                    toastTitle.setText("Success!");
                    toastIcon.setImageResource(R.drawable.ic_success_toast);
                    toastIcon.setBackgroundResource(R.drawable.toast_icon_success_bg);
                    toastBg.setBackgroundResource(R.drawable.toast_success_bg);
                }
                if (!message.isEmpty()){
                    toastMessage.setText(message);
                } else {
                    if (toastType == ERROR){
                        toastMessage.setText("This is a error message.");
                    } else {
                        toastMessage.setText("This is a success message.");
                    }
                }
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
                dialog.show();
                Window window = dialog.getWindow();
                window.setLayout(MATCH_PARENT, WRAP_CONTENT);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }, 1800);
            }
        } catch (Exception e) {
            Log.e("Toasty", "Error ", e);
        }
    }
}