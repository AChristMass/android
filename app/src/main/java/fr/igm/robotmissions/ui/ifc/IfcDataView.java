package fr.igm.robotmissions.ui.ifc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import fr.igm.robotmissions.R;
import fr.igm.robotmissions.objects.ifc.IfcData;
import fr.igm.robotmissions.objects.ifc.IfcFloor;
import fr.igm.robotmissions.objects.ifc.IfcPoint;
import fr.igm.robotmissions.ui.utils.OnTapListener;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class IfcDataView extends View implements View.OnTouchListener {

    private static final long TAP_THRESHOLD_MS = 150;
    private final ScaleListener mScaleListener;
    private Paint paint;
    private Paint shadowPaint;
    private IfcData ifcData;
    private String currentFloor;
    private Bitmap bmp;
    private Canvas canvas = new Canvas();
    private Path spacesPath;
    private Path doorsPath;
    private long touchStartTime;


    private ScaleGestureDetector mScaleDetector;

    private float lastX, lastY;
    private Matrix pathMatrix = new Matrix();

    private float[] start;
    private float[] end;
    private float ratio;

    private OnTapListener onTap;

    private int width, height;

    private RectF robotRectF = new RectF();
    private int[] robot;
    private Drawable robotDrawable;
    private int offset = 40;

    public IfcDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        shadowPaint = new Paint(0);
        shadowPaint.setColor(0xff101010);
        shadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
        mScaleListener = new ScaleListener();
        mScaleDetector = new ScaleGestureDetector(context, mScaleListener);
        setOnTouchListener(this);
        robotDrawable = getResources().getDrawable(R.drawable.ic_android_black_24dp);
    }

    public void setIfcData(IfcData ifcData) {
        this.ifcData = ifcData;
        currentFloor = ifcData.getFloorMap().keySet().iterator().next();

        double ifcWidth = ifcData.getDimensions().getWidth();
        double ifcHeight = ifcData.getDimensions().getHeight();

        bmp = Bitmap.createBitmap((int) ifcWidth + offset * 2, (int) ifcHeight + offset * 2, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bmp);
        resetTransformation();
        updatePaths();
        updateView();
    }

    public void resetTransformation() {
        pathMatrix = new Matrix();
        fixMatrix();
    }

    public void drawOnCanvas() {
        if (bmp == null)
            return;
        // clear
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Path totalPath = new Path();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            totalPath.op(spacesPath, doorsPath, Path.Op.UNION);
            canvas.drawPath(totalPath, shadowPaint);
        }
        // draw space inner
        paint.setColor(getResources().getColor(R.color.spaceColor));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        canvas.drawPath(spacesPath, paint);

        // draw door inner
        paint.setColor(getResources().getColor(R.color.doorColor));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        canvas.drawPath(doorsPath, paint);

        // draw walls
        paint.setColor(getResources().getColor(R.color.wallColor));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawPath(totalPath, paint);

        // draw points
        drawPoint(start, getResources().getColor(R.color.startPointColor));
        drawPoint(end, getResources().getColor(R.color.endPointColor));
        drawPoint(robot, getResources().getColor(R.color.wallColor));
    }

    private void drawPoint(int[] point, int color) {
        Log.d("points", "draw " + Arrays.toString(point));
        if (point == null)
            return;
        drawPoint(new float[]{point[0], point[1]}, color);
    }

    private void drawPoint(float[] point, int color) {
        Log.d("points", "draw " + Arrays.toString(point));
        if (point == null)
            return;
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(15 / ratio);
        canvas.drawPoint((float) (point[0] - ifcData.getDimensions().getxMin()),
                (float) (point[1] - ifcData.getDimensions().getyMin()), paint);
    }


    private Path getPath(Collection<ArrayList<IfcPoint>> ifcPointArrayList) {
        Path path = new Path();

        for (ArrayList<IfcPoint> lst : ifcPointArrayList) {
            boolean toMove = true;
            for (IfcPoint p : lst) {
                float x = (float) (p.getX() - ifcData.getDimensions().getxMin());
                float y = (float) (p.getY() - ifcData.getDimensions().getyMin());
                if (toMove) {
                    path.moveTo(x, y);
                    toMove = false;
                } else {
                    path.lineTo(x, y);
                }
            }
        }
        return path;
    }

    private void updatePaths() {
        IfcFloor floor = ifcData.getFloorMap().get(currentFloor);
        spacesPath = getPath(floor.getSpacesPolygons().values());
        doorsPath = getPath(floor.getDoorsPolygons().values());
    }

    private void fixMatrix() {
        if (ifcData == null)
            return;
        double ifcWidth = ifcData.getDimensions().getWidth() + offset;
        double ifcHeight = ifcData.getDimensions().getHeight() + offset;
        if (width > height) {
            ratio = (float) (height / ifcHeight);
        } else {
            ratio = (float) (width / ifcWidth);
        }
        pathMatrix.setScale(ratio, ratio);
        pathMatrix.postScale(1, -1);
        pathMatrix.postTranslate(offset * ratio, (float) (ifcHeight) * ratio);
        mScaleListener.scaleFactor = 1f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bmp != null) {
            canvas.drawBitmap(bmp, pathMatrix, paint);
            if (onTap != null) {
                paint.setColor(Color.BLACK);
                paint.setTextSize(25);
                canvas.drawText("Tap to set position", 10, 25, paint);
            }
        }
    }

    public void setOnTap(OnTapListener onTap) {
        this.onTap = onTap;
    }

    private float[] getPointInIfc(float viewX, float viewY) {

        Matrix inverse = new Matrix();
        pathMatrix.invert(inverse);
        float[] val = new float[]{viewX, viewY};
        inverse.mapPoints(val);

        int ix = (int) val[0];
        int iy = (int) val[1];
        if (0 > ix || ix >= bmp.getWidth() ||
                0 > iy || iy >= bmp.getHeight() ||
                bmp.getPixel(ix, iy) == 0) {
            return null;
        }

        val[0] += ifcData.getDimensions().getxMin();
        val[1] += ifcData.getDimensions().getyMin();
        return val;
    }


    @Override
    public boolean onTouch(View _v, MotionEvent ev) {

        if (ev.getPointerCount() > 1) {
            lastX = -1;
            return mScaleDetector.onTouchEvent(ev);
        } else {
            boolean isTap = false;
            if (onTap != null) {
                long curTime = System.currentTimeMillis();
                if (ev.getAction() == ACTION_DOWN)
                    touchStartTime = curTime;
                long timeSinceTouch = curTime - touchStartTime;
                isTap = ev.getAction() == ACTION_UP && timeSinceTouch < TAP_THRESHOLD_MS;

            }
            if (isTap) {
                float[] ifcPos = getPointInIfc(ev.getX(), ev.getY());
                if (ifcPos == null) {
                    Toast.makeText(getContext(), getResources().getString(R.string.bad_tap), Toast.LENGTH_SHORT).show();
                } else {
                    onTap.onTap(ifcPos);
                }
            } else if (ev.getAction() == ACTION_MOVE && lastX >= 0) {
                float dx = ev.getX() - lastX;
                float dy = ev.getY() - lastY;
                pathMatrix.postTranslate(dx, dy);
                invalidate();
            }
            lastX = ev.getX();
            lastY = ev.getY();
        }
        return true;
    }

    public void updateView() {
        drawOnCanvas();
        invalidate();
    }

    public void focusStart() {
        focusPoint(start);
    }

    public void focusEnd() {
        focusPoint(end);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        resetTransformation();
    }

    private void focusPoint(float[] point) {
        if (point == null)
            return;
        resetTransformation();
        float ifcWidth = (float) ifcData.getDimensions().getWidth();
        float ifcHeight = (float) ifcData.getDimensions().getHeight();
        float xMin = (float) ifcData.getDimensions().getxMin();
        float yMin = (float) ifcData.getDimensions().getyMin();
        Matrix translate = new Matrix();
        float dx = (xMin - point[0] + ifcWidth / 2) * ratio;
        float dy = (yMin - point[1] + ifcHeight / 2) * ratio;
        translate.setTranslate(dx, dy);
        pathMatrix.postConcat(translate);
        updateView();
    }

    public void setRobot(int[] robotPos) {
        this.robot = robotPos;
    }

    public String getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(String currentFloor) {
        this.currentFloor = currentFloor;
        updatePaths();

    }

    public float[] getStart() {
        return start;
    }

    public void setStart(float[] start) {
        this.start = start;
    }

    public float[] getEnd() {
        return end;
    }

    public void setEnd(float[] end) {
        this.end = end;
    }

    public class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        final float MIN_SCALE_FACTOR = 1f;
        final float MAX_SCALE_FACTOR = 2f;
        float lastFocusX, lastFocusY;
        float scaleFactor = 1f;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastFocusX = detector.getFocusX();
            lastFocusY = detector.getFocusY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Matrix transformationMatrix = new Matrix();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();
            float focusShiftX = focusX - lastFocusX;
            float focusShiftY = focusY - lastFocusY;


            //Zoom focus is where the fingers are centered,
            transformationMatrix.postTranslate(-focusX, -focusY);
            float newScaleFactor = detector.getScaleFactor() * scaleFactor;
            if (MIN_SCALE_FACTOR < newScaleFactor && newScaleFactor < MAX_SCALE_FACTOR) {
                transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());
                scaleFactor *= detector.getScaleFactor();
            }

            /* Using getFocusShift allows for scrolling with two pointers down. Remove it to skip this functionality */
            transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY);

            pathMatrix.postConcat(transformationMatrix);

            lastFocusX = detector.getFocusX();
            lastFocusY = detector.getFocusY();
            invalidate();
            return true;
        }

        public float getScaleFactor() {
            return scaleFactor;
        }
    }
}
