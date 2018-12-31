package com.lightricks.efraim.toolbar.toolbar;

import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lightricks.efraim.toolbar.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Draws packs titles and badges.
 */
public class ToolbarPackDecorator extends RecyclerView.ItemDecoration {
    private final PackItemsDecorationAdapter packItemsDecorationAdapter;
    private final int packTitleTopOffset;
    private final int packTitleLeftAndRightPadding;
    private final int badgeLeftOffset;
    private final int badgeTopOffset;
    private final View badgeView;

    private final static int MAX_TITLE_ENTRIES = 10;
    /**
     * Caches titles text views in order to avoid inflating measuring and layouting every scroll. The key is the title resource id.
     */
    private Map<Integer, TextView> titlesLruCache = new LinkedHashMap<Integer, TextView>(MAX_TITLE_ENTRIES + 1, .75F, true) {
        public boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_TITLE_ENTRIES;
        }
    };

    /**
     * @param packItemsDecorationAdapter
     * @param packTitleTopOffset Offset from top of recycler view.
     * @param packTitleLeftAndRightPadding Padding added to the title so it will be drawn after the beginning of the pack and end before the end of the pack.
     * @param badgeLeftOffset Offset from the left of the pack.
     * @param badgeTopOffset Offset from top of recycler view.
     * @param badgeView View to be used as a badge.
     */
    public ToolbarPackDecorator(PackItemsDecorationAdapter packItemsDecorationAdapter, int packTitleTopOffset, int packTitleLeftAndRightPadding, int badgeLeftOffset, int badgeTopOffset, View badgeView) {
        this.packItemsDecorationAdapter = packItemsDecorationAdapter;
        this.packTitleTopOffset = packTitleTopOffset;
        this.packTitleLeftAndRightPadding = packTitleLeftAndRightPadding;
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
            // Next loop will start from the next pack.
            i = lastChildPosContainer[0];
            int firstDisplayedInPackPosition = parent.getChildAdapterPosition(firstVisibleChild);
            TextView titleView = getPackTitleView(firstDisplayedInPackPosition, parent);
            if (titleView != null) {
                drawPackHeader(c, firstVisibleChild, lastVisibleChild, titleView);
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
        TextView textView = titlesLruCache.get(title);
        if (textView == null) {
            textView = packItemsDecorationAdapter.getPackTitleView(pos);
            textView.setText(title);
            measureView(textView, parent);
            titlesLruCache.put(title, textView);
        }
        return textView;
    }

    /**
     * Center the pack title in relative to the recycler view. Pack title must be drawn within the pack items with predefined padding in start and the end.
     */
    private void drawPackHeader(Canvas canvas, View firstVisibleInPack, @Nullable View lastVisibleInPack, View headerView) {
        canvas.save();
        int rightBorder = canvas.getWidth() - packTitleLeftAndRightPadding;
        if (lastVisibleInPack != null && lastVisibleInPack.getRight() < canvas.getWidth()) {
            // Closing pack item is visible, title should not be drawn after it.
            rightBorder = lastVisibleInPack.getRight() - headerView.getRight() - packTitleLeftAndRightPadding;
        }
        int leftBorder = firstVisibleInPack.getLeft() + packTitleLeftAndRightPadding;
        int transX = (canvas.getWidth() - headerView.getRight()) / 2;
        if (transX < leftBorder) {
            transX = leftBorder;
        } else if (transX > rightBorder) {
            transX = rightBorder;
        }
        canvas.translate(transX, packTitleTopOffset);
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

    /**
     * Adaptor for {@link ToolbarPackDecorator}. Every scroll triggers new draw. In order to avoid unnecessary operations
     * on hot code path avoid layouting views when not necessary. The badge view is layouted once and only the background is replaced.
     * For the pack title the same view cannot be reused since the titles length are not identical thus need to measure the
     * text view for every title. Separating the content of the title form the view allows caching of the views.
     */
    public interface PackItemsDecorationAdapter {
        /**
         * True if item is the first item in the pack.
         */
        boolean isFirstItemInPack(int pos);

        /**
         * False if item is last item in the pack.
         */
        boolean isLastItemInPack(int pos);

        /**
         * Title string resource id. Null if title should not be displayed.
         */
        @Nullable
        @StringRes
        Integer getPackTitleText(int pos);

        /**
         * Text view to display the title. Text view defines the styling of the title.
         */
        @NonNull
        TextView getPackTitleView(int pos);

        /**
         * Badge icon resource id. Null if badge should not be displayed.
         */
        @Nullable
        @DrawableRes
        Integer getPackBadgeIcon(int pos);
    }
}
