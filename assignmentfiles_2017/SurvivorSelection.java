import java.util.Random;

public class SurvivorSelection
{
    private static Random rand_ = new Random();

    public static void setSeed(long seed)
    {
        rand_.setSeed(seed);
    }
}
