package com.lightricks.efraim.toolbar.toolbar;

import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.Map;

public class ToolbarPackItemsDecoration extends RecyclerView.ItemDecoration {
    private final PackItemsDecorationAdapter packItemsDecorationAdapter;
    private final int packHeaderTopOffset;
    private final int packHeaderLeftAndRightPadding;
    private final int badgeTopOffset;
    private final int badgeLeftOffset;
    private final View badgeView;

    private final static int MAX_TITLE_ENTRIES = 10;
    private Map<Integer, TextView> titlesCache = new LinkedHashMap<Integer, TextView>(MAX_TITLE_ENTRIES + 1, .75F, true) {
        public boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_TITLE_ENTRIES;
        }
    };

    public ToolbarPackItemsDecoration(PackItemsDecorationAdapter packItemsDecorationAdapter, int packHeaderTopOffset, int packHeaderLeftAndRightPadding, int badgeTopOffset, int badgeLeftOffset, View badgeView) {
        this.packItemsDecorationAdapter = packItemsDecorationAdapter;
        this.packHeaderTopOffset = packHeaderTopOffset;
        this.packHeaderLeftAndRightPadding = packHeaderLeftAndRightPadding;
        this.badgeTopOffset = badgeTopOffset;
        this.badgeLeftOffset = badgeLeftOffset;
        this.badgeView = badgeView;
    }


    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int[] lastChildPosContainer = new int[1];
        for (int i = 0; i < parent.getChildCount(); i++) {
            View firstVisibleChild = parent.getChildAt(i);
            View lastVisibleChild = findLastChildInPack(i, parent, lastChildPosContainer);
            i = lastChildPosContainer[0];
            int firstDisplayedInPackPosition = parent.getChildAdapterPosition(firstVisibleChild);
            TextView titleView = getPackTitleView(firstDisplayedInPackPosition, parent);
            if (titleView != null) {
                drawHeader(c, firstVisibleChild, lastVisibleChild, titleView);
            }
            if (badgeView != null) {
                if (packItemsDecorationAdapter.isFirstItemInPack(firstDisplayedInPackPosition)) {
                    Integer badge = packItemsDecorationAdapter.getPackBadgeIcon(firstDisplayedInPackPosition);
                    if (badge != null) {
                        if (badgeView.getWidth() == 0) {
                            measureView(badgeView, parent);
                        }
                        badgeView.setBackgroundResource(badge);
                        drawBadge(c, firstVisibleChild, badgeView);
                    }
                }
            }
        }
    }

    private View findLastChildInPack(int startPos, RecyclerView parent, int[] outIndex) {
        for (int i = startPos; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            if (packItemsDecorationAdapter.isLastItemInPack(position)) {
                outIndex[0] = i;
                return child;
            }
        }
        outIndex[0] = parent.getChildCount();
        return null;
    }


    private TextView getPackTitleView(int pos, RecyclerView parent) {
        Integer title = packItemsDecorationAdapter.getPackTitleText(pos);
        if (title == null) {
            return null;
        }
        TextView textView = titlesCache.get(title);
        if (textView == null) {
            textView = packItemsDecorationAdapter.getPackTitleView(pos);
            textView.setText(title);
            measureView(textView, parent);
            titlesCache.put(title, textView);
        }
        return textView;
    }

    private void drawHeader(Canvas canvas, View firstVisibleInPack, @Nullable View lastVisibleInPack, View headerView) {
        canvas.save();
        int rightBorder = canvas.getWidth() - packHeaderLeftAndRightPadding;
        if (lastVisibleInPack != null && lastVisibleInPack.getRight() < canvas.getWidth()) {
            // Clsoing pack item is visible, title should not be drawn after it.
            rightBorder = lastVisibleInPack.getRight() - headerView.getRight() - packHeaderLeftAndRightPadding;
        }
        int leftBorder = firstVisibleInPack.getLeft() + packHeaderLeftAndRightPadding;
        int transX = (canvas.getWidth() - headerView.getRight()) / 2;
        if (transX < leftBorder) {
            transX = leftBorder;
        } else if (transX > rightBorder) {
            transX = rightBorder;
        }

        canvas.translate(transX, packHeaderTopOffset);
        headerView.draw(canvas);
        canvas.restore();
    }

    private void drawBadge(Canvas canvas, View firstVisibleChild, View badge) {
        canvas.save();
        int xTranslation = firstVisibleChild.getLeft() - badgeLeftOffset;
        int yTranslation = badgeTopOffset;
        canvas.translate(xTranslation, yTranslation);
        badge.draw(canvas);
        canvas.restore();
    }


    private void measureView(View view, ViewGroup parent) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(),
                View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(),
                View.MeasureSpec.UNSPECIFIED);

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(),
                view.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.getPaddingTop() + parent.getPaddingBottom(),
                view.getLayoutParams().height);

        view.measure(childWidth,
                childHeight);

        view.layout(0,
                0,
                view.getMeasuredWidth(),
                view.getMeasuredHeight());
    }

    public interface PackItemsDecorationAdapter {
        boolean isFirstItemInPack(int pos);

        boolean isLastItemInPack(int pos);

        @Nullable
        @StringRes
        Integer getPackTitleText(int pos);

        @NonNull
        TextView getPackTitleView(int pos);

        @Nullable
        @DrawableRes
        Integer getPackBadgeIcon(int pos);
    }
}
