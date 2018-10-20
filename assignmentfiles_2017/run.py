#!/usr/bin/env python
import argparse
import matplotlib.pyplot as plt
import time

from subprocess import run

EVALUATIONS = [
    "SphereEvaluation",
    "BentCigarFunction",
    "SchaffersEvaluation",
    "KatsuuraEvaluation"
]

DEFAULT_PARAMS = {
    # Number of islands.
    'islands': 2,
    # Number of migrants per island per migration.
    'migrants': 5,
    # Number of generations before a migration occurs.
    'epoch': 100,

    # Number of agents per island.
    'agents': 100,
    # Number of parents selected per generation.
    'parents': 50,
    # Number of children generated per generation.
    'children': 50,
    # Number of agents selected for a selection tournament.
    'tournamentSize': 10,

    # Probability of mutation.
    'mutationProb': 0.1,
    # Radius within which fitness sharing is active.
    'fitnessSharing': 0.0,
    # Selection pressure for linear ranking.
    'expectedOffspring': 2.0,

    'randomSelectionOp': "false",
    'selectionOp': 3
}


def run_function(evaluation, iterations=1, arguments={}):
    """
    Run the algorithm on the given evaluation function for a number of
    iterations.

    Parameters
    ----------
    evaluation : str
        The evaluation function to use.
    iterations : int, optional
        The number of times to run the algorithm.
    arguments : dict
        Dictionary of arguments to pass to the program.
    """
    print(f"Running function {evaluation}")
    scores = []
    best_fitness = []
    average_fitness = []

    arguments = [f"-D{key}={value}" for key, value in arguments.items()]

    try:
        start_time = time.time()

        for i in range(iterations):
            seed = i

            # Run the program with given arguments.
            process = run(
                ["java"]
                + arguments
                + ["-jar", "testrun.jar", "-submission=player63",
                   f"-evaluation={evaluation}", f"-seed={seed}"],
                capture_output=True, text=True)

            variables = parse_output(process.stdout)

            scores += variables['Score']
            best_fitness.append(variables['best_fitness'])
            average_fitness.append(variables['average_fitness'])

    except KeyboardInterrupt:
        pass
    finally:
        total_runtime = time.time() - start_time

    if len(scores) > 0:
        print(f"Average score over {i+1} runs: "
              f"{sum(scores) / len(scores)}")
        print(f"Total runtime: {total_runtime:.2f}s\n")

    return scores, best_fitness, average_fitness, variables


def parse_output(stdout):
    """
    Parse the program's output into a dictionary. Each unique variable
    is assigned a list that contains all its values in the order they
    where printed.
    """
    output = stdout.strip().split('\n')

    variables = {}

    for line in output:
        try:
            name, value = line.split(':')
        except ValueError:
            continue

        try:
            value = float(value.strip())
        except ValueError:
            value = value.strip()

        try:
            variables[name].append(value)
        except KeyError:
            variables[name] = [value]

    return variables


def plot_results(num=None, best_fitness=[], average_fitness=[], scores=[],
                 n_islands=1, variables={}):
    fig = plt.figure(num, figsize=(16, 8))
    plt.subplot(131)
    plt.title("Performance over all islands")
    plt.plot(best_fitness, label="Best fitness")
    plt.plot(average_fitness, label="Average fitness")

    plt.xlabel("Generation")
    plt.ylabel("Fitness")
    plt.legend()

    plt.subplot(232)
    plt.title("Best individual per island")
    # for i in range(arguments['islands']):
    for i in range(n_islands):
        operator = variables[f'operator_island_{i}'][0]
        plt.plot(variables[f'best_fitness_{i}'],
                 label=f'Island {i+1} ({operator})')

    plt.xlabel("Generation")
    plt.ylabel("Fitness")
    plt.legend()

    plt.subplot(235)
    plt.title("Individual average per island")
    for i in range(n_islands):
        plt.plot(variables[f'average_fitness_{i}'],
                 label=f'Island {i+1}')

    plt.xlabel("Generation")
    plt.ylabel("Fitness")
    plt.legend()

    plt.subplot(133)
    plt.title("Distribution over scores")
    plt.hist(scores, density=True)
    plt.xlabel("Score")
    plt.ylabel("Density")

    return fig


def main():
    # Parse the arguments.
    parser = argparse.ArgumentParser()
    parser.add_argument('--iterations', '-i', default=10,
                        help="Number of times to run each function.", type=int)
    parser.add_argument('--evaluation', '-e', default=None,
                        help=("The evaluation function to run. If not set, all"
                              " functions will be run."))
    parser.add_argument('--output', '-o', default=None,
                        help="Name of the file to output the scores to.",
                        type=str)

    for key in DEFAULT_PARAMS:
        value_type = type(DEFAULT_PARAMS[key])

        try:
            parser.add_argument(
                f'--{key}', f'-{key[0].upper()}',
                default=DEFAULT_PARAMS[key], type=value_type,
                help=f"A parameter of type {value_type}.")
        except argparse.ArgumentError:
            parser.add_argument(
                f'--{key}', f'-{key[0].upper()*2}',
                default=DEFAULT_PARAMS[key], type=value_type,
                help=f"A parameter of type {repr(value_type)}.")

    args = parser.parse_args()

    # Set the evaluation functions to run.
    evaluations = EVALUATIONS

    if args.evaluation is not None:
        if args.evaluation in EVALUATIONS:
            evaluations = [args.evaluation]
        else:
            print(f"Evaluation {args.evaluation} does not exist. Available "
                  f"functions are: {', '.join(evaluations)}.")

    print(f"Running functions for {args.iterations} iterations.")

    # Run the selected evaluation functions a number of times and
    # report the outcomes.
    arguments = {}
    arg_arguments = vars(args)
    for key in DEFAULT_PARAMS:
        arguments[key] = arg_arguments[key]

    for idx, evaluation in enumerate(evaluations):
        scores, best_fitness, average_fitness, variables = \
                run_function(evaluation, args.iterations, arguments)
        fig = plot_results(idx, best_fitness[-1], average_fitness[-1], scores,
                           arguments['islands'], variables)

        if args.output is not None:
            image_name = args.output.split('.')[0]
            image_name = f"{image_name}_plot.png"
            fig.savefig(image_name, bbox_inches='tight')

            with open(args.output, 'w') as output_file:
                output_file.write(
                    "id;score;" + ';'.join([f"best_{i}"
                                            for i in range(10)]))
                for idx, score in enumerate(scores):
                    intermediate = variables['best_fitness'][::len(
                        variables['best_fitness'])//10]
                    line = f"\n{idx};{score};"
                    line = line + ';'.join([str(x) for x in intermediate])
                    output_file.write(line)
        else:
            plt.show()


if __name__ == "__main__":
    main()
