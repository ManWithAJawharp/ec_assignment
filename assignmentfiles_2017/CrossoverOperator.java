import java.util.Random;

public class CrossoverOperator
{
    private Random rand_;

    public CrossoverOperator(Random rand)
    {
        rand_ = rand;
    }

    public double[][] call(double[] first, double[] second)
    {
        return onePoint(first, second);
    }

    public double[][] onePoint(double[] first, double[] second)
    {
        // Implement simple one-point crossover.
        int genotype_length = first.length; // Assume both genotypes have equal length.

        double[][] children = new double[2][genotype_length];

        int point = rand_.nextInt(genotype_length);

        double[] child1 = new double[genotype_length];
        double[] child2 = new double[genotype_length];

        for (int i=0; i < point; i++)
        {
            child1[i] =  first[i];
            child2[i] = second[i];
        }

        for (int i = point; i < genotype_length; i++)
        {
            child1[i] = second[i];
            child2[i] = first[i];
        }

        children[0] = child1;
        children[1] = child2;

        return children;
    }
}
