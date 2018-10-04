from subprocess import run

evaluations = ["SphereEvaluation",
               "BentCigarFunction",
               "SchaffersEvaluation",
               "KatsuuraEvaluation"]

evaluation = evaluations[0]

print(f"Running function {evaluation}")
scores = []

for i in range(20):
    seed = i

    process = run(["java", "-jar", "testrun.jar", "-submission=player63",
                   f"-evaluation={evaluation}", f"-seed={seed}"],
                  capture_output=True, text=True)
    output = process.stdout.strip().split('\n')

    scores.append(float(output[1].split()[-1]))

print(f"Average score: {sum(scores) / len(scores)}")
