package kgk.mobile.external;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import javax.inject.Inject;

import kgk.mobile.App;
import kgk.mobile.R;

public final class ImageCreatorAndroid {

    private static final int USER_MARKER_IMAGE_WIDTH_DP = 16;
    private static final int USER_MARKER_IMAGE_HEIGHT_DP = 16;

    @Inject Context context;

    ////

    public ImageCreatorAndroid() {
        App.getComponent().inject(this);
    }

    ////

    @Nullable
    public BitmapDescriptor createUserMarkerImage() {
        Bitmap bitmap = createUserMarkerBitmap();
        if (bitmap == null) return null;
        else return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    ////

    private Bitmap createUserMarkerBitmap() {
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
                R.drawable.user_marker_image, null);

        if (drawable == null) return null;

        Bitmap bitmap = Bitmap.createBitmap(
                dpToPx(USER_MARKER_IMAGE_WIDTH_DP), dpToPx(USER_MARKER_IMAGE_HEIGHT_DP),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }
}
