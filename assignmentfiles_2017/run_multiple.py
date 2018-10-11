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


def run_function(evaluation, iterations=1):
    """
    Run the algorithm on the given evaluation function for a number of
    iterations.

    Parameters
    ----------
    evaluation : str
        The evaluation function to use.
    iterations : int, optional
        The number of times to run the algorithm.
    """
    print(f"Running function {evaluation}")
    scores = []
    best_fitness = []
    average_fitness = []

    try:
        start_time = time.time()

        for i in range(iterations):
            seed = i

            # Run the program with given arguments.
            process = run(
                ["java", "-jar", "testrun.jar", "-submission=player63",
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
    parser = argparse.ArgumentParser()
    parser.add_argument('--iterations', '-i', default=10,
                        help="Number of times to run each function.", type=int)
    parser.add_argument('--evaluation', '-e', default=None,
                        help=("The evaluation function to run. If not set, all"
                              " functions will be run."))
    args = parser.parse_args()

    evaluations = EVALUATIONS

    if args.evaluation is not None:
        if args.evaluation in EVALUATIONS:
            evaluations = [args.evaluation]
        else:
            print(f"Evaluation {args.evaluation} does not exist. Available "
                  f"functions are: {', '.join(evaluations)}.")

    print(f"Running functions for {args.iterations} iterations.")

    for idx, evaluation in enumerate(evaluations):
        scores, best_fitness, average_fitness = \
                run_function(evaluation, args.iterations)

        plt.figure()
        if args.iterations is 1:
            plt.plot(best_fitness[0])
            plt.plot(average_fitness[0])
        else:
            plt.subplot(121)
            plt.plot(best_fitness[0])
            plt.plot(average_fitness[0])

            plt.subplot(122)
            plt.hist(scores)

        plt.show()


if __name__ == "__main__":
    main()
