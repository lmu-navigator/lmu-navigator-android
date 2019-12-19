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

public class AllBuildingsAdapter extends BuildingsAdapter {

    public AllBuildingsAdapter(Context context, RealmResults<Building> items, boolean autoUpdate) {
        super(context, items, autoUpdate);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_all, parent, false);
        return new ViewHolder(v);
    }

    @Override
    protected void onBindBilduing(RecyclerView.ViewHolder vh, Building building) {
        AllBuildingsAdapter.ViewHolder holder = (AllBuildingsAdapter.ViewHolder) vh;
        holder.city.setText(ModelHelper.getDescription(building));
        holder.street.setText(ModelHelper.getName(building));

        int imageSize = mContext.getResources().getDimensionPixelSize(R.dimen.image_size_all);
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

        @BindView(R.id.text_address1)
        TextView street;

        @BindView(R.id.text_address2)
        TextView city;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
