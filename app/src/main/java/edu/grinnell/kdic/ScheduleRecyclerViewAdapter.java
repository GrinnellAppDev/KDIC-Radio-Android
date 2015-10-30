package edu.grinnell.kdic;

import android.content.Context;
import android.graphics.LinearGradient;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = ScheduleRecyclerViewAdapter.class.getSimpleName();
    private Context mContext;
    private Schedule mSchedule;

    // define view types
    public static final int SECTION_HEADER = 0;
    public static final int CARD = 1;
    public static final int DAY_SCHEDULE = 2;

    private ArrayList<ScheduleRecyclerItem> mContent;

    public ScheduleRecyclerViewAdapter(Context context) {
        mContext = context;
        mSchedule = new Schedule(context);
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
            case DAY_SCHEDULE:
                v = inflater.inflate(R.layout.rv_item_card, parent, false);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ScheduleRecyclerItem item = mContent.get(position);
        holder.title.setText(item.getS1());
        holder.subtitle.setText(item.getS2());

        if (item.getViewType() == CARD) {
            if (mSchedule.isFavorite(item.getS1()))
                holder.favorite.setImageResource(R.drawable.ic_favorite_white_24dp);
            else
                holder.favorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            holder.ll_favorite.setClickable(true);
            holder.ll_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSchedule.isFavorite(item.getS1())) {
                        mSchedule.removeFavorite(item.getS1());
                        Log.d(TAG, "Removed from Favorites");
                    } else {
                        mSchedule.setFavorite(item.getS1());
                        Log.d(TAG, "Added to Favorites");
                    }
                    notifyDataSetChanged();
                }
            });
            // holder.cardView.setCardBackgroundColor();
        } else if (item.getViewType() == DAY_SCHEDULE) {
            holder.subtitle.setVisibility(View.GONE);
            holder.favorite.setImageResource(R.drawable.ic_keyboard_arrow_right_white_24dp);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
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
                favorite = (ImageView) itemView.findViewById(R.id.iv_favorite);
                ll_favorite = (LinearLayout) itemView.findViewById(R.id.ll_favorite);
            }
        }
    }
}
