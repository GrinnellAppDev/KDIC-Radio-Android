package edu.grinnell.kdic.schedule;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
  private Favorites mFavorites;
  private int animatePos;
  private ArrayList<ScheduleRecyclerItem> mContent;

  // define constants
  public static final int SECTION_HEADER = 0; // section header card
  public static final int CARD = 1;           // card with show info
  public static final int DAY_SCHEDULE = 2;   // card for day
  public static final int CARD_NO_FAV = 3;    // card for Auto-Play
  private static final int MS_ANIMATION_DURATION = 200;
  private static final int MS_ANIMATION_SCALE = 2;
  private static final float ZERO_ANIMATION_SCALE = 0f;
  private static final float ONE_ANIMATION_SCALE = 1f;
  private static final float ONE_HALF_ANIMATION_SCALE = 0.5f;

  // Constructor for ScheduleRecyclerViewAdapter
  public ScheduleRecyclerViewAdapter(FragmentActivity context) {
    mContext = context;
    Schedule mSchedule = new Schedule(context);
    mFavorites = new Favorites(context);
  }

  /**
   * Add content to the ScheduleRecyclerItem
   * @param content, the content to be added
   */
  public void addContent(ArrayList<ScheduleRecyclerItem> content) {
    mContent = content;
    notifyDataSetChanged();
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view;
    switch (viewType) {
      case SECTION_HEADER:
        view = inflater.inflate(R.layout.rv_item_header, parent, false);
        break;
      case CARD:
      case CARD_NO_FAV:
        view = inflater.inflate(R.layout.rv_item_card, parent, false);
        break;
      case DAY_SCHEDULE:
        view = inflater.inflate(R.layout.rv_item_day_card, parent, false);
        break;
      default:
        throw new IllegalArgumentException();
    }
    return new ViewHolder(view, viewType);
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

  /**
   * Set the tittle and subtitle for the view holder
   * @param holder, the view holder
   * @param item, the item in the recycler view
   */
  private void bindSectionHeader(ViewHolder holder, ScheduleRecyclerItem item) {
    holder.mTitle.setText(item.getS1());
    holder.mSubtitle.setText(item.getS2());
  }

  /**
   * Set image resources for the favorite items
   * @param holder, the view holder
   * @param item, the item
   */
  public void bindCard(final ViewHolder holder, final ScheduleRecyclerItem item) {
    bindSectionHeader(holder, item);
    if (holder.mViewType == CARD_NO_FAV)
      // remove the favorite button for Auto Play card
      holder.mFavorite.setVisibility(View.GONE);
    else {
      holder.mFavorite.setVisibility(View.VISIBLE);
      // add heart if show is a favorite
      if (mFavorites.isFavorite(item.getS1()))
        holder.mFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
      else
        holder.mFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
      holder.mFavorite.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          setFavoritesOnClick(v, item, holder);
        }
      });
    }
  }

  /**
   * Set response after clicking on favourite button
   * The screen will show the heart icon or make it disappear if the user clicks again
   * @param view, the current view,
   * @param item, the item of the recycler view
   * @param, holder, the view holder
   */
  private void setFavoritesOnClick(View view, final ScheduleRecyclerItem item,
      final ViewHolder holder) {
    final boolean wasFavorite = mFavorites.isFavorite(item.getS1());
    if (wasFavorite) {
      mFavorites.removeFavorite(item.getS1());
    } else {
      mFavorites.addFavorites(item.getS1());
    }
    // animate the heart button
    final ScaleAnimation heartInAnim = new ScaleAnimation(ZERO_ANIMATION_SCALE, ONE_ANIMATION_SCALE,
        ZERO_ANIMATION_SCALE, ONE_ANIMATION_SCALE,
        Animation.RELATIVE_TO_SELF, ONE_HALF_ANIMATION_SCALE, Animation.RELATIVE_TO_SELF, ONE_HALF_ANIMATION_SCALE);
    heartInAnim.setInterpolator(new OvershootInterpolator());
    heartInAnim.setDuration(MS_ANIMATION_DURATION);

    ScaleAnimation heartOutAnim = new ScaleAnimation(ONE_ANIMATION_SCALE, ZERO_ANIMATION_SCALE,
        ONE_ANIMATION_SCALE, ZERO_ANIMATION_SCALE,
        Animation.RELATIVE_TO_SELF, ONE_HALF_ANIMATION_SCALE, Animation.RELATIVE_TO_SELF, ONE_HALF_ANIMATION_SCALE);
    heartOutAnim.setInterpolator(new AccelerateInterpolator());
    heartOutAnim.setDuration(MS_ANIMATION_DURATION/MS_ANIMATION_SCALE);
    heartOutAnim.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
      }
      @Override
      public void onAnimationEnd(Animation animation) {
        holder.mFavorite.setImageResource(!wasFavorite ?
            R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);
        holder.mFavorite.startAnimation(heartInAnim);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {}
    });
    holder.mFavorite.startAnimation(heartOutAnim);

  }

  /**
   * Get the daily schedule
   * @param holder, the view holder
   * @param item, the item in the recycler view
   */
  public void bindDaySchedule(final ViewHolder holder, final ScheduleRecyclerItem item) {
    holder.mTitle.setText(item.getS1());
    holder.mCardView.setOnClickListener(new View.OnClickListener() {
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
    private int mViewType;
    private CardView mCardView;
    private TextView mTitle;
    private TextView mSubtitle;
    private ImageView mFavorite;

    //Constructor for the ViewHolder
    public ViewHolder(View itemView, int viewType) {
      super(itemView);
      this.mViewType = viewType;
      mTitle = (TextView) itemView.findViewById(R.id.tv_title);
      mSubtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
      if (viewType == CARD || viewType == CARD_NO_FAV || viewType == DAY_SCHEDULE) {
        mCardView = (CardView) itemView.findViewById(R.id.card_view_item);
        mFavorite = (ImageView) itemView.findViewById(R.id.iv_favorite);
      }
    }
  }
}
