import java.util.Random;
import java.util.Arrays;

import static java.lang.System.out;

public class Agent
{
	private MutationOperator mutationOp_;
	private CrossoverOperator crossOp_;

    private double fitness_;
	private double[] genotype_;

    private Random rand_;

	private double mutationProb_ = 0.1;

	public Agent(Random rand)
	{
		rand_ = rand;
        genotype_ = new double[10];

        for (int i=0; i < genotype_.length; i++)
        {
            genotype_[i] = 10 * (rand_.nextDouble() - 0.5);
        }
	}

    public Agent(Random rand, double[] genotype)
    {
        rand_ = rand;
        genotype_ = genotype;
    }

	public void mutate()
	{
		// Apply a mutation operator to the genotype.
		if (rand_.nextDouble() < mutationProb_)
		{
			genotype_ = mutationOp_.call(genotype_);
		}
	}

    public Agent[] crossover(Agent other)
    {
        double[][] genotypes = crossOp_.call(genotype_, other.get_genotype());

        Agent[] children = {new Agent(rand_, genotypes[0]), new Agent(rand_, genotypes[1])};
        return children;
    }

    public double[] get_genotype()
    {
        return genotype_;
    }

	public double[] get_fenotype()
	{
		// Map this agent's genotype to a genotype.
        double[] values = Arrays.copyOfRange(genotype_, 0, 10);

        // Clip fenotype values to a range of [-5, 5].
        for (int i=0; i < values.length; i++)
        {
            if (values[i] > 5)
            {
               values[i] = 5;
            }
            else if (values[i] < -5)
            {
                values[i] = -5;
            }
        }

		return genotype_;
	}
}
