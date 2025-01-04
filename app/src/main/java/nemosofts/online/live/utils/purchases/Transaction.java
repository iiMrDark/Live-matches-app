package nemosofts.online.live.utils.purchases;

import android.app.Activity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import nemosofts.online.live.R;
import nemosofts.online.live.callback.Method;
import nemosofts.online.live.dialog.Toasty;
import nemosofts.online.live.executor.LoadStatus;
import nemosofts.online.live.interfaces.SuccessListener;
import nemosofts.online.live.utils.helper.Helper;
import nemosofts.online.live.utils.helper.SPHelper;
import nemosofts.online.live.view.NSoftsProgressDialog;


public class Transaction {

    private final NSoftsProgressDialog pDialog;
    private final Activity mContext;
    private final Helper helper;

    public Transaction(Activity context) {
        this.mContext = context;
        helper = new Helper(mContext);
        pDialog = new NSoftsProgressDialog(mContext);
    }

    public void purchasedItem(String planId, String planName, String planPrice, String planDuration, String planCurrencyCode) {
        if (helper.isNetworkAvailable()) {
            LoadStatus transaction = new LoadStatus(new SuccessListener() {
                @Override
                public void onStart() {
                    pDialog.show();
                }

                @Override
                public void onEnd(String success, String status, String message) {
                    dismissProgressDialog();
                    if (success.equals("1")) {
                        if (status.equals("1")) {
                            new SPHelper(mContext).setIsSubscribed(true);
                            ActivityCompat.recreate(mContext);
                        }
                        Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.makeText(mContext,mContext.getString(R.string.err_server_not_connected), Toasty.ERROR);
                    }
                }
            }, helper.getAPIRequest(Method.TRANSACTION_URL, 0, planId, planName, planPrice,
                    planDuration, new SPHelper(mContext).getUserId(), planCurrencyCode, "",
                    "", "", "", "", "", null));
            transaction.execute();
        } else {
            Toasty.makeText(mContext,mContext.getString(R.string.err_internet_not_connected), Toasty.ERROR);
        }
    }

    private void dismissProgressDialog() {
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
}
