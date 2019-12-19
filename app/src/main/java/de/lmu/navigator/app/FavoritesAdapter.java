package de.lmu.navigator.app;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.lmu.navigator.R;
import de.lmu.navigator.database.ModelHelper;
import de.lmu.navigator.database.model.Building;
import io.realm.RealmResults;

public class FavoritesAdapter extends BuildingsAdapter {

    public FavoritesAdapter(Context context, RealmResults<Building> items, boolean autoUpdate) {
        super(context, items, autoUpdate);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_favorite, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindBilduing(RecyclerView.ViewHolder vh, Building building) {
        FavoritesAdapter.ViewHolder holder = (FavoritesAdapter.ViewHolder) vh;
        holder.name.setText(ModelHelper.getName(building));

        int imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.image_size_favorite);
        Picasso.get()
               .load(ModelHelper.getThumbnailUrl(building))
               .resize(imageSize, imageSize)
               .centerCrop()
               .placeholder(getPlaceholderDrawable(building, imageSize))
               .into(holder.image);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        CircularImageView image;

        @BindView(R.id.name)
        TextView name;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
