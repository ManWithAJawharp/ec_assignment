import java.util.Random;
import java.util.ArrayList;

import static java.lang.System.out;

public class Crossover
{
    private static Random rand_ = new Random();

    public static void setSeed(long seed)
    {
        rand_.setSeed(seed);
    }

    // One-point crossover.
    public static double[][] onePoint(double[] first, double[] second)
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

    // N-point crossover.
    public static double[][] nPoint(double[] first, double[] second)
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

    // Averageing crossover.
    public static double[][] average(double[] first, double[] second)
    {
        int genotypeLength = first.length;

        double [][] children = new double[2][genotypeLength];

        for (int i = 0; i < genotypeLength; i++)
        {
            // Compute the mean of two genes with a probability
            // of 1/2.
            if (rand_.nextDouble() < 0.5)
            {
                children[0][i] = (first[i] + second[i]) / 2;
                children[1][i] = (first[i] + second[i]) / 2;
            }
            else
            {
                children[0][i] = first[i];
                children[2][i] = second[i];
            }
        }

        return children;
    }
}
