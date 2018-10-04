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

    // Add uniformly distributed values to all genes.
    // Values fall in the range [-sigma, sigma].
    public static double[] addUniform(double[] genotype, double sigma)
    {
        // Sigma must be a positive value.
        sigma = Math.abs(sigma);

        for (int i = 0; i < genotype.length; i++)
        {
            double uniform = sigma * (rand_.nextDouble() - 0.5);

            genotype[i] += uniform;
        }

        return genotype;
    }

    // Add gaussian distributed values to all genes.
    // Gaussian mean is 0 and standard deviation equals
    // sigma.
    public static double[] addGaussian(double[] genotype, double sigma, double learningRate)
    {
        // Sigma must be a positive value.
        sigma = sigma * Math.exp(learningRate * rand_.nextGaussian());

        for (int i = 0; i < genotype.length; i++)
        {
            double gaussian = sigma * rand_.nextGaussian();

            genotype[i] += gaussian;
        }

        return genotype;
    }
}
