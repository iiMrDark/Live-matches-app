package nemosofts.online.live.fragment.player;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import nemosofts.online.live.R;
import nemosofts.online.live.activity.EmbeddedPlayerActivity;

public class EmbeddedImageFragment extends Fragment {

    private static final String TAG_URL = "streamUrl";

    @NonNull
    public static EmbeddedImageFragment newInstance(String streamUrl, String imageCover,
                                                    boolean isPlayVisible) {
        EmbeddedImageFragment f = new EmbeddedImageFragment();
        Bundle args = new Bundle();
        args.putString(TAG_URL, streamUrl);
        args.putString("imageCover", imageCover);
        args.putBoolean("isPlayVisible", isPlayVisible);
        f.setArguments(args);
        return f;
    }

    private String streamUrl;
    private String imageUrl;
    private boolean isPlayVisible;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_embedded_image, container, false);

        if (getArguments() != null) {
            streamUrl = getArguments().getString(TAG_URL);
            imageUrl = getArguments().getString("imageCover");
            isPlayVisible = getArguments().getBoolean("isPlayVisible");
        }

        ImageView imageCover = rootView.findViewById(R.id.imageCover);
        ImageView imagePlay = rootView.findViewById(R.id.imagePlay);

        imagePlay.setVisibility(isPlayVisible ? View.VISIBLE : View.GONE);

        Picasso.get().load(imageUrl).into(imageCover);

        imagePlay.setOnClickListener(v -> {
            if (!streamUrl.isEmpty()) {
                Intent intent = new Intent(getActivity(), EmbeddedPlayerActivity.class);
                intent.putExtra(TAG_URL, streamUrl);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), getString(R.string.stream_not_found), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
