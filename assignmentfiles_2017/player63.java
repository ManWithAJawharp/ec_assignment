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
    private IslandGroup islands_;

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

        int n_islands = 2;
        int n_agents = 200;
        int n_parents = 100;
        int n_children = 100;
        double fitnessSharing = 0;
        double expectedOffspring = 1.5;
        int n_migrants = 10;
        int epoch = 100;

        Agent.mutationProb_ = 0.1;
        Agent.mutationStepSize_ = 1 / Math.sqrt(2 * Math.sqrt(n_agents));
        Agent.mutationStepSizePrime_ = 1 / Math.sqrt(2 * n_agents);

        // Initialize population.
        // out.println("\nInitialize the population");
        // out.println(evaluation_);
        islands_ = new IslandGroup(n_islands, n_agents, n_parents, n_children,
                fitnessSharing, expectedOffspring, rnd_);

        int generations = 0;

        // out.println("Run evolution");

        // Calculate fitness
        while(evals < evaluations_limit_)
        {
            islands_.step();

            // Check fitness of unknown function.
            evals += islands_.evaluate(evaluation_, evals, evaluations_limit_);

            islands_.printIslandStats(true, true);

            if (n_islands > 0)
            {
                if (generations % epoch == 0)
                {
                    islands_.migrate(n_migrants);
                }

                generations++;
            }
        }
	}
}
