package jiepastor.perkinput_proto.Touch;

import android.graphics.PointF;

/**
 * A point when user touches the screen
 */

public class TouchPointer{
    private boolean isPointActive = false;
    private PointF point;
    private int id;

    public void onTouchDown(int id, PointF point){
        this.id = id;
        this.point = point;
        isPointActive = true;
        //System.out.println("pointer " + id + ": (" + point.x + ", " + point.y + ")");
    }

    public void onTouchMove(PointF point){
        this.point = point;
    }

    public boolean isPointActive(){
        return isPointActive;
    }

    public PointF getCoordinates(){
        return point;
    }

    public int getId(){
        return id;
    }
}
