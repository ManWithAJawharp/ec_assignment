runs=200
results=results

mkdir -p $results

# Bent cigar
islands=5
epoch=25
mutationProb=0.9

./run.py -e BentCigarFunction -i $runs -I $islands -E $epoch -MM $mutationProb -S 0 -o $results//bent_trun.csv
./run.py -e BentCigarFunction -i $runs -I $islands -E $epoch -MM $mutationProb -S 1 -o $results//bent_tour.csv 
./run.py -e BentCigarFunction -i $runs -I $islands -E $epoch -MM $mutationProb -S 2 -o $results//bent_lira.csv 
./run.py -e BentCigarFunction -i $runs -I $islands -E $epoch -MM $mutationProb -S 3 -o $results//bent_roro.csv 
./run.py -e BentCigarFunction -i $runs -I $islands -E $epoch -MM $mutationProb -R true -o $results//bent_rand.csv 

# Schaffers F7 function
islands=5
epoch=25
mutationProb=0.9

./run.py -e SchaffersEvaluation -i $runs -I $islands -E $epoch -MM $mutationProb -S 0 -o $results//scha_trun.csv
./run.py -e SchaffersEvaluation -i $runs -I $islands -E $epoch -MM $mutationProb -S 1 -o $results//scha_tour.csv 
./run.py -e SchaffersEvaluation -i $runs -I $islands -E $epoch -MM $mutationProb -S 2 -o $results//scha_lira.csv 
./run.py -e SchaffersEvaluation -i $runs -I $islands -E $epoch -MM $mutationProb -S 3 -o $results//scha_roro.csv 
./run.py -e SchaffersEvaluation -i $runs -I $islands -E $epoch -MM $mutationProb -R true -o $results//scha_rand.csv 

# Katsuura evaluation
islands=5
epoch=25
mutationProb=0.1

./run.py -e KatsuuraEvaluation -i $runs -I $islands -E $epoch -MM $mutationProb -S 0 -o $results//kats_trun.csv
./run.py -e KatsuuraEvaluation -i $runs -I $islands -E $epoch -MM $mutationProb -S 1 -o $results//kats_tour.csv 
./run.py -e KatsuuraEvaluation -i $runs -I $islands -E $epoch -MM $mutationProb -S 2 -o $results//kats_lira.csv 
./run.py -e KatsuuraEvaluation -i $runs -I $islands -E $epoch -MM $mutationProb -S 3 -o $results//kats_roro.csv 
./run.py -e KatsuuraEvaluation -i $runs -I $islands -E $epoch -MM $mutationProb -R true -o $results//kats_rand.csv 
