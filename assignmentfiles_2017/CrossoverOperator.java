import java.util.Random;
import java.util.ArrayList;

import static java.lang.System.out;

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
        int genotypeLength = first.length; // Assume both genotypes have equal length.

        double[][] children = new double[2][genotypeLength];

        int point = rand_.nextInt(genotypeLength);

        double[] child1 = new double[genotypeLength];
        double[] child2 = new double[genotypeLength];

        for (int i=0; i < point; i++)
        {
            child1[i] =  first[i];
            child2[i] = second[i];
        }

        for (int i = point; i < genotypeLength; i++)
        {
            child1[i] = second[i];
            child2[i] = first[i];
        }

        children[0] = child1;
        children[1] = child2;

        return children;
    }

    public double[][] nPoint(double[] first, double[] second)
    {
        int genotypeLength = first.length;

        double[][] children = new double[2][genotypeLength];

        ArrayList<Integer> points = new ArrayList<Integer>();
        points.add(rand_.nextInt(genotypeLength));

        while (points.get(points.size() - 1) < genotypeLength)
        {
            int previousPoint = points.get(points.size() - 1);
            points.add(previousPoint + rand_.nextInt(genotypeLength - previousPoint + 1));
        }

        int parentFlag = 0;
        for (int i = 0; i < genotypeLength; i++)
        {
            children[parentFlag][i] = first[i];
            children[1 - parentFlag][i] = second[i];

            if (i == points.get(0))
            {
                // Flip parentFlag.
                parentFlag = 1 - parentFlag;
                points.remove(0);
            }
        }

        return children;
    }

    public double[][] average(double[] first, double[] second)
    {
        int genotypeLength = first.length;

        double [][] children = new double[1][genotypeLength];

        for (int i = 0; i < genotypeLength; i++)
        {
            children[0][i] = (first[i] + second[i]) / 2;
        }

        return children;
    }
}
