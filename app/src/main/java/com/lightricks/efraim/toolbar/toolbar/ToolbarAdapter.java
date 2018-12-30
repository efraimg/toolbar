package com.lightricks.efraim.toolbar.toolbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lightricks.efraim.toolbar.R;

import java.util.HashMap;
import java.util.Map;

public class ToolbarAdapter extends ListAdapter<ToolbarItem, ToolbarAdapter.ToolbarItemViewHolder> {
    private static final Map<Integer, ToolbarItemStyle> RES_ID_STYLE_MAP = new HashMap<>();
    private static final Map<ToolbarItemStyle, Integer> STYLE_RES_ID_MAP = new HashMap<>();
    private final Context context;
    private final int height;

    static {
        RES_ID_STYLE_MAP.put(R.layout.toolbar_item_icon, ToolbarItemStyle.ICON);
        STYLE_RES_ID_MAP.put(ToolbarItemStyle.ICON, R.layout.toolbar_item_icon);

        RES_ID_STYLE_MAP.put(R.layout.toolbar_item_number, ToolbarItemStyle.NUMBER);
        STYLE_RES_ID_MAP.put(ToolbarItemStyle.NUMBER, R.layout.toolbar_item_number);

        RES_ID_STYLE_MAP.put(R.layout.toolbar_item_pack, ToolbarItemStyle.PACK);
        STYLE_RES_ID_MAP.put(ToolbarItemStyle.PACK, R.layout.toolbar_item_pack);
    }

    private ToolbarView.ToolbarItemClickedListener toolbarItemClickedListener;
    private View.OnClickListener onClickListener;

    abstract class ToolbarItemViewHolder extends RecyclerView.ViewHolder {
        ToolbarItem toolbarItem;

        ToolbarItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            layoutParams.height = height;
            itemView.setTag(this);
            if (onClickListener != null) {
                itemView.setOnClickListener(onClickListener);
            }
        }

        void bind(int position) {
            toolbarItem = getItem(position);
        }
    }

    class IconViewHolder extends ToolbarItemViewHolder {
        View icon;
        TextView title;
        View badge;

        IconViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.toolbar_icon_icon_view);
            title = itemView.findViewById(R.id.toolbar_icon_title_text);
            badge = itemView.findViewById(R.id.toolbar_icon_badge);
        }

        @Override
        void bind(int position) {
            super.bind(position);
            ToolbarItem toolbarItem = getItem(position);
            if (toolbarItem.getIcon() != null) {
                icon.setBackgroundResource(toolbarItem.getIcon());
            }
            if (toolbarItem.getIconTintColor() != null) {
                int colorId = context.getResources().getColor(toolbarItem.getIconTintColor(), null);
                icon.setBackgroundTintList(ColorStateList.valueOf(colorId));
            }
            if (toolbarItem.getTitle() != null) {
                title.setText(toolbarItem.getTitle());
            } else {
                title.setText("");
            }
            if (toolbarItem.getTitleColor() != null) {
                int colorId = context.getResources().getColor(toolbarItem.getTitleColor(), null);
                icon.setBackgroundTintList(ColorStateList.valueOf(colorId));
                title.setTextColor(colorId);
            }
            if (toolbarItem.getBadge() != null) {
                badge.setBackgroundResource(toolbarItem.getBadge());
            }
        }
    }

    class NumberViewHolder extends ToolbarItemViewHolder {
        TextView number;
        TextView title;

        NumberViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.toolbar_number_value_text);
            title = itemView.findViewById(R.id.toolbar_number_title_text);
        }

        @Override
        void bind(int position) {
            super.bind(position);
            ToolbarItem toolbarItem = getItem(position);
            number.setText(toolbarItem.getValue());
            if (toolbarItem.getTitle() != null) {
                title.setText(toolbarItem.getTitle());
            } else {
                title.setText("");
            }
            if (toolbarItem.getTitleColor() != null) {
                int colorId = context.getResources().getColor(toolbarItem.getTitleColor(), null);
                number.setTextColor(colorId);
                title.setTextColor(colorId);
            }
        }
    }

    class PackViewHolder extends ToolbarItemViewHolder {
        ImageView image;
        View icon;
        TextView title;
        TextView titleSelected;
        View fisrtItemMask;
        View lastItemMask;
        View iconMask;

        PackViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.toolbar_pack_image);
            icon = itemView.findViewById(R.id.toolbar_pack_icon);
            title = itemView.findViewById(R.id.toolbar_pack_title_text);
            titleSelected = itemView.findViewById(R.id.toolbar_pack_title_text_selected);
            fisrtItemMask = itemView.findViewById(R.id.toolbar_pack_first_item_mask);
            lastItemMask = itemView.findViewById(R.id.toolbar_pack_last_item_mask);
            iconMask = itemView.findViewById(R.id.toolbar_pack_icon_mask);
        }

        @Override
        void bind(int position) {
            super.bind(position);
            ToolbarItem toolbarItem = getItem(position);
            Glide.with(image).load(toolbarItem.getThumbnail()).into(image);
            TextView activeTitle;
            if (toolbarItem.getIcon() != null) {
                activeTitle = titleSelected;
                titleSelected.setVisibility(View.VISIBLE);
                title.setVisibility(View.GONE);
                iconMask.setVisibility(View.VISIBLE);
                icon.setBackgroundResource(toolbarItem.getIcon());
                if (toolbarItem.getIconTintColor() != null) {
                    int colorId = context.getResources().getColor(toolbarItem.getIconTintColor(), null);
                    icon.setBackgroundTintList(ColorStateList.valueOf(colorId));
                }
            }else{
                activeTitle = title;
                titleSelected.setVisibility(View.GONE);
                title.setVisibility(View.VISIBLE);
                icon.setBackground(null);
                iconMask.setVisibility(View.GONE);
            }
            if (toolbarItem.getTitle() != null) {
                activeTitle.setText(toolbarItem.getTitle());
            } else {
                activeTitle.setText("");
            }

            if (toolbarItem.getTitleBackgroundColor() != null) {
                title.setBackgroundColor(context.getResources().getColor(toolbarItem.getTitleBackgroundColor(), null));
            }

            if (toolbarItem.isFirst()) {
                fisrtItemMask.setVisibility(View.VISIBLE);
            } else {
                fisrtItemMask.setVisibility(View.INVISIBLE);
            }
            if (toolbarItem.isLast()) {
                lastItemMask.setVisibility(View.VISIBLE);
            } else {
                lastItemMask.setVisibility(View.INVISIBLE);
            }

        }
    }


    public ToolbarAdapter(Context context, int height) {
        super(new DiffUtil.ItemCallback<ToolbarItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull ToolbarItem item1, @NonNull ToolbarItem item2) {
                return item1.getId().equals(item2.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ToolbarItem item1, @NonNull ToolbarItem item2) {
                return item1.equals(item2);
            }
        });
        this.context = context;
        this.height = height;
        onClickListener = v -> {
            if (toolbarItemClickedListener != null) {
                ToolbarItemViewHolder toolbarItemViewHolder = (ToolbarItemViewHolder) v.getTag();
                toolbarItemClickedListener.onClick(toolbarItemViewHolder.toolbarItem);
            }
        };
    }

    public void setItemClickedListener(@NonNull ToolbarView.ToolbarItemClickedListener toolbarItemClickedListener) {
        this.toolbarItemClickedListener = toolbarItemClickedListener;
    }


    @Override
    public int getItemViewType(int position) {
        ToolbarItem toolbarItem = getItem(position);
        return STYLE_RES_ID_MAP.get(toolbarItem.getStyle());
    }

    @NonNull
    @Override
    public ToolbarItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        switch (RES_ID_STYLE_MAP.get(viewType)) {
            case ICON:
                return new IconViewHolder(view);
            case NUMBER:
                return new NumberViewHolder(view);
            case PACK:
                return new PackViewHolder(view);
            default:
                throw new IllegalArgumentException("Unsupported type: " + RES_ID_STYLE_MAP.get(viewType));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ToolbarItemViewHolder toolbarItemViewHolder, int position) {
        toolbarItemViewHolder.bind(position);
    }

}
