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
    private int tournamentSize_;

    private double shareRadius_;
    private double expectedOffspring_;

    public Population(int n_agents, int n_parents, int n_children, int tournamentSize,
            double shareRadius, double expectedOffspring, Random rand)
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
        tournamentSize_ = tournamentSize;

        shareRadius_ = shareRadius;
        expectedOffspring_ = expectedOffspring;
    }

    // Randomly assign low fitness values to all agents.
    public void initFitness()
    {
        for (int i = 0; i < agents_.length; i++)
        {
            agents_[i].setFitness(10 * rand_.nextDouble() - 50);
        }
    }

    // Update the population by introducting new offspring and trimming the
    // population.
    public void step()
    {
        selectParents();

        createOffspring();

        trimPopulation();
    }

    // Select the k fittest parents.
    public void selectParents(int k_selection)
    {
        // return Selection.selectKBest(k_selection, agents_);
        parents_ =  Selection.tournament(k_selection, 20, agents_);
    }

    public void selectParents()
    {
        selectParents(parentsSize_);
    }

    // Randomly select k migrants and remove them from the agents set.
    public Agent[] emigrate(int k_emigrants)
    {
        int[] indices = randomSelection(agents_, k_emigrants);

        Agent[] staying = new Agent[agents_.length - k_emigrants];
        Agent[] emigrants = new Agent[k_emigrants];
        int processedEmigrants = 0;

        for (int i = 0; i < agents_.length; i++)
        {
            // Check whether agent index is selected for migration.
            boolean foundEmigrant = false;

            if (processedEmigrants < k_emigrants)
            {
                for (int index : indices)
                {
                    // If a selected
                    if (i == index)
                    {
                        emigrants[processedEmigrants] = agents_[index];
                        processedEmigrants++;
                        foundEmigrant = true;
                        break;
                    }
                }
            }

            if (!foundEmigrant)
            {
                staying[i-processedEmigrants] = agents_[i];
            }
        }

        agents_ = staying;

        return emigrants;
    }

    public void immigrate(Agent[] immigrants)
    {
        int totalPopulation = agents_.length + immigrants.length;

        Agent[] newPop = new Agent[totalPopulation];

        for (int i = 0; i < agents_.length; i++)
        {
            newPop[i] = agents_[i];
        }

        for (int i = 0; i < immigrants.length; i++)
        {
            newPop[i + agents_.length] = immigrants[i];
        }

        agents_ = newPop;
    }

    // Make random pairs of selected parents to perform crossover.
    public void createOffspring(int k_children)
    {
        int[] indices = randomSelection(parents_, parents_.length);

        ArrayList<Agent> offspring = new ArrayList<Agent>();

        // Iterate through pairs of parents to create new offspring.
        while (offspring.size() < k_children)
        {
            for (int i = 1; i < indices.length; i += 2)
            {
                Agent[] children = crossover(parents_[indices[i-1]],
                        parents_[indices[i]]);

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

    public void createOffspring()
    {
        createOffspring(offspringSize_);
    }

    // Assign fitness to all agents that did not have fitness assigned
    // to them before.
    public int evaluate(ContestEvaluation evaluation, int evals,
            int evaluationLimit, Agent[] agents)
    {
        int actualEvaluations = 0;

        for (Agent agent : agents)
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

    public int evaluate(ContestEvaluation evaluation, int evals, int evaluationLimit)
    {
        try
        {
            return evaluate(evaluation, evals, evaluationLimit, offspring_);
        }
        catch (NullPointerException e)
        {
            return evaluate(evaluation, evals, evaluationLimit, agents_);
        }
    }

    // Kill a subset of the population to get it back to the original number.
    public void trimPopulation()
    {
        //parents_ = Selection.tournament(
        //        populationSize_ - offspring_.length, tournamentSize_, agents_);
        //parents_ = Selection.truncation(
        //        populationSize_ - offspring_.length, agents_);
        // parents_ = Selection.linearRanking(
        //        populationSize_ - offspring_.length, expectedOffspring_, agents_);
        parents_ = Selection.roundRobin(
                populationSize_ - offspring_.length, tournamentSize_, agents_);

        agents_ = joinGroups(parents_, offspring_);
    }

    // Returns the highest fitness in this population.
    public double getBestFitness()
    {
        double bestFitness = 0;

        for (Agent agent : agents_)
        {
            double fitness;

            try
            {
                fitness = agent.getFitness();
            }
            catch (FitnessNotComputedException e)
            {
                fitness = 0;
            }

            if (fitness > bestFitness)
            {
                bestFitness = fitness;
            }
        }
        
        return bestFitness;
    }

    // Returns the average fitness of all agents in this population.
    public double getAverageFitness()
    {
        double fitness = 0;

        for (Agent agent : agents_)
        {
            try
            {
                fitness += agent.getFitness();
            }
            catch (FitnessNotComputedException e)
            {
                continue;
            }
        }

        return fitness / agents_.length;
    }

    public Agent[] getAgents()
    {
        return agents_;
    }

    public static void sortAgents(Agent[] agents)
    {
        Arrays.sort(agents, 0, agents.length);
    }

    private Agent[] crossover(Agent first, Agent second)
    {
        double[][] genotypes = Crossover.randomlyWeightedAvg(first.getGenotype(),
                second.getGenotype());

        // Generate new fenotypes.
        Agent[] children = new Agent[genotypes.length];

        // Create new agents from generated fenotypes.
        for (int i = 0; i < genotypes.length; i++)
        {
            children[i] = new Agent(rand_, genotypes[i]);
        }

        return children;
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
                // TODO: Try different distance metrics.
                shareSum += shareFactor(fenotypeDistance(agent, other), shareRadius);    
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
    
    private double fenotypeDistance(Agent agent1, Agent agent2)
    {
        double[] fenotype1 = agent1.getFenotype();
        double[] fenotype2 = agent2.getFenotype();

        double distance = 0;
        for (int i = 0; i < fenotype1.length; i++)
        {
            distance += Math.pow(fenotype1[i] - fenotype2[i], 2);
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

    //  Generate an integer array with k random indices.
    public static int[] randomSelection(Agent[] set, int k)
    {
        ArrayList<Integer> indices = new ArrayList<Integer>();

        for (int i = 0; i < set.length; i++)
        {
            indices.add(i);
        }

        Collections.shuffle(indices);

        int[] array = new int[k];
        for (int i = 0; i < k; i++)
        {
            array[i] = indices.get(i);
        }

        return array;
    }
}
