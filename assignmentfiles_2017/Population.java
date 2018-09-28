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

    private int populationSize_;

    public Population(int n_agents, Random rand)
    {
        rand_ = rand;

        // Initialize the agents;
        agents_ = new Agent[n_agents];
        for (int i=0; i < n_agents; i++)
        {
            agents_[i] = new Agent(rand);
        }

        populationSize_ = n_agents;
    }

    // Randomly assign low fitness values to all agents.
    public void initFitness()
    {
        for (int i = 0; i < agents_.length; i++)
        {
            agents_[i].setFitness(-100);
        }
    }

    // Select the k fittest parents.
    public Agent[] selectParents(int k_selection)
    {
        // Sort all agents by their fitness.
        sortAgents();
        
        // Assign parent probability to agents.

        // Select the k fittest agents in the population.
        return Arrays.copyOfRange(agents_, agents_.length - k_selection, agents_.length);
    }

    // Make random pairs of selected parents to perform crossover.
    public void createOffspring(Agent[] parents)
    {
        // Create a shuffled list of indices for all selected parents.
        ArrayList<Integer> indices = new ArrayList<Integer>();

        for (int i = 0; i < parents.length; i++)
        {
            indices.add(i);
        }
        Collections.shuffle(indices);

        ArrayList<Agent> offspring = new ArrayList<Agent>();

        // Iterate through pairs of parents to 
        for (int i = 1; i < indices.size(); i += 2)
        {
            Agent[] children = applyCrossover(parents[i-1], parents[i]); 

            for (int j = 0; j < children.length; j++)
            {
                offspring.add(children[j]);
            }
        }

        Agent[] offspringArray = new Agent[offspring.size()];
        for (int i = 0; i < offspring.size(); i++)
        {
            offspringArray[i] = offspring.get(i);
        }


        // Apply mutations to the new offspring.
        for (int i = 0; i < offspringArray.length; i++)
        {
            offspringArray[i].mutate();
        }

        // Concatenate agents array and offspring ArrayList into one array.
        Agent[] allAgents = new Agent[agents_.length + offspringArray.length];

        for (int i = 0; i < agents_.length; i++)
        {
            allAgents[i] = agents_[i];
        }

        for (int i = 0; i < offspringArray.length; i++)
        {
            allAgents[i + agents_.length] = offspringArray[i];
        }

        agents_ = allAgents;
    }

    // Apply a corssover operator to two agents. The selected operator
    //  is defined by the first agent.
    public Agent[] applyCrossover(Agent first, Agent second)
    {
        return first.crossover(second);
    }

    public int evaluate(ContestEvaluation evaluation, int evals, int evaluationLimit)
    {
        int actualEvaluations = 0;

        for (int i = 0; i < agents_.length; i++)
        {
            if (!agents_[i].isFitnessComputed())
            {
                agents_[i].setFitness((double) evaluation.evaluate(
                            agents_[i].getFenotype()));

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
        sortAgents();

        int populationSurplus = agents_.length - populationSize_;

        agents_ = Arrays.copyOfRange(agents_, populationSurplus, agents_.length);
    }

    public int getPopulationSize()
    {
        return agents_.length;
    }

    public double[] getBestGenotype()
    {
        sortAgents();

        Agent bestAgent = agents_[agents_.length - 1]; 

        return bestAgent.getGenotype();
    }

    public double getBestFitness()
    {
        sortAgents();

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

    private void sortAgents()
    {
        Arrays.sort(agents_, 0, agents_.length);
    }
}
