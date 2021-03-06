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
    private int tournamentSize;

    private double mutationProb;
    private double fitnessSharing; // Fitness sharing radius.
    private double expectedOffspring; // Linear ranking parameter.

    private boolean randomSelectionOp;
    private Selection.Operator selectionOp;

	public player63()
	{
		rnd_ = new Random();
	}
	
	public void setSeed(long seed)
	{
		// Set seed of algortihms random process
		rnd_.setSeed(seed);

        Mutation.setSeed(seed + 1);
        Crossover.setSeed(seed + 2);
        Selection.setSeed(seed + 3);
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
        tournamentSize = Integer.parseInt(System.getProperty("tournamentSize", "10"));

        mutationProb = Double.parseDouble(System.getProperty("mutationProb", "0.1"));
        fitnessSharing = Double.parseDouble(System.getProperty("fitnessSharing", "0"));
        expectedOffspring = Double.parseDouble(System.getProperty("expectedOffspring", "2"));

        randomSelectionOp = Boolean.parseBoolean(System.getProperty("randomSelectionOp", "false"));
        selectionOp = Selection.Operator.values()[
            Integer.parseInt(System.getProperty("selectionOp", "2"))];
    }
    
	public void run()
	{
        int evals = 0;

        Agent.mutationProb_ = mutationProb;
        Agent.mutationStepSize_ = 1 / Math.sqrt(2 * Math.sqrt(n_agents));
        Agent.mutationStepSizePrime_ = 1 / Math.sqrt(2 * n_agents);

        // Initialize population.
        islands_ = new IslandGroup(n_islands, n_agents, n_parents, n_children, tournamentSize,
                fitnessSharing, expectedOffspring, rnd_, randomSelectionOp, selectionOp);
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
