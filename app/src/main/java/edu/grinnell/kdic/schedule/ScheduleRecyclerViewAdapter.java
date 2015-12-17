package edu.grinnell.kdic.schedule;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
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
    private int animatePos;

    // define view types
    public static final int SECTION_HEADER = 0; // section header card
    public static final int CARD = 1;           // card with show info
    public static final int DAY_SCHEDULE = 2;   // card for day
    public static final int CARD_NO_FAV = 3;    // card for Auto-Play

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
            case CARD_NO_FAV:
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
            case CARD_NO_FAV:
                bindCard(holder, item);
                break;
            case DAY_SCHEDULE:
                bindDaySchedule(holder, item);
                break;
            case SECTION_HEADER:
                bindSectionHeader(holder, item);
                break;
        }
        //animateCard(holder.itemView, position);
    }

    private void animateCard(View v, int position) {
        if (position > animatePos) {
            Animation cardAnimation = AnimationUtils.loadAnimation(mContext, R.anim.scale_card_up);
            v.startAnimation(cardAnimation);
            animatePos = position;
        }
    }

    private void bindSectionHeader(ViewHolder holder, ScheduleRecyclerItem item) {
        holder.title.setText(item.getS1());
        holder.subtitle.setText(item.getS2());
    }

    public void bindCard(final ViewHolder holder, final ScheduleRecyclerItem item) {
        holder.title.setText(item.getS1());
        holder.subtitle.setText(item.getS2());

        if (holder.viewType == CARD_NO_FAV)
            holder.favorite.setVisibility(View.GONE);  // remove the favorite button for Auto Play card
        else {
            holder.favorite.setVisibility(View.VISIBLE);

            // add heart if show is a favorite
            if (mFavorites.isFavorite(item.getS1()))
                holder.favorite.setImageResource(R.drawable.ic_favorite_white_24dp);
            else
                holder.favorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            holder.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final boolean wasFavorite = mFavorites.isFavorite(item.getS1());
                    if (wasFavorite) {
                        mFavorites.removeFavorite(item.getS1());
                        Log.d(TAG, "Removed from Favorites");
                    } else {
                        mFavorites.addFavorites(item.getS1());
                        Log.d(TAG, "Added to Favorites");
                    }

                    // animate the heart button
                    final ScaleAnimation heartInAnim = new ScaleAnimation(0f, 1f, 0f, 1f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    heartInAnim.setInterpolator(new OvershootInterpolator());
                    heartInAnim.setDuration(200);

                    ScaleAnimation heartOutAnim = new ScaleAnimation(1f, 0f, 1f, 0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    heartOutAnim.setInterpolator(new AccelerateInterpolator());
                    heartOutAnim.setDuration(100);
                    heartOutAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder.favorite.setImageResource(!wasFavorite ?
                                    R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);
                            holder.favorite.startAnimation(heartInAnim);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    holder.favorite.startAnimation(heartOutAnim);
                }
            });
        }
    }

    public void bindDaySchedule(final ViewHolder holder, final ScheduleRecyclerItem item) {
        holder.title.setText(item.getS1());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DayScheduleActivity.class);
                intent.putExtra(Constants.DAY, item.getS1());
                mContext.startActivity(intent);
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

        int viewType;
        CardView cardView;
        TextView title;
        TextView subtitle;
        ImageView favorite;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            title = (TextView) itemView.findViewById(R.id.tv_title);
            subtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            if (viewType == CARD || viewType == CARD_NO_FAV || viewType == DAY_SCHEDULE) {
                cardView = (CardView) itemView.findViewById(R.id.card_view_item);
                favorite = (ImageView) itemView.findViewById(R.id.iv_favorite);
            }
        }
    }
}
