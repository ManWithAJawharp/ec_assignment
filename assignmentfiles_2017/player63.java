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

    // Algorithm parameters.
    private int n_islands;
    private int n_migrants;
    private int epoch_length; // Number of generations before migrating.

    private int n_agents;
    private int n_children;
    private int n_parents;

    private double fitnessSharing; // Fitness sharing radius.

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
        boolean isMultimodal = Boolean.parseBoolean(props.getProperty("Multimodal"));
        boolean hasStructure = Boolean.parseBoolean(props.getProperty("Regular"));
        boolean isSeparable = Boolean.parseBoolean(props.getProperty("Separable"));

		// Do sth with property values, e.g. specify relevant settings of your algorithm
        if(isMultimodal){
            // Do sth
        }else{
            // Do sth else
        }

        // Get custom algorithm properties.
        n_islands = Integer.parseInt(System.getProperty("islands", "2"));
        n_migrants = Integer.parseInt(System.getProperty("migrants", "5"));
        epoch_length = Integer.parseInt(System.getProperty("epoch", "100"));
        
        n_agents = Integer.parseInt(System.getProperty("agents", "100"));
        n_children = Integer.parseInt(System.getProperty("children", "50"));
        n_parents = Integer.parseInt(System.getProperty("parents", "50"));

        fitnessSharing = Double.parseDouble(System.getProperty("fitnessSharing", "0"));
    }
    
	public void run()
	{
        int evals = 0;

        Agent.mutationProb_ = 0.1;
        Agent.mutationStepSize_ = 1 / Math.sqrt(2 * Math.sqrt(n_agents));
        Agent.mutationStepSizePrime_ = 1 / Math.sqrt(2 * n_agents);

        // Initialize population.
        islands_ = new IslandGroup(n_islands, n_agents, n_parents, n_children,
                fitnessSharing, rnd_);
        evals += islands_.evaluate(evaluation_, evals, evaluations_limit_);

        int generations = 0;

        while (evals < evaluations_limit_)
        {
            islands_.step();

            // Check fitness of unknown function.
            evals += islands_.evaluate(evaluation_, evals, evaluations_limit_);

            islands_.printIslandStats(true, true);

            if (n_islands > 0)
            {
                if (generations % epoch_length == 0)
                {
                    islands_.migrate(n_migrants);
                }

                generations++;
            }
        }
	}
}
