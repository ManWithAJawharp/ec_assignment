import java.util.Random;

public class MutationOperator
{
    private Random rand_;

	public MutationOperator(Random rand)
	{
        rand_ = rand;
	}

	public double[] call(double[] genotype)
	{
        return addUniform(genotype);
	}

    // Swap two genes in the genotype.
    public double[] simpleSwap(double[] genotype)
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

    public double[] addUniform(double[] genotype)
    {
        for (int i = 0; i < genotype.length; i++)
        {
            double uniform = rand_.nextDouble() - 0.5;

            genotype[i] += uniform;
        }

        return genotype;
    }

    public double[] addGaussian(double[] genotype)
    {
        // Determine a sgima from the genotype.
        float sigma = 1;

        for (int i = 0; i < genotype.length; i++)
        {
            double gaussian = sigma * rand_.nextGaussian();

            genotype[i] += gaussian;
        }

        return genotype;
    }
}
