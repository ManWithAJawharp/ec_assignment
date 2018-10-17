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

    # Probability of mutation.
    'mutationProb': 0.1,
    # Radius within which fitness sharing is active.
    'fitnessSharing': 0.0,
    # Selection pressure for linear ranking.
    'expectedOffspring': 2.0,
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

    return scores, best_fitness, average_fitness


def parse_output(stdout):
    """
    Parse the program's output into a dictionary. Each unique variable
    is assigned a list that contains all its values in the order they
    where printed.
    """
    output = stdout.strip().split('\n')

    variables = {}

    for line in output:
        name, value = line.split(':')

        try:
            value = float(value.strip())
        except ValueError:
            continue

        try:
            variables[name].append(value)
        except KeyError:
            variables[name] = [value]

    return variables


def main():
    # Parse the arguments.
    parser = argparse.ArgumentParser()
    parser.add_argument('--iterations', '-i', default=10,
                        help="Number of times to run each function.", type=int)
    parser.add_argument('--evaluation', '-e', default=None,
                        help=("The evaluation function to run. If not set, all"
                              " functions will be run."))

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

    # arguments = DEFAULT_PARAMS

    for idx, evaluation in enumerate(evaluations):
        scores, best_fitness, average_fitness = \
                run_function(evaluation, args.iterations, arguments)

        plt.figure()
        # If the functions is run only once, only plot the run's
        # performance.
        if args.iterations is 1:
            plt.title("Performance")
            plt.plot(best_fitness[0], label="Best fitness")
            plt.plot(average_fitness[0], label="Average fitness")
            plt.xlabel("Generation")
            plt.ylabel("Fitness")
            plt.legend()
        else:
            # Otherwise, also plot the distribution over the collected
            # scores as a histogram.
            plt.subplot(121)
            plt.title("Performance of first run")
            plt.plot(best_fitness[0], label="Best fitness")
            plt.plot(average_fitness[0], label="Average fitness")
            plt.xlabel("Generation")
            plt.ylabel("Fitness")
            plt.legend()

            plt.subplot(122)
            plt.title("Distribution over scores")
            plt.hist(scores, density=True)
            plt.xlabel("Score")
            plt.ylabel("Density")

        plt.show()


if __name__ == "__main__":
    main()
