package gruniozerca;

import java.util.Random;

public class randomLevel {
    public randomLevel(float time, boolean isColor, int level, float grunioSpeed)
    {

    }

    public boolean[] carrotColor(int length)
    {
        Random generator = new Random();
        boolean[] table = new boolean[length];
        for(int i = 0; i < length; i++){
            if(Math.round(generator.nextDouble()) == 1) table[i] = true;
            else table[i] = false;
        }
        return table;
    }
}
