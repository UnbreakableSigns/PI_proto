package jiepastor.perkinput_proto.Braille;

/**
 * Created by jiepastor on 2/17/2018.
 */

public class Key {
    private Character character = ' ';
    private String pattern;
    private String description;
    private KeyType type;

    public enum KeyType {LETTER, NUMBER, SYMBOL, GESTURE}

    public Key(String pattern, char key, KeyType type){
        character = key;
        description = String.valueOf(key);
        this.pattern = pattern;
        this.type = type;
    }

    public Key(String pattern, String description, KeyType type){
        this.pattern = pattern;
        this.description = description;
        this.type = type;
    }

    public Character getCharacterKey() {
        return character;
    }

    public boolean isSameCharacter(String pattern){
        return pattern.equals(this.pattern);
    }

    public String getDescription() {
        return description;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public KeyType getType() {
        return type;
    }

}