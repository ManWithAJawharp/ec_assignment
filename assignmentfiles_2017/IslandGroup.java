import org.vu.contest.ContestEvaluation;

import java.util.Random;

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
            island.selectParents();

            island.createOffspring();

            island.trimPopulation();
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
    public void migrate()
    {
    }
}
