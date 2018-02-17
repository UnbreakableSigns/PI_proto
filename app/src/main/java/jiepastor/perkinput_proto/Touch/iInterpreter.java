/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jiepastor.perkinput_proto.Touch;

import android.graphics.PointF;

import java.util.List;

import jiepastor.perkinput_proto.Touch.TouchPointer;

/**
 *
 * @author jiepastor
 */
public interface iInterpreter {
    
    void clearCalibrationPoints();

    void interpretLongPress(List<PointF> touches); // Calibration Touch

    String interpretShortPress(List<PointF> touches); // Short Press

}
