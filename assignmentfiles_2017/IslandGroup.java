import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Collections;
import java.util.ArrayList;

import static java.lang.System.out;

public class IslandGroup
{
    private Random rand_;

    private Population[] islands_;

    public IslandGroup(int n_islands, int n_agents, int n_parents,
            int n_children, double shareRadius, Random rand)
    {
        rand_ = rand;

        islands_ = new Population[n_islands];

        for (int i = 0; i < n_islands; i++)
        {
            Population population =  new Population(n_agents, n_parents,
                    n_children, shareRadius, rand);

            // TODO: Set operators

            population.initFitness();

            islands_[i] = population;
        }
    }

    public void step()
    {
        for (Population island : islands_)
        {
            island.step();
        }
    }

    public int evaluate(ContestEvaluation evaluation, int evals,
            int evaluationLimit)
    {
        int newEvaluations = 0;

        for (Population island : islands_)
        {
            newEvaluations += island.evaluate(evaluation, evals, evaluationLimit);

            if (newEvaluations + evals >= evaluationLimit)
            {
                break;
            }
        }

        return newEvaluations;
    }

    // Migrate individuals between islands.
    public void migrate(int k_migrants)
    {
        // Collect migrants from all islands.
        Agent[][] migrants = new Agent[islands_.length][k_migrants];

        for (int i = 0; i < islands_.length; i++)
        {
            migrants[i] = islands_[i].emigrate(k_migrants);
        }

        // Create a randomly shuffled list of island indices.
        ArrayList<Integer> indices = new ArrayList<Integer>();

        for (int i = 0; i < islands_.length; i++)
        {
            indices.add(i);
        }

        Collections.shuffle(indices);

        // Send groups of migrants to different islands.
        for (int i = 0; i < islands_.length; i++)
        {
            islands_[i].immigrate(migrants[indices.get(i)]);
        }
    }
}
