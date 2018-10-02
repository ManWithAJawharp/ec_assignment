#!/usr/bin/env python
import os

from subprocess import call

# Ignore these class files.
class_blacklist = ['BentCigarFunction.class',
                   'KatsuuraEvaluation.class',
                   'SchaffersEvaluation.class',
                   'SphereEvaluation.class']

paths = os.listdir('./')

java_files = []
class_files = []

for path in paths:
    filename, extension = os.path.splitext(path)

    if extension == '.java':
        java_files.append(path)

print(f"Compiling {', '.join(java_files)}.")
call(["javac", "-cp", "contest.jar"] + java_files)

for path in paths:
    filename, extension = os.path.splitext(path)

    if extension == '.class' and path not in class_blacklist:
        class_files.append(path)

call(["jar", "cmf", "MainClass.txt", "submission.jar"] + class_files)
