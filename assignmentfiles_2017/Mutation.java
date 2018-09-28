import java.util.Random;

public class Mutation
{
    private static Random rand_ = new Random();

    public static void setSeed(long seed)
    {
        rand_.setSeed(seed);
    }
    //
    // Swap two genes in the genotype.
    public static double[] simpleSwap(double[] genotype)
    {
        int position_a = rand_.nextInt(genotype.length);
        double value_a = genotype[position_a];

        int position_b = (position_a + rand_.nextInt(genotype.length)) %
            genotype.length;
        double value_b = genotype[position_b];

        genotype[position_a] = value_b;
        genotype[position_b] = value_a;

		return genotype;
    }

    public static double[] addUniform(double[] genotype)
    {
        for (int i = 0; i < genotype.length; i++)
        {
            double uniform = rand_.nextDouble() - 0.5;

            genotype[i] += uniform;
        }

        return genotype;
    }

    public static double[] addGaussian(double[] genotype)
    {
        // Determine a sgima from the genotype.
        double sigma = 0.5;

        for (int i = 0; i < genotype.length; i++)
        {
            double gaussian = sigma * rand_.nextGaussian();

            genotype[i] += gaussian;
        }

        return genotype;
    }
}
