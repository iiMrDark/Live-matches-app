package nemosofts.online.live.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import nemosofts.online.live.R;

public class NSoftsProgressDialog extends Dialog {

    public NSoftsProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_progress);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}