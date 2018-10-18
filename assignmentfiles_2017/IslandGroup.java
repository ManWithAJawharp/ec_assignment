import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Collections;
import java.util.ArrayList;

import java.lang.StringBuilder;
import static java.lang.System.out;

public class IslandGroup
{
    private Random rand_;

    private Population[] islands_;

    public IslandGroup(int n_islands, int n_agents, int n_parents,
            int n_children, int tournamentSize, double shareRadius, double expectedOffspring,
            Random rand, boolean randomSelectionOp)
    {
        rand_ = rand;

        islands_ = new Population[n_islands];

        Selection.Operator selectionOp;
        selectionOp = Selection.Operator.ROUNDROBIN;

        for (int i = 0; i < n_islands; i++)
        {
            if (randomSelectionOp)
            {
                // Select an integer from 1 to 3.
                int selection = rand_.nextInt(3) + 1;

                selectionOp = Selection.Operator.values()[selection];
            }

            Population population = new Population(n_agents, n_parents,
                n_children, tournamentSize, shareRadius, expectedOffspring, rand,
                selectionOp);

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

    public void printIslandStats(boolean islandBest, boolean islandAvg)
    {
        double topBest = 0;
        double topAverage = 0;

        StringBuilder sb = new StringBuilder();

        double[] bestFitnesses = new double[islands_.length];
        double[] avgFitnesses = new double[islands_.length];

        for (int i = 0; i < islands_.length; i++)
        {
            double best = islands_[i].getBestFitness();
            double average = islands_[i].getAverageFitness();

            if (best > topBest)
            {
                topBest = best;
            }

            if (average > topAverage)
            {
                topAverage = average;
            }

            bestFitnesses[i] = best;

            if (average < 0)
            {
                average = 0;
            }
            avgFitnesses[i] = average;
        }

        sb.append("best_fitness: ");
        sb.append(topBest);
        sb.append("\n");
        sb.append("average_fitness: ");
        sb.append(topAverage);
        sb.append("\n");

        for (int i = 0; i < islands_.length; i++)
        {
            sb.append("best_fitness_");
            sb.append(i);
            sb.append(": ");
            sb.append(bestFitnesses[i]);
            sb.append("\n");
            sb.append("average_fitness_");
            sb.append(i);
            sb.append(": ");
            sb.append(avgFitnesses[i]);
            
            if (i < islands_.length - 1)
            {
                sb.append("\n");
            }
        }

        out.println(sb);
    }
}
