public class Agent
{
	// This agent's representation
	private Genotype genotype;
	private MutationOperator mutationOp;
	private CrossoverOperator crossOp;

	public Agent()
	{
	}

	public void mutate()
	{
		// Apply a mutation operator to the genotype.
		genotype = mutationOp.call(genotype);
	}

	public double[] get_fenotype()
	{
		// Map this agent's genotype to a genotype.
		return new double[10];
	}
}
