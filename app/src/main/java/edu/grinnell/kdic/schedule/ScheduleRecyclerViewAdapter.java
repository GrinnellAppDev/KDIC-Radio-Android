package edu.grinnell.kdic.schedule;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import edu.grinnell.kdic.Constants;
import edu.grinnell.kdic.Favorites;
import edu.grinnell.kdic.R;


public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = ScheduleRecyclerViewAdapter.class.getSimpleName();
    private FragmentActivity mContext;
    private Schedule mSchedule;
    private Favorites mFavorites;

    // define view types
    public static final int SECTION_HEADER = 0;
    public static final int CARD = 1;
    public static final int DAY_SCHEDULE = 2;

    private ArrayList<ScheduleRecyclerItem> mContent;

    public ScheduleRecyclerViewAdapter(FragmentActivity context) {
        mContext = context;
        mSchedule = new Schedule(context);
        mFavorites = new Favorites(context);
    }

    public void addContent(ArrayList<ScheduleRecyclerItem> content) {
        mContent = content;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case SECTION_HEADER:
                v = inflater.inflate(R.layout.rv_item_header, parent, false);
                break;
            case CARD:
                v = inflater.inflate(R.layout.rv_item_card, parent, false);
                break;
            case DAY_SCHEDULE:
                v = inflater.inflate(R.layout.rv_item_day_card, parent, false);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScheduleRecyclerItem item = mContent.get(position);

        switch (item.getViewType()) {
            case CARD:
                bindCard(holder, item);
                break;
            case DAY_SCHEDULE:
                bindDaySchedule(holder, item);
                break;
        }
    }

    public void bindCard(ViewHolder holder, final ScheduleRecyclerItem item) {
        holder.title.setText(item.getS1());
        holder.subtitle.setText(item.getS2());
        if (mFavorites.isFavorite(item.getS1()))
            holder.favorite.setImageResource(R.drawable.ic_favorite_white_24dp);
        else
            holder.favorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        holder.ll_favorite.setClickable(true);
        holder.ll_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavorites.isFavorite(item.getS1())) {
                    mFavorites.removeFavorite(item.getS1());
                    Log.d(TAG, "Removed from Favorites");
                } else {
                    mFavorites.addFavorites(item.getS1());
                    Log.d(TAG, "Added to Favorites");
                }
                notifyDataSetChanged();
            }
        });

    }

    public void bindDaySchedule(final ViewHolder holder, final ScheduleRecyclerItem item) {
        holder.title.setText(item.getS1());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DayScheduleFragment fragment = new DayScheduleFragment();
                Bundle args = new Bundle();
                args.putString(Constants.DAY, item.getS1());
                fragment.setArguments(args);
                mContext.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right)
                        .replace(R.id.fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mContent.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        TextView subtitle;
        ImageView favorite;
        LinearLayout ll_favorite;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            subtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            if (viewType == CARD || viewType == DAY_SCHEDULE) {
                cardView = (CardView) itemView.findViewById(R.id.card_view_item);
                if (viewType == CARD) {
                    favorite = (ImageView) itemView.findViewById(R.id.iv_favorite);
                    ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
                }
            }
        }
    }
}
