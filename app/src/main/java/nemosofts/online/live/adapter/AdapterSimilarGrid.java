package nemosofts.online.live.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.nemosofts.view.ImageHelperView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nemosofts.online.live.R;
import nemosofts.online.live.item.ItemData;
import nemosofts.online.live.utils.helper.SPHelper;

public class AdapterSimilarGrid extends RecyclerView.Adapter<AdapterSimilarGrid.ViewHolder> {

    private final RecyclerItemClickListener listener;
    private final List<ItemData> arrayList;
    public final Boolean isPurchases;

    public AdapterSimilarGrid(Context context, List<ItemData> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
        isPurchases = new SPHelper(context).getIsSubscribed();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_similar_g,parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ItemData itemPost = arrayList.get(position);

        Picasso.get()
                .load(arrayList.get(position).getThumb())
                .centerCrop()
                .resize(300,300)
                .placeholder(R.drawable.material_design_default)
                .error(R.drawable.material_design_default)
                .into(holder.logo);

        holder.title.setText(itemPost.getTitle());

        if (Boolean.TRUE.equals(isPurchases)){
            holder.premiumView.setVisibility(View.GONE);
        } else {
            holder.premiumView.setVisibility(arrayList.get(position).getIsPremium() ? View.VISIBLE : View.GONE);
        }
        holder.bind(itemPost, listener);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageHelperView logo;
        private final TextView title;
        private final TextView premiumView;

        public ViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.iv_similar_img);
            title = itemView.findViewById(R.id.tv_similar_text);
            premiumView = itemView.findViewById(R.id.tv_similar_pre);
        }

        public void bind(final ItemData itemPost, final RecyclerItemClickListener listener){
            itemView.setOnClickListener(v -> listener.onClickListener(itemPost, getLayoutPosition()));
        }
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemData itemPost, int position);
    }

}
