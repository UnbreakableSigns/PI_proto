package jiepastor.perkinput_proto.Keyboard;

import android.content.Context;
import android.graphics.PointF;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import jiepastor.perkinput_proto.Braille.Alphabet;
import jiepastor.perkinput_proto.Braille.Key;
import jiepastor.perkinput_proto.Touch.Interpreter;
import jiepastor.perkinput_proto.Touch.TouchPointer;
import jiepastor.perkinput_proto.Touch.iInterpreter;
import jiepastor.perkinput_proto.View.Screen;

/**
 * Created by jiepastor on 2/4/2018.
 */

public class IME extends InputMethodService {
    boolean _calibrated = false; // Whether the touch has already been handled or not
    String _curString = ""; // Current code being typed
    iInterpreter interpreter; // Convert the touch to a layout

    Alphabet alphabet = new Alphabet();
    public enum Gesture{ SPACE, BACKSPACE, EXIT, NONE}

    //View
    private Screen screenView;

    //Touch
    private HashMap<Integer, TouchPointer> currentPointers = new HashMap<>();// Current touch events on the screen

    //additional
    private Speech speech;
    private Vibrator vibrator = null;

    @Override
    public View onCreateInputView() {
        screenView = new Screen(this);
        screenView.setOnTouchListener(touchListener);
        speech = new Speech(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        interpreter = new Interpreter();
        return screenView;
    }

    //public View.onTouchListener touchListener;
    public View.OnTouchListener touchListener = new View.OnTouchListener() {
        List<PointF> pointsToSend = new ArrayList<>();

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getActionMasked();
            int pointerIndex = event.getActionIndex();
            int pointerId = event.getPointerId(pointerIndex);

            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    //move current pointer position
                    for(int i = 0; i < event.getPointerCount(); i++){
                        TouchPointer touch = currentPointers.get(event.getPointerId(i));
                        touch.onTouchMove(new PointF(event.getX(i), event.getY(i)));
                    }

                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN:
                    TouchPointer touchPointer = currentPointers.get(pointerId);
                    if (touchPointer == null) {
                        touchPointer = new TouchPointer();
                        currentPointers.put(pointerId, touchPointer);
                    }
                    touchPointer.onTouchDown(pointerId, new PointF(event.getX(pointerIndex), event.getY(pointerIndex)));
                    calibrationHandler.postDelayed(calibrationRunnable, 500);

                    pointsToSend = new ArrayList<>();
                    for(TouchPointer tp : currentPointers.values()){
                        pointsToSend.add(tp.getCoordinates());
                    }

                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    calibrationHandler.removeCallbacks(calibrationRunnable);

                    if(!_curString.isEmpty())//2nd touch

                    {
                        _curString += interpreter.interpretShortPress(pointsToSend);
                        sendKeyCharacter();
                        _curString = "";
                    }
                    else //1st touch
                    {
                        if(pointsToSend.size()<4) {
                            _curString = interpreter.interpretShortPress(pointsToSend);
                            System.out.println("1st touch");
                        }
                    }

                case MotionEvent.ACTION_POINTER_UP:
                    //remove current pointer
                    currentPointers.remove(pointerId);
                    break;
            }
            screenView.setCurrentTouchPoints(new ArrayList(currentPointers.values()));
            return true;

        }
    };

    //Sends character to input connection.
    //Called when all fingers are up.
    public void sendKeyCharacter(){
        Key key = alphabet.getKey(_curString);
        Character character = key.getCharacterKey();
        String desc = key.getDescription();


        if(character != '\u0000' && !key.getType().equals(Key.KeyType.GESTURE)) {//letter, number or symbol
            InputConnection ic = getCurrentInputConnection();
            ic.commitText(String.valueOf(character), 1);
        }
        else if (key.getType().equals(Key.KeyType.GESTURE)) {//gesture
            {
                Gesture currGesture = Gesture.NONE;

                if (key.getDescription().equals("SPACE"))
                    currGesture = Gesture.SPACE;
                else if (key.getDescription().equals("BACKSPACE"))
                    currGesture = Gesture.BACKSPACE;
                else if (key.getDescription().equals("EXIT"))
                    currGesture = Gesture.EXIT;

                sendGesture(currGesture);
            }
        }
        else {//invalid
            desc = "INVALID";
            vibrator.vibrate(500);
        }

        speech.speakText(desc);
        screenView.setLabelText(desc);
    }

    //Handles calibration of keyboard
    private final Handler calibrationHandler = new Handler();
    private final Runnable calibrationRunnable = new Runnable() {
        public void run() {

            List<PointF> listOfRefPointers = new ArrayList<>();

            //collect all active current pointers
            for(TouchPointer touchPointer : currentPointers.values()){
                if(touchPointer.isPointActive()) {
                    listOfRefPointers.add(touchPointer.getCoordinates());
                }
            }

            //calibrate keyboard
            if(listOfRefPointers.size() == 4) {
                screenView.setReferencePoints( listOfRefPointers.get(0), listOfRefPointers.get(1), listOfRefPointers.get(2), listOfRefPointers.get(3));

                if (screenView.isLayoutSet()){
                    interpreter.interpretLongPress(listOfRefPointers);
                    speech.speakText("Calibrated.");
                    _calibrated = true;
                    vibrator.vibrate(100);
                }
            }
        }
    };


    public void sendGesture(Gesture g){
        InputConnection ic = getCurrentInputConnection();

        switch(g){
            case SPACE:
                ic.commitText(" ",1);

                break;
            case BACKSPACE:
                ic.deleteSurroundingText(1,0);
                break;
            case EXIT:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                break;
        }
    }
}
