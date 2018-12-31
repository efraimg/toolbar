package com.lightricks.efraim.toolbar.toolbar;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Pair;
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
    /**
     * Notifies when a toolbar item is clicked.
     */
    public interface ToolbarItemClickedListener {
        void onClick(ToolbarItem item);
    }

    private RecyclerView toolbarRecyclerView;
    private ToolbarSpaceDecorator spaceDecorator;
    private ToolbarPackDecorator packDecorator;
    /**
     * Left offset of first item. {@link #firstPackOffset} is used in case of pack items.
     */
    private int firstItemOffset;
    /**
     * Padding between items. {@link #spaceBetweenPackItems} and {@link #spaceBetweenPacks} are used
     * in case of pack items.
     */
    private int spaceBetweenItems;
    /**
     * Left offset of first pack.
     */
    private int firstPackOffset;
    /**
     * Padding between packs.
     */
    private int spaceBetweenPacks;
    /**
     * Padding between pack items in the same pack.
     */
    private int spaceBetweenPackItems;
    private ToolbarAdapter toolbarRecyclerViewAdapter;
    /**
     * Items to be displayed in the recycler view.
     */
    private List<ToolbarItem> toolbarItems;
    /**
     * Callback to be invoked when a item is clicked.
     */
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

    public void setToolbarItems(@NonNull List<ToolbarItem> toolbarItems) {
        this.toolbarItems = toolbarItems;
        if (toolbarRecyclerViewAdapter != null) {
            toolbarRecyclerViewAdapter.submitList(toolbarItems);
        }
        calculateItemsSpace();
        setSpaceDecorator();
    }

    /**
     * Set callback for item click.
     */
    public void setToolbarItemClickedListener(ToolbarItemClickedListener toolbarItemClickedListener) {
        this.toolbarItemClickedListener = toolbarItemClickedListener;
        if (toolbarRecyclerViewAdapter != null) {
            toolbarRecyclerViewAdapter.setItemClickedListener(toolbarItemClickedListener);
        }
    }

    /**
     * At this stage the size of the view is known so items display can be configured.
     */
    private void setPreDrawListener() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                initRecyclerView();
                initSpacingParams();
                setPacksDecorator();
                setSpaceDecorator();
                return false;
            }
        });
    }

    /**
     * When size of of view is changed, need to redraw the view according to new dimensions.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldh == 0) {
            // First time the view is measured.
            return;
        }
        // Force recycler view to redraw.
        setRecyclerViewAdaptor();
        // Recalculate spaces.
        initSpacingParams();
        // Set new decorators.
        setPacksDecorator();
        setSpaceDecorator();

    }

    private void initRecyclerView() {
        toolbarRecyclerView = findViewById(R.id.toolbar_recycler_view);
        toolbarRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        toolbarRecyclerView.setHasFixedSize(true);
        setRecyclerViewAdaptor();
    }

    private void setRecyclerViewAdaptor() {
        if (toolbarRecyclerView == null) {
            return;
        }
        toolbarRecyclerViewAdapter = new ToolbarAdapter(getContext(), getHeight());
        toolbarRecyclerView.setAdapter(toolbarRecyclerViewAdapter);
        if (toolbarItems != null) {
            toolbarRecyclerViewAdapter.submitList(toolbarItems);
        }
        if (toolbarItemClickedListener != null) {
            toolbarRecyclerViewAdapter.setItemClickedListener(toolbarItemClickedListener);
        }
    }

    @NonNull
    private ToolbarPackDecorator createToolbarPackDecoration() {
        int titleOffset = calculatePackTitleOffsetFromTopOfRecyclerView();
        int packHeaderLeftAndRightPadding = getResources().getDimensionPixelOffset(R.dimen.toolbar_pack_title_left_and_right_padding);
        Pair<Integer, Integer> badgeLeftAndTopOffset = calculateBadgeLeftAndTopOffsetFromTopOfRecyclerView();
        View badgeView = getLayoutInflater(getContext()).inflate(R.layout.badge, null);
        badgeView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ToolbarPackDecorator(new PackDecorationAdaptor(), titleOffset, packHeaderLeftAndRightPadding,
                badgeLeftAndTopOffset.first, badgeLeftAndTopOffset.second, badgeView);
    }

    /**
     * Calculate the offset of the pack title from the top of the recycler view so the the title
     * will be drawn right below the pack items plus predefined padding.
     */
    private int calculatePackTitleOffsetFromTopOfRecyclerView() {
        float packItemsHeightPercent = getFloatVal(R.dimen.toolbar_pack_item_height);
        float topPaddingPercent = (1.0f - packItemsHeightPercent) / 2.0f;
        float titleTopOffsetFromThumbnail = getResources().getDimension(R.dimen.toolbar_pack_title_top_offset);
        int titleOffset = (int) ((packItemsHeightPercent + topPaddingPercent) * getHeight() + titleTopOffsetFromThumbnail);
        return titleOffset;
    }


    /**
     * Badge center should be in the top left corner of the pack.
     */
    private Pair<Integer, Integer> calculateBadgeLeftAndTopOffsetFromTopOfRecyclerView() {
        // Left offset.
        int badgeSize = getResources().getDimensionPixelSize(R.dimen.toolbar_badge_size);
        int badgeLeftOffset = badgeSize / 2;
        // Top offset.
        float heightPercent = getFloatVal(R.dimen.toolbar_pack_item_height);
        float topPaddingPercent = (1.0f - heightPercent) / 2.0f;
        int badgeTopOffset = (int) (getHeight() * topPaddingPercent - badgeSize / 2);
        return new Pair<>(badgeLeftOffset, badgeTopOffset);
    }


    private float getFloatVal(@DimenRes int dimenId) {
        TypedValue outValue = new TypedValue();
        getResources().getValue(dimenId, outValue, true);
        return outValue.getFloat();
    }

    private void initSpacingParams() {
        calculatePackItemsSpace();
        calculateItemsSpace();
    }

    private void calculatePackItemsSpace() {
        firstPackOffset = getResources().getDimensionPixelSize(R.dimen.toolbar_pack_first_pack_left_offset);
        spaceBetweenPacks = (int) (getFloatVal(R.dimen.toolbar_pack_space_relative_to_toolbar_height) * getHeight());
        spaceBetweenPackItems = getResources().getDimensionPixelOffset(R.dimen.toolbar_pack_space_between_pack_items);
    }

    /**
     * Calculate space between items so they will be layout nicely on the screen. If there is more
     * items that can fit in the view width, set the space so the last item will be partially visible.
     * If the items do not exceed view width, try to set the spaces so the items will take over all
     * the view width in a symmetrical way. If in order to fill the width the space between items
     * exceeds a given threshold defined by {@link R.dimen#toolbar_item_max_space} center the items
     * and set the space to max.
     */
    private void calculateItemsSpace() {
        if (toolbarItems == null || toolbarItems.size() == 0 || getWidth() == 0) {
            return;
        }
        firstItemOffset = getContext().getResources().getDimensionPixelOffset(R.dimen.toolbar_item_first_item_left_offset);

        int layoutWidth = getWidth();
        int itemWidth = getContext().getResources().getDimensionPixelOffset(R.dimen.toolbar_item_width);
        int minSpaceBetweenItems = getContext().getResources().getDimensionPixelOffset(R.dimen.toolbar_item_min_space);
        int totalItemsWidth = toolbarItems.size() * itemWidth + firstItemOffset +
                (toolbarItems.size() - 1) * minSpaceBetweenItems;
        spaceBetweenItems = minSpaceBetweenItems;
        if (totalItemsWidth > layoutWidth) {
            // Items cannot fit in view (without scroll). Set space in a way that the last item will be half visible;
            float lastItemVisibilityPercent = getFloatVal(R.dimen.toolbar_item_last_item_visibility_percent);
            int availableLayoutSpace = layoutWidth - (int)(itemWidth *lastItemVisibilityPercent) - firstItemOffset;
            int numOfItemsDisplayedExcludingLast = availableLayoutSpace / (itemWidth + minSpaceBetweenItems);
            int unUsedSpace = availableLayoutSpace - numOfItemsDisplayedExcludingLast * (itemWidth + minSpaceBetweenItems);
            spaceBetweenItems += unUsedSpace / numOfItemsDisplayedExcludingLast;
        } else if (totalItemsWidth < layoutWidth) {
            //Items can fit in view. Try to fill up screen width by adding space between items.
            int maxSpaceBetweenItems = getContext().getResources().getDimensionPixelOffset(R.dimen.toolbar_item_max_space);
            int totalItemsWidthIncludingSpace = maxSpaceBetweenItems * (toolbarItems.size() + 1) + toolbarItems.size() * itemWidth;
            if (totalItemsWidthIncludingSpace >= layoutWidth) {
                // Items can fit in screen without exceeding max space.
                int unUsedSpace = layoutWidth - toolbarItems.size() * itemWidth;
                int padding = unUsedSpace / (toolbarItems.size() + 1);
                spaceBetweenItems = padding;
                firstItemOffset = spaceBetweenItems;
            } else {
                // Items can not fill screen, center the items.
                spaceBetweenItems = maxSpaceBetweenItems;
                int gap = (layoutWidth - totalItemsWidthIncludingSpace) / 2;
                firstItemOffset = spaceBetweenItems + gap;
            }
        }
    }

    private void setSpaceDecorator() {
        if (toolbarRecyclerView == null) {
            return;
        }
        if (spaceDecorator != null) {
            toolbarRecyclerView.removeItemDecoration(spaceDecorator);
        }
        spaceDecorator = new ToolbarSpaceDecorator(new SpaceDecoratorAdaptor());
        toolbarRecyclerView.addItemDecoration(spaceDecorator);
    }

    private void setPacksDecorator() {
        if (toolbarRecyclerView == null) {
            return;
        }
        if (packDecorator != null) {
            toolbarRecyclerView.removeItemDecoration(packDecorator);
        }
        packDecorator = createToolbarPackDecoration();
        toolbarRecyclerView.addItemDecoration(packDecorator);
    }


    private class PackDecorationAdaptor implements ToolbarPackDecorator.PackItemsDecorationAdapter {
        @Override
        public boolean isFirstItemInPack(int pos) {
            if (toolbarItems != null && pos < toolbarItems.size()) {
                return toolbarItems.get(pos).isFirst();
            }
            return false;
        }

        @Override
        public boolean isLastItemInPack(int pos) {
            if (toolbarItems != null && pos < toolbarItems.size()) {
                return toolbarItems.get(pos).isLast();
            }
            return false;
        }

        /**
         * Return the title to be displayed for the given pack item. The title text should be defined
         * in the object of the first item in the pack.
         */
        @Override
        public Integer getPackTitleText(int pos) {
            if (toolbarItems != null && pos < toolbarItems.size()) {
                if (toolbarItems.get(pos).getStyle() != ToolbarItemStyle.PACK) {
                    return null;
                }
                for (int i = pos; i >= 0; --i) {
                    if (toolbarItems.get(i).isFirst()) {
                        return toolbarItems.get(i).getPackTitle();
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
                if (toolbarItems.get(i).isFirst()) {
                    Integer badge = toolbarItems.get(i).getBadge();
                    return badge;
                }
            }
            return null;
        }
    }

    private class SpaceDecoratorAdaptor implements ToolbarSpaceDecorator.SpaceDecorationAdapter {

        @Override
        public int getSpaceBeforeItem(int pos) {
            if (toolbarItems == null || pos > toolbarItems.size()) {
                return 0;
            }
            return toolbarItems.get(pos).getStyle() != ToolbarItemStyle.PACK ? getItemSpace(pos) : getPackSpace(pos);
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
                if (toolbarItems.get(pos).isFirst()) {
                    return spaceBetweenPacks;
                } else {
                    return spaceBetweenPackItems;
                }
            }
        }
    }
}

