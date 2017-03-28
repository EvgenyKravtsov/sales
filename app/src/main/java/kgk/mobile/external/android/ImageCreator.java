package kgk.mobile.external.android;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import kgk.mobile.App;
import kgk.mobile.DependencyInjection;
import kgk.mobile.R;

public final class ImageCreator {

    private static final int USER_MARKER_IMAGE_WIDTH_DP = 16;
    private static final int USER_MARKER_IMAGE_HEIGHT_DP = 16;
    private static final int SALES_OUTLET_MARKER_IMAGE_WIDTH_DP = 32;
    private static final int SALES_OUTLET_MARKER_IMAGE_HEIGHT_DP = 32;

    private final Context context;

    ////

    public ImageCreator(Context context) {
        this.context = context;
    }

    ////

    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, DependencyInjection.provideAppContext().getResources().getDisplayMetrics());
    }

    ////

    @Nullable
    public BitmapDescriptor createUserMarkerImage() {
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
                R.drawable.user_marker_image, null);
        Bitmap bitmap = createBitmap(
                drawable,
                USER_MARKER_IMAGE_WIDTH_DP,
                USER_MARKER_IMAGE_HEIGHT_DP);

        if (bitmap == null) return null;
        else return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Nullable
    public BitmapDescriptor createSalesOutletMarkerImage() {
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
                R.drawable.sales_outlet_marker_image, null);
        Bitmap bitmap = createBitmap(
                drawable,
                SALES_OUTLET_MARKER_IMAGE_WIDTH_DP,
                SALES_OUTLET_MARKER_IMAGE_HEIGHT_DP);

        if (bitmap == null) return null;
        else return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Nullable
    public BitmapDescriptor createSalesOutletEnteredMarkerImage() {
        return createImage(R.drawable.sales_outlet_entered_marker_image,
                SALES_OUTLET_MARKER_IMAGE_WIDTH_DP,
                SALES_OUTLET_MARKER_IMAGE_HEIGHT_DP);
    }

    ////

    private Bitmap createBitmap(Drawable drawable, int widthDp, int heightDp) {
        if (drawable == null) return null;

        Bitmap bitmap = Bitmap.createBitmap(
                dpToPx(widthDp), dpToPx(heightDp),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private BitmapDescriptor createImage(int resourceId, int widthDp, int heightDp) {
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),
                resourceId, null);
        Bitmap bitmap = createBitmap(drawable, widthDp, heightDp);

        if (bitmap == null) return null;
        else return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
