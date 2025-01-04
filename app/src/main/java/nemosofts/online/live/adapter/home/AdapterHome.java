package nemosofts.online.live.adapter.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.nemosofts.view.enchanted.EnchantedViewPager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import nemosofts.online.live.R;
import nemosofts.online.live.activity.PostIDActivity;
import nemosofts.online.live.activity.VideoDetailsActivity;
import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.interfaces.InterAdListener;
import nemosofts.online.live.item.ItemPost;
import nemosofts.online.live.utils.helper.Helper;
import nemosofts.online.live.utils.helper.SPHelper;
import nemosofts.online.live.utils.recycler.RecyclerItemClickListener;

public class AdapterHome extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    Helper helper;
    SPHelper spHelper;
    List<ItemPost> arrayList;
    int clickPos = 0;

    private static final int VIEW_PROG = 0;
    private static final int VIEW_BANNER = 1;
    private static final int VIEW_EVENT = 2;
    private static final int VIEW_ADS = 3;

    Boolean ads = true;

    public AdapterHome(Context context, List<ItemPost> arrayList) {
        this.context = context;
        List<ItemPost> filteredList = new ArrayList<>();
        helper = new Helper(context, interAdListener);
        spHelper = new SPHelper(context);

        for (ItemPost item : arrayList) {
            if ("slider".equals(item.getType())) {
                filteredList.add(item);
                break;
            }
        }
        for (ItemPost item : arrayList) {
            if ("event".equals(item.getType())) {
                filteredList.add(item);
                break;
            }
        }
        for (ItemPost item : arrayList) {
            if ("ads".equals(item.getType())) {
                filteredList.add(item);
                break;
            }
        }

        this.arrayList = filteredList;
    }

    static class BannerHolder extends RecyclerView.ViewHolder {

        EnchantedViewPager enchantedViewPager;
        HomePagerAdapter homePagerAdapter;

        BannerHolder(View view) {
            super(view);
            enchantedViewPager = view.findViewById(R.id.viewPager_home);
            enchantedViewPager.useAlpha();
            enchantedViewPager.useScale();
            enchantedViewPager.setPageMargin(-5);
        }
    }

    class EventHolder extends RecyclerView.ViewHolder {

        RecyclerView rv;
        AdapterHomeEvent adapter;

        EventHolder(View view) {
            super(view);
            rv = view.findViewById(R.id.rv_home_cat);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            rv.setLayoutManager(linearLayoutManager);
            rv.setItemAnimator(new DefaultItemAnimator());
        }
    }

    static class LatestAds extends RecyclerView.ViewHolder {

        LinearLayout adView;

        LatestAds(View view) {
            super(view);
            adView = view.findViewById(R.id.ll_adView);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {

        @SuppressLint("StaticFieldLeak")
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_BANNER) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_home_ui_banner, parent, false);
            return new BannerHolder(itemView);
        }
        else if (viewType == VIEW_EVENT) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_home_ui_categories, parent, false);
            return new EventHolder(itemView);
        }
        else if (viewType == VIEW_ADS) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_baner_ad, parent, false);
            return new LatestAds(itemView);
        }
        else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_home_ui_categories, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof BannerHolder bannerHolder) {
            if (bannerHolder.homePagerAdapter == null) {
                bannerHolder.enchantedViewPager.setFocusable(false);
                bannerHolder.homePagerAdapter = new HomePagerAdapter(context,
                        arrayList.get(holder.getAbsoluteAdapterPosition()).getArrayListBanner());
                bannerHolder.enchantedViewPager.setAdapter(((BannerHolder) holder).homePagerAdapter);
                if (bannerHolder.homePagerAdapter.getCount() > 2) {
                    bannerHolder.enchantedViewPager.setCurrentItem(1);
                }
            }
        }
        else if (holder instanceof EventHolder eventHolder) {
            eventHolder.adapter = new AdapterHomeEvent(arrayList.get(holder.getAbsoluteAdapterPosition()).getArrayListEvent());
            eventHolder.rv.setAdapter(eventHolder.adapter);

            eventHolder.rv.addOnItemTouchListener(new RecyclerItemClickListener(context, (view, position1) -> {
                clickPos = holder.getAbsoluteAdapterPosition();
                helper.showInterAd(position1, context.getString(R.string.live_event));
            }));
        }
        else if (holder instanceof LatestAds latestAds) {
            if (Boolean.TRUE.equals(ads)){
                ads = false;
                helper.showBannerAd(latestAds.adView, Callback.PAGE_HOME);
            }
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public boolean isHeader(int position) {
        return arrayList.get(position) == null;
    }

    @Override
    public int getItemViewType(int position) {
        return switch (arrayList.get(position).getType()) {
            case "slider" -> VIEW_BANNER;
            case "event" -> VIEW_EVENT;
            case "ads" -> VIEW_ADS;
            default -> VIEW_PROG;
        };
    }

    InterAdListener interAdListener = (position, type) -> {
        if (type.equals(context.getString(R.string.live))) {
            Intent intent = new Intent(context, VideoDetailsActivity.class);
            intent.putExtra("post_id", arrayList.get(clickPos).getArrayListLive().get(position).getId());
            context.startActivity(intent);
        } else
            if (type.equals(context.getString(R.string.categories))){
            Intent intent = new Intent(context, PostIDActivity.class);
            intent.putExtra("page_type", context.getString(R.string.categories));
            intent.putExtra("id", arrayList.get(clickPos).getArrayListCategories().get(position).getId());
            intent.putExtra("name", arrayList.get(clickPos).getArrayListCategories().get(position).getName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        else if (type.equals(context.getString(R.string.live_event))) {
            Intent intent = new Intent(context, VideoDetailsActivity.class);
            intent.putExtra("post_id", arrayList.get(clickPos).getArrayListEvent().get(position).getPostId());
            context.startActivity(intent);
        }
    };
}