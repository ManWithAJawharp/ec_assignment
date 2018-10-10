#!/usr/bin/env python
import argparse
import matplotlib.pyplot as plt
import time

from subprocess import run


def run_function(evaluation, iterations=10):
    print(f"Running function {evaluation}")
    scores = []

    try:
        start_time = time.time()

        for i in range(iterations):
            seed = i

            # Run the program with given arguments.
            process = run(
                ["java", "-jar", "testrun.jar", "-submission=player63",
                 f"-evaluation={evaluation}", f"-seed={seed}"],
                capture_output=True, text=True)

            # Parse the output.
            # TODO: parse multiple output variables according to the
            # format "var_name: {value}"
            output = process.stdout.strip().split('\n')
            output = float(output[0].split()[-1])

            scores.append(output)
    except KeyboardInterrupt:
        pass
    finally:
        total_runtime = time.time() - start_time

    if len(scores) > 0:
        print(f"Average score over {i+1} runs: "
              f"{sum(scores) / len(scores)}")
        print(f"Total runtime: {total_runtime:.2f}s\n")

    return scores


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--iterations', '-i', default=10,
                        help="Number of times to run each function.", type=int)
    args = parser.parse_args()

    evaluations = ["SphereEvaluation",
                   "BentCigarFunction",
                   "SchaffersEvaluation",
                   "KatsuuraEvaluation"]

    print(f"Running functions for {args.iterations} iterations.")

    plt.figure()
    plt.suptitle("Score distributions for different evaluation functions.")

    for idx, evaluation in enumerate(evaluations):
        scores = run_function(evaluation, args.iterations)

        plt.subplot(2, 2, idx+1)
        plt.title(evaluation)
        plt.xlabel("Score")
        plt.hist(scores)

    plt.show()


if __name__ == "__main__":
    main()
