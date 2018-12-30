package com.lightricks.efraim.toolbar.toolbar;

import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/**
 * A item that will be displayed in the app toolbar. Every Feature has a Toolbar that is displayed
 * at the bottom of the screen. A 'toolbar' is a container for toolbar items. There are multiple
 * types of items that can be displayed in a toolbar, some of the fields may be relevant only for
 * a certain type. This class is used as union class for all types.
 */
public class ToolbarItem {
    /**
     * Id for the toolbar item. When item is clicked the id will be used to identify the source of the click.
     */
    @NonNull
    private final String id;

    /**
     * Style of the item.
     */
    @NonNull
    private final ToolbarItemStyle style;

    /**
     * Title displayed by this item.
     */
    @StringRes
    @Nullable
    private final Integer title;


    /**
     * Color used by the item.
     */
    @ColorRes
    @Nullable
    private final Integer titleBackgroundColor;
    /**
     * Value to be displayed by this item. For example for a item that displays a number, the value can be the number to
     * display.
     */
    @Nullable
    private final String value;

    /**
     * Icon that is displayed by this item.
     */
    @DrawableRes
    @Nullable
    private final Integer icon;

    @Nullable
    private final Uri thumbnail;


    private final boolean isSelected;

    /**
     * Badge displayed on top of items's content
     */
    @DrawableRes
    @Nullable
    private final Integer badge;



    /**
     * When true, the item is the first of a pack or group of items.
     */
    private final boolean isFirst;
    /**
     * When false, the item is the last of a pack or group of items.
     */
    private final boolean isLast;

    @StringRes
    @Nullable
    private final Integer packTitle;

    private ToolbarItem(@NonNull String id, @NonNull ToolbarItemStyle style, @Nullable @StringRes Integer title, @Nullable Integer titleBackgroundColor, @Nullable String value, @DrawableRes @Nullable Integer icon,
                        @Nullable Uri thumbnail, boolean isSelected, @DrawableRes @Nullable Integer badge, boolean isFirst, boolean isLast, @StringRes @Nullable Integer packTitle) {
        this.id = id;
        this.style = style;
        this.title = title;
        this.titleBackgroundColor = titleBackgroundColor;
        this.value = value;
        this.icon = icon;
        this.thumbnail = thumbnail;
        this.isSelected = isSelected;
        this.badge = badge;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.packTitle = packTitle;
    }

    public static ToolbarItem createIconToolbarItem(@NonNull String id, @NonNull @StringRes Integer title,
                                                    @DrawableRes @NonNull Integer icon,boolean isSelected, @DrawableRes @Nullable Integer badge) {
        return new ToolbarItem(id, ToolbarItemStyle.ICON, title, null, null, icon, null, isSelected, badge, false, false, null);
    }

    public static ToolbarItem createNumberToolbarItem(@NonNull String id, @NonNull @StringRes Integer title, @NonNull String value,boolean isSelected) {
        return new ToolbarItem(id, ToolbarItemStyle.NUMBER, title, null, value, null, null, isSelected, null, false, false, null);
    }

    public static ToolbarItem createPackToolbarItem(@NonNull String id,
                                                    @NonNull @StringRes Integer title, @NonNull Integer backgroundColor, @DrawableRes @Nullable Integer icon
            , @NonNull Uri thumbnail, boolean isSelected,@DrawableRes @Nullable Integer badge, boolean isFirst, boolean isLast, @StringRes @Nullable Integer packTitle) {
        return new ToolbarItem(id, ToolbarItemStyle.PACK, title, backgroundColor, null, icon, thumbnail, isSelected, badge, isFirst, isLast, packTitle);

    }


    public String getId() {
        return id;
    }

    @NonNull
    public ToolbarItemStyle getStyle() {
        return style;
    }

    @Nullable
    public Integer getTitle() {
        return title;
    }

    @Nullable
    public String getValue() {
        return value;
    }

    @DrawableRes
    @Nullable
    public Integer getIcon() {
        return icon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Nullable
    public Uri getThumbnail() {
        return thumbnail;
    }

    @DrawableRes
    @Nullable
    public Integer getBadge() {
        return badge;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public boolean isLast() {
        return isLast;
    }

    @Nullable
    public Integer getTitleBackgroundColor() {
        return titleBackgroundColor;
    }

    @StringRes
    @Nullable
    public Integer getPackTitle() {
        return packTitle;
    }
}
