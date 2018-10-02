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
    private double shareRadius_;

    public Population(int n_agents, double shareRadius, Random rand)
    {
        rand_ = rand;

        // Initialize the agents;
        agents_ = new Agent[n_agents];
        for (int i=0; i < n_agents; i++)
        {
            agents_[i] = new Agent(rand);
        }

        populationSize_ = n_agents;

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

    // Select the k fittest parents.
    public Agent[] selectParents(int k_selection)
    {
        // return ParentSelection.selectKBest(k_selection, agents_);
        return ParentSelection.tournament(k_selection, 5, agents_);
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

    // Assign fitness to all agents that did not have fitness assigned
    // to them before.
    public int evaluate(ContestEvaluation evaluation, int evals, int evaluationLimit)
    {
        int actualEvaluations = 0;

        for (int i = 0; i < agents_.length; i++)
        {
            if (!agents_[i].isFitnessComputed())
            {
                Double fitness = (double) evaluation.evaluate(
                        agents_[i].getFenotype());

                // Apply fitness sharing to the newly computed fitness.
                fitness = fitnessSharing(agents_[i], fitness, shareRadius_);

                agents_[i].setFitness(fitness);

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
        Population.sortAgents(agents_);

        int populationSurplus = agents_.length - populationSize_;

        agents_ = Arrays.copyOfRange(agents_, populationSurplus, agents_.length);
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
    private double fitnessSharing(Agent agent, double baseFitness, double shareRadius)
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
}
