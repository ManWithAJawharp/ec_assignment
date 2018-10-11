import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.System.out;

public class SurvivorSelection
{
    private static Random rand_ = new Random();

    public static void setSeed(long seed)
    {
        rand_.setSeed(seed);
    }

    public static Agent[] tournament(int k, int tournamentSize, Agent[] agents)
    {
        Agent[] survivors = new Agent[k];

        // Copy agents array to an ArrayList for shuffling.
        ArrayList<Agent> agentsList = new ArrayList<>(Arrays.asList(agents));

        for (int i = 0; i < agents.length - k; i++)
        {
            Collections.shuffle(agentsList);
            
            Agent[] subset = new Agent[tournamentSize];
            for (int j = 0; j < tournamentSize; j++)
            {
                subset[j] = agentsList.get(j);
            }

            Population.sortAgents(subset);
            agentsList.remove(subset[0]);
        }

        return agentsList.toArray(survivors);
    }

    public static Agent[] truncation(int k, Agent[] agents)
    {
        Agent[] survivors = new Agent[k];

        Population.sortAgents(agents);

        for (int i = 0; i < k; i++)
        {
            survivors[i] = agents[i];
        }

        return survivors;
    }

    public static Agent[] roundRobin()
    {
        // Blah
        return new Agent[50];
    }

    public static Agent[] roulette()
    {
        return new Agent[50];
    }

    public static Agent[] linearRanking()
    {
        return new Agent[50];
    }
}
