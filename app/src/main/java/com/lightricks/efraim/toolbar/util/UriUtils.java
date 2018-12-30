// Copyright (c) 2018. Lightricks. All rights reserved.
// Created by Efraim Globus.

package com.lightricks.efraim.toolbar.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;

public class UriUtils {
    /**
     * Creates a {@link Uri} for a project resource.
     */
    public static Uri getResourceUri(@NonNull Context context,
                                     @AnyRes int resourceId) {
        Resources resources = context.getResources();
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resourceId))
                .appendPath(resources.getResourceTypeName(resourceId))
                .appendPath(resources.getResourceEntryName(resourceId))
                .build();
    }
}
