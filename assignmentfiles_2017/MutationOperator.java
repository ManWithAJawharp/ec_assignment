import java.util.Random;

public class MutationOperator
{
    private Random rand;

	public MutationOperator(Random rand)
	{
        this.rand = rand;
	}

	public double[] call(double[] genotype)
	{
        return simpleSwap(genotype);
	}

    // Swap two genes in the genotype.
    public double[] simpleSwap(double[] genotype)
    {
        int position_a = rand.nextInt(genotype.length);
        double value_a = genotype[position_a];

        int position_b = (position_a + rand.nextInt(genotype.length)) %
            genotype.length;
        double value_b = genotype[position_b];

        genotype[position_a] = value_b;
        genotype[position_b] = value_a;

		return genotype;
    }
}
