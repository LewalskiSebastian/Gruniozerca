package gruniozerca;

import java.util.Random;

import static java.lang.Math.sqrt;


class randomLevel {
    private float carrotSpeed;
    private int numberOfCarrots;
    private boolean[] colors;
    private float[] carrotX;
    public float[] carrotY;
    //int j;
    randomLevel(int level, float grunioSpeed, float levelH)       //Tu mogłem dać jakieś komentarze xd
    {
        carrotSpeed = (float)(sqrt(level))*0.003f;
        if (carrotSpeed > 0.04) carrotSpeed = 0.04f;
        numberOfCarrots = level * 5;
        if (level > 1) colors = carrotColor(numberOfCarrots);               //jak poziom <= 1 to wszystkie marchewki czerwone inaczej losowo
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
        float levelY = 0.4f - level * 0.05f;                                //im większy poziom to tym mniejsze odstępy w osi y będą miedzy marchewami
        if (levelY < 0.01f) levelY = 0.01f;
        carrotY[0] = levelH + levelY + (float)Math.random() * levelY + (Math.abs(carrotX[0] - 0.5f) * grunioSpeed) / carrotSpeed;
        for (int i = 1; i < numberOfCarrots; i++){
            carrotY[i] = carrotY[i-1] + levelY + (float)Math.random() * levelY + (Math.abs(carrotX[i] - carrotX[i - 1]) * grunioSpeed) / carrotSpeed;
        }

    }

    private boolean[] carrotColor(int length)                   //losuje kolorki po prostu, nie wiem po co oddzielną metodę zrobiłem
    {
        Random generator = new Random();
        boolean[] table = new boolean[length];
        for(int i = 0; i < length; i++)
            if (Math.round(generator.nextDouble()) == 1) {
                table[i] = true;
            } else table[i] = false;
        return table;
    }

    boolean[] getCarrotColor()
    {
        return colors;
    }

    float[][] getCarrotsCoordinates()                        //zwraca koordynaty marchewek w tabeli 2 na ilość
    {
        float[][] CarrotsCoordinates = new float[2][numberOfCarrots];
        for (int i = 0; i < numberOfCarrots; i++){
            CarrotsCoordinates[0][i] = carrotX[i];
            CarrotsCoordinates[1][i] = carrotY[i];
        }
        return CarrotsCoordinates;
    }

    int getCarrotsNumber()           //zwraca ilość marchewek (długość tabeli)
    {
        return numberOfCarrots;
    }

    float getCarrotsSpeed()          //zwraca szybkość marchewek
    {
        return carrotSpeed;
    }

}
