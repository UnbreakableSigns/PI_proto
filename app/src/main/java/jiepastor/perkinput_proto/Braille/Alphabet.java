package jiepastor.perkinput_proto.Braille;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains the Set of keys of a character
 */

public class Alphabet {

    private List<Key> keySet = new ArrayList<>();

    public static final int LOWER_CASE_MODE = 0;
    public static final int UPPER_CASE_MODE = 1;
    public static final int NUMBER_MODE = 2;

    public int CURRENT_MODE = LOWER_CASE_MODE;

    private static final Key defaultKey = new Key("00000000",'\u0000', Key.KeyType.SYMBOL);

    public Alphabet(){
        keySet.add( new Key("10000001",'a', Key.KeyType.LETTER) );
        keySet.add( new Key("11000001",'b', Key.KeyType.LETTER) );
        keySet.add( new Key("10001000",'c', Key.KeyType.LETTER) );
        keySet.add( new Key("10001100",'d', Key.KeyType.LETTER) );
        keySet.add( new Key("10000100",'e', Key.KeyType.LETTER) );
        keySet.add( new Key("11001000",'f', Key.KeyType.LETTER) );
        keySet.add( new Key("11001100",'g', Key.KeyType.LETTER) );
        keySet.add( new Key("11000100",'h', Key.KeyType.LETTER) );
        keySet.add( new Key("01001000",'i', Key.KeyType.LETTER) );
        keySet.add( new Key("01001100",'j', Key.KeyType.LETTER) );
        keySet.add( new Key("10100001",'k', Key.KeyType.LETTER) );
        keySet.add( new Key("11100001",'l', Key.KeyType.LETTER) );
        keySet.add( new Key("10101000",'m', Key.KeyType.LETTER) );
        keySet.add( new Key("10101100",'n', Key.KeyType.LETTER) );
        keySet.add( new Key("10100100",'o', Key.KeyType.LETTER) );
        keySet.add( new Key("11101000",'p', Key.KeyType.LETTER) );
        keySet.add( new Key("11101100",'q', Key.KeyType.LETTER) );
        keySet.add( new Key("11100100",'r', Key.KeyType.LETTER) );
        keySet.add( new Key("01101000",'s', Key.KeyType.LETTER) );
        keySet.add( new Key("01101100",'t', Key.KeyType.LETTER) );
        keySet.add( new Key("10100010",'u', Key.KeyType.LETTER) );
        keySet.add( new Key("11100010",'v', Key.KeyType.LETTER) );
        keySet.add( new Key("01001110",'w', Key.KeyType.LETTER) );
        keySet.add( new Key("10101010",'x', Key.KeyType.LETTER) );
        keySet.add( new Key("10101110",'y', Key.KeyType.LETTER) );
        keySet.add( new Key("10100110",'z', Key.KeyType.LETTER) );
        keySet.add( new Key("01001100",'0', Key.KeyType.NUMBER) );
        keySet.add( new Key("10000001",'1', Key.KeyType.NUMBER) );
        keySet.add( new Key("11000001",'2', Key.KeyType.NUMBER) );
        keySet.add( new Key("10001000",'3', Key.KeyType.NUMBER) );
        keySet.add( new Key("10000100",'4', Key.KeyType.NUMBER) );
        keySet.add( new Key("11001000",'5', Key.KeyType.NUMBER) );
        keySet.add( new Key("11001100",'6', Key.KeyType.NUMBER) );
        keySet.add( new Key("11000100",'7', Key.KeyType.NUMBER) );
        keySet.add( new Key("01001000",'8', Key.KeyType.NUMBER) );
        keySet.add( new Key("01001100",'9', Key.KeyType.NUMBER) );
        keySet.add( new Key("01000001",',', Key.KeyType.SYMBOL) );
        keySet.add( new Key("01100001",';', Key.KeyType.SYMBOL) );
        keySet.add( new Key("01000100",':', Key.KeyType.SYMBOL) );
        keySet.add( new Key("01000110",'.', Key.KeyType.SYMBOL) );
        keySet.add( new Key("01100100",'!', Key.KeyType.SYMBOL) );
        keySet.add( new Key("01100010",'?', Key.KeyType.SYMBOL) );
        keySet.add( new Key("00010001","SPACE", Key.KeyType.GESTURE) );
        keySet.add( new Key("11101110","BACKSPACE", Key.KeyType.GESTURE) );
    }

    public void setMode(int mode){
        CURRENT_MODE = mode;
    }

    public Key getKey(String code){
        Key key = defaultKey;
//        Key.KeyType modeType = CURRENT_MODE == NUMBER_MODE ? Key.KeyType.NUMBER : Key.KeyType.LETTER;

        for (Key k : keySet) {
            if (k.isSameCharacter(code)) {
                key = k;

                if (CURRENT_MODE == NUMBER_MODE && (key.getType().equals(Key.KeyType.NUMBER)))
                    return key;

                if (CURRENT_MODE == UPPER_CASE_MODE)
                    key.setCharacter(Character.toUpperCase(k.getCharacterKey()));

                return key;
            }
        }

        return key;
    }
}
