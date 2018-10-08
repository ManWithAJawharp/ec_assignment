import org.vu.contest.ContestEvaluation;

import java.util.Random;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;

import static java.lang.System.out;

public class Population
{
    private Random rand_;
    private Agent[] agents_;

    private Agent[] parents_;
    private Agent[] offspring_;

    private int populationSize_;
    private int parentsSize_;
    private int offspringSize_;

    private double shareRadius_;

    public Population(int n_agents, int n_parents, int n_children,
            double shareRadius, Random rand)
    {
        rand_ = rand;

        // Initialize the agents;
        agents_ = new Agent[n_agents];
        for (int i=0; i < n_agents; i++)
        {
            agents_[i] = new Agent(rand);
        }

        populationSize_ = n_agents;
        parentsSize_ = n_parents;
        offspringSize_ = n_children;

        shareRadius_ = shareRadius;
    }

    // Randomly assign low fitness values to all agents.
    public void initFitness()
    {
        for (int i = 0; i < agents_.length; i++)
        {
            agents_[i].setFitness(-100);
        }
    }

    // Update the population by introducting new offspring and trimming the
    // population.
    public void step()
    {
        selectParents(parentsSize_);

        createOffspring();

        trimPopulation();
    }

    // Select the k fittest parents.
    public void selectParents(int k_selection)
    {
        // return ParentSelection.selectKBest(k_selection, agents_);
        parents_ =  ParentSelection.tournament(k_selection, 5, agents_);
    }

    public void selectParents()
    {
        selectParents(parentsSize_);
    }

    // Make random pairs of selected parents to perform crossover.
    public void createOffspring(int k_children)
    {
        // Create a shuffled list of indices for all selected parents.
        ArrayList<Integer> indices = new ArrayList<Integer>();

        for (int i = 0; i < parents_.length; i++)
        {
            indices.add(i);
        }
        Collections.shuffle(indices);

        ArrayList<Agent> offspring = new ArrayList<Agent>();

        // Iterate through pairs of parents to 
        while (offspring.size() < k_children)
        {
            for (int i = 1; i < indices.size(); i += 2)
            {
                Agent[] children = crossover(parents_[i-1], parents_[i]);

                for (int j = 0; j < children.length; j++)
                {
                    offspring.add(children[j]);
                }
            }
        }

        offspring_ = offspring.toArray(new Agent[offspring.size()]);
        offspring_ = Arrays.copyOfRange(offspring_, 0, k_children);
        // Apply mutations to the new offspring.
        for (int i = 0; i < offspring_.length; i++)
        {
            offspring_[i].mutate();
        }
    }

    private Agent[] crossover(Agent first, Agent second)
    {
        double[][] genotypes = Crossover.average(first.getGenotype(),
                second.getGenotype());

        // FGenerate new fenotypes.
        Agent[] children = new Agent[genotypes.length];

        // Create new agents from generated fenotypes.
        for (int i = 0; i < genotypes.length; i++)
        {
            children[i] = new Agent(rand_, genotypes[i]);
        }

        return children;
    }

    public void createOffspring()
    {
        createOffspring(offspringSize_);
    }

    // Assign fitness to all agents that did not have fitness assigned
    // to them before.
    public int evaluate(ContestEvaluation evaluation, int evals, int evaluationLimit)
    {
        int actualEvaluations = 0;

        for (Agent agent : offspring_)
        {
            if (!agent.isFitnessComputed())
            {
                Double fitness = (double) evaluation.evaluate(
                        agent.getFenotype());

                fitness = fitnessSharing(agent, agents_, fitness, shareRadius_);

                agent.setFitness(fitness);

                evals++;
                actualEvaluations++;
            }

            if (evals >= evaluationLimit)
            {
                break;
            }
        }

        return actualEvaluations;
    }

    // Kill a subset of the population to get it back to the original number.
    public void trimPopulation()
    {
        parents_ = SurvivorSelection.tournament(
                populationSize_ - parents_.length, 5, agents_);

        agents_ = joinGroups(parents_, offspring_);
    }

    public int getPopulationSize()
    {
        return agents_.length;
    }

    public double[] getBestGenotype()
    {
        sortAgents(agents_);

        Agent bestAgent = agents_[agents_.length - 1]; 

        return bestAgent.getGenotype();
    }

    public double getBestFitness()
    {
        sortAgents(agents_);

        Agent bestAgent = agents_[agents_.length - 1];

        double fitness;

        try
        {
            return bestAgent.getFitness();
        } catch (FitnessNotComputedException e)
        {
            return 0;
        }
    }

    public double getAverageFitness()
    {
        double fitness = 0;

        for (Agent agent : agents_)
        {
            try
            {
            fitness += agent.getFitness() / agents_.length;
            } catch (FitnessNotComputedException e)
            {
                continue;
            }
        }

        return fitness;
    }

    public static void sortAgents(Agent[] agents)
    {
        Arrays.sort(agents, 0, agents.length);
    }

    // Decrease an agent's fitness based on its distance to other agents.
    private double fitnessSharing(Agent agent, Agent[] group,
            double baseFitness, double shareRadius)
    {
        double shareSum = 0;

        for (Agent other : agents_)
        {
            if (agent.equals(other))
            {
                continue;
            }
            else
            {
                shareSum += shareFactor(genotypeDistance(agent, other), shareRadius);    
            }
        }

        if (shareSum > 0)
        {
            return baseFitness / shareSum;
        }
        else
        {
            return baseFitness;
        }
    }

    // Compute the Euclidian distance between two agents in genotype
    // space.
    private double genotypeDistance(Agent agent1, Agent agent2)
    {
        double[] genotype1 = agent1.getGenotype();
        double[] genotype2 = agent2.getGenotype();

        double distance = 0;
        for (int i = 0; i < genotype1.length; i++)
        {
            distance += Math.pow(genotype1[i] - genotype2[i], 2);
        }

        return Math.sqrt(distance);
    }

    // COmpute the amount of fitness sharing based on the distance
    // between two agents and the sharing radius.
    private double shareFactor(double distance, double shareRadius)
    {
        if (distance > shareRadius)
        {
            return 0.0;
        }
        else
        {
            return 1 - distance / shareRadius;
        }
    }

    private Agent[] joinGroups(Agent[] group1, Agent[] group2)
    {
        // Concatenate agents array and offspring ArrayList into one array.
        Agent[] allAgents = new Agent[group1.length + group2.length];

        for (int i = 0; i < group1.length; i++)
        {
            allAgents[i] = group1[i];
        }

        for (int i = 0; i < group2.length; i++)
        {
            allAgents[i + group1.length] = group2[i];
        }

        return allAgents;
    }
}
