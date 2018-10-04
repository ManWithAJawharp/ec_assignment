import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Properties;
import java.util.Arrays;

import static java.lang.System.out;

public class player63 implements ContestSubmission
{
	Random rnd_;
	private ContestEvaluation evaluation_;
	private int evaluations_limit_;
	
    private Population population_;

	public player63()
	{
		rnd_ = new Random();
	}
	
	public void setSeed(long seed)
	{
		// Set seed of algortihms random process
		rnd_.setSeed(seed);

        ParentSelection.setSeed(seed + 1);
        Mutation.setSeed(seed + 2);
        Crossover.setSeed(seed + 3);
        SurvivorSelection.setSeed(seed + 4);
	}

	public void setEvaluation(ContestEvaluation evaluation)
	{
		// Set evaluation problem used in the run
		evaluation_ = evaluation;
		
		// Get evaluation properties
		Properties props = evaluation.getProperties();
        // Get evaluation limit
        evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
		// Property keys depend on specific evaluation
		// E.g. double param = Double.parseDouble(props.getProperty("property_name"));
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

		// Do sth with property values, e.g. specify relevant settings of your algorithm
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }
    }
    
	public void run()
	{
        int evals = 0;

        // Initialize population.
        // out.println("\nInitialize the population");
        // out.println(evaluation_);
        population_ = new Population(100, 3, rnd_);

        // Maybe assign random fitness to first generation.
        // They are unlikely to be very good and it gives us a free 100
        // evaluations further on.
        population_.initFitness();

        // out.println("Run evolution");
        // calculate fitness
        while(evals < evaluations_limit_)
        {
            // Select parents.
            population_.selectParents(10);

            // Apply crossover, create offspring and apply  mutation
            // operators.
            population_.createOffspring();

            // Select survivors
            population_.trimPopulation();

            // Check fitness of unknown function.
            int evaluatedAgents = population_.evaluate(evaluation_, evals, evaluations_limit_);
            evals += evaluatedAgents;

            // out.println(Double.toString(population_.getAverageFitness()));
        }

        double[] genotype = population_.getBestGenotype();
        // out.println(Arrays.toString(genotype));
        out.println(population_.getBestFitness());
	}
}
