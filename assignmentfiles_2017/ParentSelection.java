import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class ParentSelection
{
    private static Random rand_ = new Random();

    public static void setSeed(long seed)
    {
        rand_.setSeed(seed);
    }

    // Select the k fittest agents.
    public static Agent[] selectKBest(int k, Agent[] agents)
    {
        Population.sortAgents(agents);

        return Arrays.copyOfRange(agents, agents.length - k, agents.length);
    }

    // Select k agents randomly.
    public static Agent[] selectKUniform(int k, Agent[] agents)
    {
        // Copy agents array to ArrayList and shuffle.
        ArrayList<Agent> agentsList = new ArrayList<Agent>(Arrays.asList(agents)); 
        Collections.shuffle(agentsList);

        // Select the first k agents from the shuffled ArrayList.
        ArrayList<Agent> selection = (ArrayList<Agent>) agentsList.subList(0, k);

        return (Agent[]) selection.toArray();
    }
}
