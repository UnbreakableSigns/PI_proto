/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiepastor.perkinput_proto.Touch;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jiepastor
 */
public class Interpreter implements iInterpreter{
    List<PointF> calibratedPoints = new ArrayList<>();
    boolean _inLandscape; // Whether the view is in landscape orientation or not
    HashMap<Integer,List<Layout>> _layouts = new HashMap<>(); // Possible layouts
    public static final int TOTAL_FINGERS = 4;

    public Interpreter(){
        for(int i = 0; i < TOTAL_FINGERS; i++) {
            List<Layout> layouts = generateLayoutsWithFingersTotalAndFingersDown(TOTAL_FINGERS, i);
            _layouts.put(i,layouts);
        }
    }

    private List<Layout> generateLayoutsWithFingersTotalAndFingersDown(int fingersTotal, int fingersDown){
        // Base cases
//        System.out.println("generateLayoutsWithFingersTotalAndFingersDown - " + fingersDown + "/" + fingersTotal );

        if (fingersDown == 0) {
            // Return an array with one element, where no fingers are down.
            Layout layout = new Layout(fingersTotal);
            return Arrays.asList(new Layout[]{layout});
        } else if(fingersTotal == 0) {
            // Return an empty array of layouts.
            return new ArrayList<>();
        }

        // Recursive cases
        List<Layout> set1Suffixes = generateLayoutsWithFingersTotalAndFingersDown(fingersTotal - 1,fingersDown);
        int set1Length = set1Suffixes.size();

        List<Layout> set2Suffixes = generateLayoutsWithFingersTotalAndFingersDown(fingersTotal - 1,fingersDown - 1);
        int set2Length = set2Suffixes.size();

        // Create a new NSMutableArray.
        List<Layout> layoutArray = new ArrayList<>(set1Length + set2Length);

        // Add the first set if syffuxes, where the first position of each layout is NOT down.
        for(int i = 0; i < set1Length; ++i) {
            // Create a new layout (no fingers are down by default).
            Layout layout = new Layout(fingersTotal);

            // Set each layout position
            for(int pos = 1; pos < fingersTotal; ++pos) {
                if(set1Suffixes.get(i).isFingerDownAtIndex(pos - 1)){
                    layout.setFingerDownAtIndex(pos);
                }
            }

            // Add the layout to the array of layouts
            layoutArray.add(layout);
        }

        // Second set. the first position of each layout is DOWN.
        for(int i = 0; i < set2Length; ++i) {
            // Create a new layout and set the first finger down this time.
            Layout layout = new Layout(fingersTotal);
            layout.setFingerDownAtIndex(0);
            
            // Set each layout position
            for(int pos = 1; pos < fingersTotal; ++pos) {
                if(set2Suffixes.get(i).isFingerDownAtIndex(pos - 1)){
                    layout.setFingerDownAtIndex(pos);
                }
            }
            // Add the layout to the array of layouts
            layoutArray.add(layout);
        }

        return layoutArray;
    }
    
    @Override
    public void clearCalibrationPoints(){
        calibratedPoints.clear();
    }
    
    @Override
    public void interpretLongPress(List<PointF> touches){
        if (touches.size() == TOTAL_FINGERS) {
         calibrateWithPoints(touches);
        }
    }
        
    @Override
    public String interpretShortPress(List<PointF> touches){

        if(calibratedPoints.isEmpty()){
            System.out.println("Screen has not been calibrated");
            System.out.println(touches.size() + " fingers down. Hold 4 fingers down to calibrate");
            return "----";
        }

        Collections.sort(touches,sortTouches);
        int numFingers = touches.size();

        List<Layout> layouts = _layouts.get(numFingers);
        if(layouts == null)
            return "error";
        int count = layouts.size();

        double minError = Double.MAX_VALUE;
        Layout bestLayout = null;

        for (int i = 0; i < count; i++) {
            Layout currentLayout = layouts.get(i);
            double error = currentLayout.getErrorForTouchesWithCalibrationPoints(touches, calibratedPoints);

            if (error < minError) { // Update the best layout
                minError = error;
                bestLayout = currentLayout;
            }
        }    
        return bestLayout.toString();
    }
    
    private void calibrateWithPoints(List<PointF> touches){
        PointF ranges = getRange(touches);
        _inLandscape = ranges.y < ranges.x;
        System.out.println("Calibrating: " +  (_inLandscape ? "Landscape" : "Portrait"));
        calibratedPoints = touches;
        Collections.sort(calibratedPoints,sortTouches);
    }

    private PointF getRange(List<PointF> touches) {
        float minY = Float.POSITIVE_INFINITY, minX = Float.POSITIVE_INFINITY, maxY = 0, maxX = 0;

        for (PointF touch : touches) {
            float x = touch.x;
            float y = touch.y;

            if(x < minX) { minX = x; }
            if(x > maxX) { maxX = x; }
            if(y < minY) { minY = y; }
            if(y > maxY) { maxY = y; }
        }
        return new PointF(maxX - minX, maxY - minY);
    }

    Comparator<PointF> sortTouches = new Comparator<PointF>() {
        @Override
        public int compare (PointF p1, PointF p2){
            if(_inLandscape)
                return Float.compare(p1.x, p2.x);
            else
                return Float.compare(p1.y, p2.y);
        }
    };

}