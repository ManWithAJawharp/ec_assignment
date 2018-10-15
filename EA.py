import random
from statistics import mean
from math import sin, cos, tan, exp

class population:
	def __init__(self):
		self.population = []
		self.pop_size = 100
		self.pop_fitness = []
		
		self.max_gen = 3000
		self.current_gen = 0
		self.k = 30
		self.s = 2
		self.q = 30
		self.p_mutate = 0.2
		self.i_mutate = 0.1
	
	def print_logs(self):
		print("Generation %s" % (self.current_gen,))
		#print("Population Size: %f" % len(self.population))
		print("Max Fitness: %f" % max(self.pop_fitness))
		print("Mean Fitness: %f" % mean(self.pop_fitness))
		print("X: %f  Y: %f" % (self.population[0][0], self.population[0][1]))
		print("\n")
	
	def new_population_random(self):
		self.population = []
		for x in range(self.pop_size):
			self.population.append((random.uniform(-100, 100),random.uniform(-100, 100)))
	
	def calc_fitness_pop(self):
		self.pop_fitness = []
		for particle in self.population:
			self.pop_fitness.append(fitness(particle[0], particle[1]))
	
	def sort_pop(self):
		current_pop = self.population
		current_fitness = self.pop_fitness
		sorted_pop, sorted_fitness = zip(*sorted(zip(current_fitness, current_pop)))
		sorted_pop = list(reversed(sorted_pop))
		sorted_fitness = list(reversed(sorted_fitness))
		return sorted_pop, sorted_fitness
	
	def select_rank(self, k):
		sorted_fitness, sorted_pop = self.sort_pop()
		self.population = sorted_pop[0:k]
		self.pop_fitness = sorted_fitness[0:k]
	
	def select_round_robin_tournament(self, k, q):							#Round-Robin Tournament Selection, scores each player against q opponents, selects k parents/survivors
		sorted_fitness, sorted_pop = self.sort_pop()							#Sort population by fitness
		scores = []																#Initialise Score list
		for particle in range(0, self.pop_size):								#Score each player in population
			wins = 0																#Initialise win counter
			for opponent in range(0, q):											#Score against q opponents
				if sorted_fitness[random.randint(0, self.pop_size-1)] <= sorted_fitness[particle]:
					wins = wins + 1														#Increment win score if fitness is higher
			scores.append(wins)														#Add win score to score list
		score_sorted_pop = list(reversed([x for _,x in sorted(zip(scores, sorted_pop))]))			#Sort population according to score
		score_sorted_fitness = list(reversed([x for _,x in sorted(zip(scores, sorted_fitness))]))	#Sort fitness list according to score
		self.population = score_sorted_pop[0:k]										#Take top k players from population
		self.pop_fitness = score_sorted_fitness[0:k]								#Update corresponding k fitnesses
	
	def select_fitness_roulette(self, k):									#Fitness Proportionate Selection (FPS), selects k parents/survivors
		sorted_fitness, sorted_pop = self.sort_pop()							#Sort population by fitness
		sum_fitness = sum(sorted_fitness)
		p_fps = [fitness/sum_fitness for fitness in sorted_fitness]				#Calculate probability for each individual according to FPS formula
		pop_selection = []														#Initiate selected survivor population list
		fitness_selection = []													#Initiate fitness corresponding to pop_selection
		for a in range(0, k):
			roulette = random.uniform(0, 1)											#Roll roulette
			p_cumulative = 0														#This variable is used to check which individual the roulette has landed on
			particle_index = 0														#Initiate individual index count (used to add individual to survivor list)
			for particle_probability in p_fps:										#Loop for each individual
				p_cumulative = p_cumulative + particle_probability						#Update p_cumulative with individual's probability when checking each individual
				if p_cumulative >= roulette:											#Individual is selected when p_cumulative exceeds roulette value
					#print(particle_index)
					pop_selection.append(sorted_pop[particle_index])
					fitness_selection.append(sorted_fitness[particle_index])
					break																#Break for-loop when selection is made
				particle_index = particle_index + 1										#Update particle index counter
		
		self.population = pop_selection										#Update selected parents/survivors into class attribute
		self.pop_fitness = fitness_selection
	
	def select_rank_roulette_linear(self, k, s):							#Rank Based Selection: Linear Ranking, selects k partents/survivors with parameter 1 <= s <= 2
		sorted_fitness, sorted_pop = self.sort_pop()							#Sort population by fitness
		pop_rank = list(reversed(range(self.pop_size)))							#Create rank list corresponding to a sorted population
		p_lin_rank = [((2-s)/self.pop_size)+(2*rank*(s-1)/(self.pop_size*(self.pop_size-1))) for rank in pop_rank]	#Calculate weights for each individual according to Linear Ranking forumula
		p_lin_rank = [p/sum(p_lin_rank) for p in p_lin_rank]					#Normalise weight values in order to be used as probability values
		
		pop_selection = []														#Initiate selected survivor population list
		fitness_selection = []													#Initiate fitness corresponding to pop_selection
		for a in range(0, k):
			roulette = random.uniform(0, 1)											#Roll roulette
			p_cumulative = 0														#This is used to check which individual the roulette has landed on
			particle_index = 0														#Initiate individual index count (used to add individual to survivor list)
			for particle_probability in p_lin_rank:									#Loop for each individual
				p_cumulative = p_cumulative + particle_probability						#Update p_cumulative when checking each individual
				if p_cumulative >= roulette:											#Individual is selected when p_cumulative exceeds roulette value
					pop_selection.append(sorted_pop[particle_index])
					fitness_selection.append(sorted_fitness[particle_index])
					break																#Break for-loop when selection is made
				particle_index = particle_index + 1									#Update particle index counter
		
		self.population = pop_selection										#Update selected parents/survivors into class attribute
		self.pop_fitness = fitness_selection
	
	def select_rank_roulette_exponential(self, k):									#Rank Based Selection: Exponential Ranking, selects k parents/survivors
		sorted_fitness, sorted_pop = self.sort_pop()									#Sort population by fitness
		pop_rank = list(reversed(range(self.pop_size)))									#This is a list counting down from 1-pop_size to 0 (i value in formula)
		p_lin_rank_weights = [1 - exp(-rank) for rank in pop_rank]						#Calculate weights for each individual according to Exponential Ranking formula
		p_lin_rank = [p/sum(p_lin_rank_weights) for p in p_lin_rank_weights]			#Normalise weight values in order to be used as probability values
		
		pop_selection = []																#Initiate selected survivor population list
		fitness_selection = []															#Initiate fitness corresponding to pop_selection
		for a in range(0, k):
			roulette = random.uniform(0, 1)													#Roll roulette
			p_cumulative = 0																#This is used to check which individual the roulette has landed on
			particle_index = 0																#Initiate individual index count (used to add individual to survivor list)
			for particle_probability in p_lin_rank:											#Loop for each individual
				p_cumulative = p_cumulative + particle_probability								#Update p_cumulative when checking each individual
				if p_cumulative >= roulette:													#Individual is selected when p_cumulative exceeds roulette value
					pop_selection.append(sorted_pop[particle_index])
					fitness_selection.append(sorted_fitness[particle_index])
					break																		#Break for-loop when selection is made
				particle_index = particle_index + 1											#Update particle index counter
		
		self.population = pop_selection													#Update selected parents/survivors into class attribute
		self.pop_fitness = fitness_selection
	
	def pop_replicate_repeats(self):
		pop_size = len(self.population)
		for x in range(pop_size, self.pop_size):
			self.population.append(self.population[x%pop_size])
			self.pop_fitness.append(self.pop_fitness[x%pop_size])
	
	def pop_mutate(self):
		new_pop = []
		for particle in self.population:
			new_particle = []
			for var in particle:
				if random.uniform(0, 1) <= self.p_mutate:
					new_var = var + random.uniform(-1, 1)*self.i_mutate
				else:
					new_var = var
				new_particle.append(new_var)
			new_particle = tuple(new_particle)
			new_pop.append(new_particle)
		self.population = new_pop
	
	def ea_iterate(self):
		self.new_population_random()
		self.calc_fitness_pop()
		self.print_logs()
		
		for x in range(self.max_gen):
			self.select_round_robin_tournament(self.k, self.q)
			self.pop_replicate_repeats()
			self.pop_mutate()
			self.calc_fitness_pop()
			self.current_gen = self.current_gen + 1
			self.print_logs()


def fitness(x,y):
	return (x*x + 1000000*y*y)*-1

def main():
	particle_pop = population()
	particle_pop.ea_iterate()



if __name__ == "__main__":
	main()
