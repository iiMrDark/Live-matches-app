package nemosofts.online.live.dialog;

import static android.view.WindowManager.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.WRAP_CONTENT;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

import nemosofts.online.live.R;
import nemosofts.online.live.callback.Method;
import nemosofts.online.live.executor.LoadStatus;
import nemosofts.online.live.interfaces.SuccessListener;
import nemosofts.online.live.utils.helper.Helper;
import nemosofts.online.live.utils.helper.SPHelper;
import nemosofts.online.live.view.NSoftsProgressDialog;

public class FeedBackDialog {

    private final Helper helper;
    private final SPHelper spHelper;
    private Dialog dialog;
    private final Activity ctx;
    private final NSoftsProgressDialog progressDialog;

    public FeedBackDialog(Activity ctx) {
        this.ctx = ctx;
        helper = new Helper(ctx);
        spHelper = new SPHelper(ctx);
        progressDialog = new NSoftsProgressDialog(ctx);
    }

    public void showDialog(String id, String title) {
        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_feed_back);
        EditText etMessages = dialog.findViewById(R.id.et_messages);
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(view -> dismissDialog());
        dialog.findViewById(R.id.iv_close).setOnClickListener(view -> dismissDialog());
        dialog.findViewById(R.id.tv_submit).setOnClickListener(view -> {
            if(etMessages.getText().toString().trim().isEmpty()) {
                etMessages.setError(ctx.getString(R.string.please_describe_the_problem));
                etMessages.requestFocus();
            } else {
                loadReportSubmit(etMessages.getText().toString(), id, title);
            }
        });
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialogAnimation;
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(MATCH_PARENT, WRAP_CONTENT);
    }

    private void loadReportSubmit(String reportMessages, String itemID, String reportTitle) {
        if (helper.isNetworkAvailable()) {
            LoadStatus loadFav = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String reportSuccess, String message) {
                    if (success.equals("1")) {
                        if (reportSuccess.equals("1")) {
                            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ctx, ctx.getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    }
                    dismissDialog();
                }
            }, helper.getAPIRequest(Method.METHOD_REPORT, 0, itemID, "", reportTitle,
                    reportMessages, spHelper.getUserId(), "", "", "", "",
                    "", "", "", null));
            loadFav.execute();
        } else {
            Toast.makeText(ctx, ctx.getString(R.string.err_internet_not_connected), Toast.LENGTH_SHORT).show();
        }
    }

    private void dismissDialog() {
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
