package nemosofts.online.live.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ColorUtils;

import java.util.Objects;

import nemosofts.online.live.R;
import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.dialog.DialogUtil;

public class DialogActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.rl_splash).setBackgroundColor(ColorUtils.colorBg(this));

        String from = getIntent().getStringExtra("from");
        if (from == null){
            openMainActivity();
        } else if (from.equals(Callback.DIALOG_TYPE_UPDATE)){
            DialogUtil.upgradeDialog(this, this::openMainActivity);
        } else if (from.equals(Callback.DIALOG_TYPE_MAINTENANCE)){
            DialogUtil.maintenanceDialog(this);
        } else if (from.equals(Callback.DIALOG_TYPE_DEVELOPER)){
            DialogUtil.dModeDialog(this);
        } else if (from.equals(Callback.DIALOG_TYPE_VPN)){
            DialogUtil.vpnDialog(this);
        } else {
            openMainActivity();
        }
    }

    @Override
    public int setContentViewID() {
        return R.layout.activity_launcher;
    }

    private void openMainActivity() {
        Intent intent = new Intent(DialogActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}