import java.util.Random;
import java.util.Arrays;

import static java.lang.System.out;

// Exception gets thrown when agent's fitness is accessed
// before it is computed.
class FitnessNotComputedException extends Exception 
{ 
    private static final long serialVersionUID = 593880563;

    public FitnessNotComputedException(String s) 
    { 
        super(s); 
    } 
} 

public class Agent implements Comparable<Agent>
{
    // Advantage of best individual in Linear ranking.
    private static double bestAdvantage = 1.5;

    private boolean fitnessComputed_;
    private double fitness_;
	private double[] genotype_;
    private static final int geneLength = 11;

    private Random rand_;

	private double mutationProb_ = 0.05;
    private double survivalProb_ = 0;

	public Agent(Random rand)
	{
        fitnessComputed_ = false;
        fitness_ = 0;

        // Generate a random genotype.
        genotype_ = new double[geneLength];
        for (int i=0; i < genotype_.length; i++)
        {
            genotype_[i] = 10 * (rand.nextDouble() - 0.5);
        }

		rand_ = rand;
	}

    public Agent(Random rand, double[] genotype)
    {
        fitnessComputed_ = false;
        fitness_ = 0;

        genotype_ = genotype;

        rand_ = rand;
    }

	public void mutate()
	{
		// Apply a mutation operator to the genotype with a certain probability.
		if (rand_.nextDouble() < mutationProb_)
		{
			genotype_ = Mutation.addUniform(genotype_, 1);
		}
	}

    public Agent[] crossover(Agent other)
    {
        // Generate new genotypes by using a crossover operator.
        double[][] genotypes = Crossover.onePoint(genotype_, other.getGenotype());

        // Create new agents with the crossover operators.
        Agent[] children = new Agent[genotypes.length];
        
        for (int i = 0; i < genotypes.length; i++)
        {
            children[i] = new Agent(rand_, genotypes[i]);
        }

        return children;
    }

    public double[] getGenotype()
    {
        return genotype_;
    }

	public double[] getFenotype()
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

		return values;
	}

    public void setFitness(double fitness)
    {
        fitness_ = fitness;
        fitnessComputed_ = true;
    }

    public double getFitness() throws FitnessNotComputedException
    {
        if (!fitnessComputed_)
        {
            throw new FitnessNotComputedException("Cannot access fitnees. Fitness not computed.");
        }
        else
        {
            return fitness_;
        }
    }

    public boolean isFitnessComputed()
    {
        return fitnessComputed_;
    }

    public int compareTo(Agent other)
    {
        double myFitness, theirFitness;
        try
        {
            myFitness = getFitness();
            theirFitness = other.getFitness();
        } catch (FitnessNotComputedException e)
        {
            out.println("Not all Agents in comparison have fitness! This is a bug, please fix!");
            return 0;
        }

        if (myFitness > theirFitness)
        {
            return 1;
        }
        else if (myFitness < theirFitness)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
