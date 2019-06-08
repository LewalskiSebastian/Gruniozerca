package gruniozerca;

import java.util.Random;

public class randomLevel {
    float carrotSpeed;
    int numberOfCarrots;
    boolean[] colors;
    float[] carrotX;
    float[] carrotY;
    float levelHeight;
    //int j;
    public randomLevel(int level, float grunioSpeed, float levelH)       //Tu mogłem dać jakieś komentarze xd
    {
        levelHeight = levelH;
        carrotSpeed = level*0.0015f;
        if (carrotSpeed > 0.02) carrotSpeed = 0.02f;
        numberOfCarrots = level * 5;
        if (level > 2) colors = carrotColor(numberOfCarrots);               //jak poziom > 2 to wszystkie marchewki czerwone inaczej losowo
        else {
            colors = new boolean[numberOfCarrots];
            for (int i = 0; i < numberOfCarrots; i++){
                colors[i] = true;
            }
        }
        carrotX = new float[numberOfCarrots];                       //losowa pozycja w osi x kolejnych marchewek
        for (int i = 0; i < numberOfCarrots; i++){
            carrotX[i] = (float)Math.random();
        }
        carrotY = new float[numberOfCarrots];                       //a tutaj w osi y pozycja marchewek, tak żeby się dało dobiec
        float levelY = 0.5f - level * 0.01f;                                //im większy poziom to tym mniejsze odstępy w osi y będą miedzy marchewami
        if (levelY < 0.01f) levelY = 0.01f;
        carrotY[0] = levelHeight + levelY + (float)Math.random() * levelY + (Math.abs(carrotX[0] - 0.5f) * grunioSpeed) / carrotSpeed;
        for (int i = 1; i < numberOfCarrots; i++){
            carrotY[i] = carrotY[i-1] + levelY + (float)Math.random() * levelY + (Math.abs(carrotX[i] - carrotX[i - 1]) * grunioSpeed) / carrotSpeed;
        }

    }

    private boolean[] carrotColor(int length)                   //losuje kolorki po prostu, nie wiem po co oddzielną metodę zrobiłem
    {
        Random generator = new Random();
        boolean[] table = new boolean[length];
        for(int i = 0; i < length; i++){
            if(Math.round(generator.nextDouble()) == 1) table[i] = true;
            else table[i] = false;
        }
        return table;
    }

    public float[][] getCarrotsCoordiantes()                        //zwraca koordynaty marchewek w tabeli 2 na ilość
    {
        float[][] CarrotsCoordiantes = new float[2][numberOfCarrots];
        /*
        j = 0;
        for (int i = 0; i < numberOfCarrots; i++){
            if(carrotY[i] > (pastY-levelHeight) && carrotY[i] < (pastY-levelHeight+1)){
                CarrotsCoordiantes[0][i] = carrotX[j];
                CarrotsCoordiantes[1][i] = carrotY[j]-pastY;
                j++;
            }
        }
         */
        for (int i = 0; i < numberOfCarrots; i++){
            CarrotsCoordiantes[0][i] = carrotX[i];
            CarrotsCoordiantes[1][i] = carrotY[i];
            //System.out.print("i=" + i + " x=" + carrotX[i] + " y=" + carrotY[i] + "\n");
        }
        return CarrotsCoordiantes;
    }

    public int getCarrotsNumber()           //zwraca ilość marchewek (długość tabeli)
    {
        return numberOfCarrots;
    }

    public float getCarrotsSpeed()          //zwraca szybkość marchewek
    {
        return carrotSpeed;
    }

}
