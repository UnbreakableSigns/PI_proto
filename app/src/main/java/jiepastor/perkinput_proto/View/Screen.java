package jiepastor.perkinput_proto.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.constraint.ConstraintLayout;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import jiepastor.perkinput_proto.Touch.TouchPointer;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Layout of the view
 */

public class Screen extends ConstraintLayout {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private boolean isLayoutSet = false;
    private Context context;

    private List<Dot> dotLayout = new ArrayList<>();
    private String label = "";

    private List<TouchPointer> listOfCurrentPointers = new ArrayList<>();


    //Layout constructor
    public Screen(Context context) {
        super(context);
        this.context = context;
        setBackgroundColor(Color.BLACK);
        setAlpha(0.6f);

//        get screen width and height
//        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        height = size.y;
//        width = size.x;

    }

    public void setReferencePoints(PointF p1, PointF p2, PointF p3, PointF p4){
        removeAllViewsInLayout();

        PointF[] dotCoordinates = new PointF[]{p1,p2,p3,p4};

        //add each dot to view
        for (int n = 0; n < 4; n++) {
            Dot dot = new Dot(context, dotCoordinates[n].x, dotCoordinates[n].y, n + 1);

            dotLayout.add(dot);
            addView(dot);
        }

        isLayoutSet = true;

    }

    public void setCurrentTouchPoints(List<TouchPointer> p) {
        listOfCurrentPointers = p;
        invalidate();
    }

    public void setLabelText(String text){
        label = text;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        //draw current pointers
        Paint paint = new Paint();
        for (TouchPointer point : listOfCurrentPointers) {
            if (point.isPointActive()) {
                paint.setColor(Color.WHITE);
                canvas.drawCircle(point.getCoordinates().x, point.getCoordinates().y, 50, paint);
            }
        }

        Paint textPaint = new Paint();
        textPaint.setTextSize(100);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(label,(canvas.getWidth() / 2),(canvas.getHeight() / 6),textPaint);//((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2))
    }

    public boolean isLayoutSet() {
        return isLayoutSet;
    }
}