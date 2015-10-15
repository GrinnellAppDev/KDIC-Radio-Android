package edu.grinnell.kdic;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ViewHolder> {

    // define view types
    public static final int SECTION_HEADER = 0;
    public static final int CARD = 1;

    private ArrayList<ScheduleRecyclerItem> mContent;

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
            default:
                throw new IllegalArgumentException();
        }

        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ScheduleRecyclerItem item = mContent.get(position);
        holder.title.setText(item.getS1());
        holder.subtitle.setText(item.getS2());

        if(item.getViewType() == CARD) {
            // holder.cardView.setCardBackgroundColor();
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

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            subtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            if (viewType == CARD) {
                cardView = (CardView) itemView.findViewById(R.id.card_view_item);
            }
        }
    }
}
