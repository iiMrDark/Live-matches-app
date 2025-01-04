package nemosofts.online.live.adapter.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.nemosofts.view.enchanted.EnchantedViewPager;
import androidx.nemosofts.view.enchanted.EnchantedViewPagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.List;

import nemosofts.online.live.R;
import nemosofts.online.live.activity.PostIDActivity;
import nemosofts.online.live.interfaces.InterAdListener;
import nemosofts.online.live.item.ItemHomeSlider;
import nemosofts.online.live.utils.helper.Helper;
import nemosofts.online.live.utils.color.LoadColor;


public class HomePagerAdapter extends EnchantedViewPagerAdapter {

    private final Context mContext;
    private final LayoutInflater inflater;
    private final List<ItemHomeSlider> arrayList;
    private final Helper helper;

    public HomePagerAdapter(Context context, List<ItemHomeSlider> arrayList) {
        super(arrayList);
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.arrayList = arrayList;
        helper = new Helper(context, interAdListener);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View mCurrentView = inflater.inflate(R.layout.item_home_banner, container, false);

        TextView title = mCurrentView.findViewById(R.id.tv_home_banner);
        TextView desc = mCurrentView.findViewById(R.id.tv_home_banner_desc);
        ImageView banner = mCurrentView.findViewById(R.id.iv_home_banner);
        View gradient = mCurrentView.findViewById(R.id.view_home_banner);

        title.setText(arrayList.get(position).getTitle());
        desc.setText(arrayList.get(position).getInfo());

        Picasso.get()
                .load(arrayList.get(position).getImage())
                .placeholder(R.drawable.material_design_default)
                .error(R.drawable.material_design_default)
                .into(banner);

        new LoadColor(gradient).execute(arrayList.get(position).getImage());

        mCurrentView.setOnClickListener(v -> helper.showInterAd(position, ""));
        mCurrentView.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
        container.addView(mCurrentView);
        return mCurrentView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            Intent intent = new Intent(mContext, PostIDActivity.class);
            intent.putExtra("page_type", "banner");
            intent.putExtra("id", arrayList.get(position).getId());
            intent.putExtra("name", arrayList.get(position).getTitle());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    };
}