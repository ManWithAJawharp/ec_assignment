import random
from statistics import mean
from math import sin, cos, tan

class population:
	def __init__(self):
		self.population = []
		self.pop_size = 100
		self.pop_fitness = []
		
		self.max_gen = 1000
		self.current_gen = 0
		self.k = 30
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
			self.select_rank(self.k)
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