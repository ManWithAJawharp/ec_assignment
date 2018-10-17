import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public final class Selection
{
    private static Random rand_ = new Random();

    public static void setSeed(long seed)
    {
        rand_.setSeed(seed);
    }

    public static Agent[] truncation(int k, Agent[] agents)
    {
        Population.sortAgents(agents);

        return Arrays.copyOfRange(agents, agents.length - k, agents.length);
    }

    public static Agent[] tournament(int k, int tournamentSize, Agent[] agents)
    {
        Agent[] survivors = new Agent[k];

        ArrayList<Agent> agentsList = new ArrayList<>(Arrays.asList(agents));

        for (int i = 0; i < k; i++)
        {
            Collections.shuffle(agentsList);

            Agent[] subset = new Agent[tournamentSize];
            for (int j = 0; j < tournamentSize; j++)
            {
                subset[j] = agentsList.get(j);
            }

            Population.sortAgents(subset);

            survivors[i] = agentsList.get(agentsList.size() - 1);
            agentsList.remove(agentsList.size() - 1);
        }

        return survivors;
    }

    public static Agent[] linearRanking(int k, double s, Agent[] agents)
    {
        int populationSize = agents.length;
        double[] selectionProbabilities = new double[populationSize];

        Population.sortAgents(agents);

        double intercept = (2 - s) / populationSize;

        for (int i = 0; i < populationSize; i++)
        {
            selectionProbabilities[i] = intercept + 2* i * (s -1)
                / (populationSize * (populationSize - 1));
        }

        Agent[] survivors = roulette(k, selectionProbabilities, agents);

        return survivors;
    }

    public static Agent[] roundRobin(int k, int q, Agent[] agents)
    {
        Population.sortAgents(agents);
        
        AgentRobin[] results = new AgentRobin[agents.length];
        int[] winnings = new int[agents.length];

        // Iterate through all agents and have them compete with random
        // opponents.
        for (int i = 0; i < agents.length; i++)
        {
            int wins = 0;
            int[] opponentIndices = Population.randomSelection(agents, q);

            double agentFitness;
            try
            {
                agentFitness = agents[i].getFitness();
            } catch (FitnessNotComputedException e)
            {
                results[i] = new AgentRobin(agents[i], 0);
                continue;
            }

            // Randomly select q opponents for each agent and compete with the
            // agent.
            for (int j = 0; j < opponentIndices.length; j++)
            {
                double opponentFitness;

                try
                {
                    opponentFitness = agents[j].getFitness();
                }
                catch (FitnessNotComputedException e)
                {
                    wins++;
                    continue;
                }

                if (agentFitness >= opponentFitness)
                {
                    wins++;
                }
            }

            results[i] = new AgentRobin(agents[i], wins);
        }

        // Select k agents with most wins.
        Arrays.sort(results, 0, results.length);

        Agent[] selection = new Agent[k];

        for (int i = 0; i < k; i++)
        {
            selection[i] = results[results.length - i - 1].getAgent();
        }

        return new Agent[0];
    }

    private static Agent[] roulette(int k, double[] probabilities, Agent[] agents)
    {
        Agent[] selection = new Agent[k];

        for (int i = 0; i < k; i++)
        {
            double x = rand_.nextDouble();
            double total = 0;

            for (int j = 0; j < probabilities.length; j++)
            {
                total += probabilities[j];

                if (x <= total)
                {
                    selection[i] = agents[j];
                    break;
                }
            }
        }

        return selection;
    }
}
