public class AgentRobin implements Comparable<AgentRobin>
{
    private Agent agent_;
    private int wins_;
    public AgentRobin(Agent agent, int wins)
    {
        agent_ = agent;
        wins_ = wins;
    }

    public Agent getAgent()
    {
        return agent_;
    }

    public int getWins()
    {
        return wins_;
    }

    public int compareTo(AgentRobin other)
    {
        if (wins_ > other.getWins())
        {
            return 1;
        }
        else if (wins_ < other.getWins())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}

