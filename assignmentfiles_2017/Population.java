import java.util.Random;

public class Population
{
    private Random rand_;
    private Agent[] agents_;

    public Population(int n_agents, Random rand)
    {
        rand_ = rand;
        agents_ = new Agent[n_agents];

        for (int i=0; i < n_agents; i++)
        {
            agents_[i] = new Agent(rand);
        }
    }

    public Agent[] selectParents()
    {
        return agents_;
    }

    public Agent[] applyCrossover(Agent first, Agent second)
    {
        return first.crossover(second);
    }
}
