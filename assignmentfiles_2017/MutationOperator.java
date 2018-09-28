import java.util.Random;

import java.lang.Math;

public class MutationOperator
{
    private Random rand_;

	public MutationOperator(Random rand)
	{
        rand_ = rand;
	}

	public double[] call(double[] genotype)
	{
        return addUniform(genotype, genotype[10]);
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

    public double[] addUniform(double[] genotype, double sigma)
    {
        sigma = Math.abs(sigma);

        for (int i = 0; i < genotype.length; i++)
        {
            double uniform = sigma * (rand_.nextDouble() - 0.5);

            genotype[i] += uniform;
        }

        return genotype;
    }

    public double[] addGaussian(double[] genotype, double sigma)
    {
        sigma = Math.abs(sigma);

        for (int i = 0; i < genotype.length; i++)
        {
            double gaussian = sigma * rand_.nextGaussian();

            genotype[i] += gaussian;
        }

        return genotype;
    }
}
