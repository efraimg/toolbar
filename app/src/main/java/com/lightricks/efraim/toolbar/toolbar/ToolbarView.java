package com.lightricks.efraim.toolbar.toolbar;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lightricks.efraim.toolbar.R;

import java.util.List;

public class ToolbarView extends FrameLayout {
    private RecyclerView toolbarRecyclerView;
    private ToolbarSpaceDecoration spaceDecorator;
    private int firstItemOffset;
    private int spaceBetweenItems;
    private int spaceBetweenPacks;
    private int spaceBetweenPackItems;
    private int firstPackOffset;

    /**
     * Notifies when a toolbar item is clicked.
     */
    public interface ToolbarItemClickedListener {
        void onClick(ToolbarItem item);
    }

    private ToolbarAdapter toolbarAdapter;
    private List<ToolbarItem> toolbarItemList;
    private ToolbarItemClickedListener toolbarItemClickedListener;


    public ToolbarView(@NonNull Context context) {
        super(context);
        inflateView(context);
        setPreDrawListener();
    }

    public ToolbarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
        setPreDrawListener();
    }

    public ToolbarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateView(context);
        setPreDrawListener();
    }

    private void inflateView(Context context) {
        LayoutInflater inflater = getLayoutInflater(context);
        inflater.inflate(R.layout.toolbar_layout, this);
    }

    private LayoutInflater getLayoutInflater(Context context) {
        return (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setToolbarItemList(@NonNull List<ToolbarItem> toolbarItemList) {
        this.toolbarItemList = toolbarItemList;
        if (toolbarAdapter != null) {
            toolbarAdapter.submitList(toolbarItemList);
        }
        calcToolbarItemsSpace();
    }

    public void setToolbarItemClickedListener(ToolbarItemClickedListener toolbarItemClickedListener) {
        this.toolbarItemClickedListener = toolbarItemClickedListener;
        if (toolbarAdapter != null) {
            toolbarAdapter.setItemClickedListener(toolbarItemClickedListener);
        }
    }


    private void setPreDrawListener() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                initRecyclerView();
                initSpacingParams();
                return false;
            }
        });
    }

    private void initRecyclerView() {
        toolbarRecyclerView = findViewById(R.id.toolbar_recycler_view);
        toolbarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        toolbarAdapter = new ToolbarAdapter(getContext(), getHeight());
        toolbarRecyclerView.setAdapter(toolbarAdapter);
        if (toolbarItemList != null) {
            toolbarAdapter.submitList(toolbarItemList);
        }
        toolbarRecyclerView.addItemDecoration(createToolbarPackItemsDecoration());

    }

    @NonNull
    private RecyclerView.ItemDecoration createToolbarPackItemsDecoration() {
        int titleOffset = calcPackTitleOffsetFromTopOfRecyclerView();
        int packHeaderLeftAndRightPadding = getResources().getDimensionPixelOffset(R.dimen.toolbar_pack_title_left_and_right_padding);
        // Badge center should be in the top left corner of the toolbar item.
        int itemTopOffset = getResources().getDimensionPixelSize(R.dimen.toolbar_item_top_offset);
        int badgeSize = getResources().getDimensionPixelSize(R.dimen.toolbar_badge_size);
        int badgeTopOffset = itemTopOffset - (badgeSize / 2);
        int badgeLeftOffset = (badgeSize / 2);
        View badgeView = getLayoutInflater(getContext()).inflate(R.layout.badge, null).findViewById(R.id.toolbar_item_badge);
        return new ToolbarPackItemsDecoration(new PackDecorationAdaptor(), titleOffset, packHeaderLeftAndRightPadding, badgeTopOffset, badgeLeftOffset, badgeView);
    }

    private int calcPackTitleOffsetFromTopOfRecyclerView() {
        float heightPercent = getFloatVal(R.dimen.toolbar_pack_item_height);
        float topPaddingPercent = (1.0f - heightPercent) / 2.0f;
        float titleTopOffsetFromThumbnail = getResources().getDimension(R.dimen.toolbar_pack_title_top_offset);
        int titleOffset = (int) ((heightPercent + topPaddingPercent) * getHeight() + titleTopOffsetFromThumbnail);
        return titleOffset;
    }

    private float getFloatVal(@DimenRes int dimenId) {
        TypedValue outValue = new TypedValue();
        getResources().getValue(dimenId, outValue, true);
        return outValue.getFloat();
    }

    private void initSpacingParams() {
        spaceBetweenPacks = (int) (getFloatVal(R.dimen.toolbar_pack_space_relative_to_toolbar_height) * getHeight());
        spaceBetweenPackItems = getResources().getDimensionPixelOffset(R.dimen.toolbar_pack_space_between_pack_items);
        firstPackOffset = getResources().getDimensionPixelSize(R.dimen.toolbar_pack_first_pack_left_offset);
        firstItemOffset = getContext().getResources().getDimensionPixelOffset(R.dimen.toolbar_item_first_item_left_offset);
        calcToolbarItemsSpace();
    }

    private void calcToolbarItemsSpace() {
        if (toolbarItemList == null || toolbarItemList.size() == 0 || getWidth() == 0) {
            return;
        }
        int layoutWidth = getWidth();
        int itemWidth = getContext().getResources().getDimensionPixelOffset(R.dimen.toolbar_item_width);
        int minSpaceBetweenItems = getContext().getResources().getDimensionPixelOffset(R.dimen.toolbar_item_min_space);
        int totalItemsWidth = toolbarItemList.size() * itemWidth + firstItemOffset + (toolbarItemList.size() - 1) * minSpaceBetweenItems;

        spaceBetweenItems = minSpaceBetweenItems;
        if (totalItemsWidth > layoutWidth) {
            // Set space in a way that the last item will be half vis;
            int availableLayoutSpace = layoutWidth - itemWidth / 2 - firstItemOffset;
            int numOfItemsDisplayedExcludingLast = availableLayoutSpace / (itemWidth + minSpaceBetweenItems);
            int unUsedSpace = availableLayoutSpace - numOfItemsDisplayedExcludingLast * (itemWidth + minSpaceBetweenItems);
            spaceBetweenItems += unUsedSpace / numOfItemsDisplayedExcludingLast;
        } else if (totalItemsWidth < layoutWidth) {
            // Try to fill up screen width by adding padding between items.
            int maxSpaceBetweenItems = getContext().getResources().getDimensionPixelOffset(R.dimen.toolbar_item_max_space);
            int maxSpace = maxSpaceBetweenItems * (toolbarItemList.size() + 1) + toolbarItemList.size() * itemWidth;
            if (maxSpace >= layoutWidth) {
                int unUsedSpace = layoutWidth - toolbarItemList.size() * itemWidth;
                int padding = unUsedSpace / (toolbarItemList.size() + 1);
                spaceBetweenItems = padding;
                firstItemOffset = spaceBetweenItems;
            } else {
                spaceBetweenItems = maxSpaceBetweenItems;
                int gap = (layoutWidth - maxSpace) / 2;
                firstItemOffset = spaceBetweenItems + gap;
            }
        }
        if (spaceDecorator != null) {
            toolbarRecyclerView.removeItemDecoration(spaceDecorator);
        }
        spaceDecorator = new ToolbarSpaceDecoration(new SpaceDecoratorAdaptor());
        toolbarRecyclerView.addItemDecoration(spaceDecorator);
    }


    private class PackDecorationAdaptor implements ToolbarPackItemsDecoration.PackItemsDecorationAdapter {
        @Override
        public boolean isFirstItemInPack(int pos) {
            if (toolbarItemList != null && pos < toolbarItemList.size()) {
                return toolbarItemList.get(pos).isFirst();
            }
            return false;
        }

        @Override
        public boolean isLastItemInPack(int pos) {
            if (toolbarItemList != null && pos < toolbarItemList.size()) {
                return toolbarItemList.get(pos).isLast();
            }
            return false;
        }

        @Override
        public Integer getPackTitleText(int pos) {
            if (toolbarItemList != null && pos < toolbarItemList.size()) {
                if (toolbarItemList.get(pos).getStyle() != ToolbarItemStyle.PACK) {
                    return null;
                }
                for (int i = pos; i >= 0; --i) {
                    if (i != pos && toolbarItemList.get(i).isLast()) {
                        return null;
                    }
                    if (toolbarItemList.get(i).isFirst()) {
                        return toolbarItemList.get(i).getPackTitle();
                    }
                }
            }
            return null;
        }

        @NonNull
        @Override
        public TextView getPackTitleView(int pos) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            TextView textView = (TextView) layoutInflater.inflate(R.layout.toolbar_pack_title, null);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return textView;
        }

        @Nullable
        public Integer getPackBadgeIcon(int pos) {
            for (int i = pos; i >= 0; --i) {
                if (toolbarItemList.get(i).isFirst()) {
                    Integer badge = toolbarItemList.get(i).getBadge();
                    return badge;
                }
            }
            return null;
        }
    }

    private class SpaceDecoratorAdaptor implements ToolbarSpaceDecoration.SpaceDecorationAdapter {

        @Override
        public int getSpaceBeforeItem(int pos) {
            if (toolbarItemList == null || pos > toolbarItemList.size()) {
                return 0;
            }
            return toolbarItemList.get(pos).getStyle() != ToolbarItemStyle.PACK ? getItemSpace(pos) : getPackSpace(pos);
        }

        private int getItemSpace(int pos) {
            if (pos == 0) {
                return firstItemOffset;
            } else {
                return spaceBetweenItems;
            }
        }

        private int getPackSpace(int pos) {
            if (pos == 0) {
                return firstPackOffset;
            } else {
                if (toolbarItemList.get(pos).isFirst()) {
                    return spaceBetweenPacks;
                } else {
                    return spaceBetweenPackItems;
                }
            }
        }
    }
}

