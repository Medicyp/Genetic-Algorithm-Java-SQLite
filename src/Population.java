package mc.datamining.logicaloperators;
import java.util.ArrayList;
import java.util.stream.IntStream;
public class Population {
	private ArrayList<Group> groups = new ArrayList<Group>(GeneticAlgorithm.POPULATION_SIZE); 
	public Population() {}
	public Population(Population population) { population.groups.forEach(x -> this.groups.add(x)); }
	public Population(int populationSize, Integer numbOfOperands) { 
		IntStream.range(0, populationSize).forEach(x -> {
			Group group = new Group();
			String operators = generateRandomOperators(numbOfOperands);
			IntStream.range(0, GeneticAlgorithm.TRAINING_DATA.length).forEach(y -> {
				group.getExpressions().add(group.generateExpression(GeneticAlgorithm.TRAINING_DATA[y][0] + " " + operators, 
																	GeneticAlgorithm.TRAINING_DATA[y][1], 
						                                            numbOfOperands)); 
			});
			groups.add(group);
		});
	}
	public ArrayList<Group> getGroups() { return groups; }
	public String generateRandomOperators(Integer numbOfOperands) {
		StringBuffer operators = new StringBuffer();
		IntStream.range(0, numbOfOperands-1).forEach(x -> {
			operators.append(Expression.OPERATORS.get(((int)(Math.random() * Expression.OPERATORS.size()))) + " ");
		});
        return operators.toString();
	}
	public ArrayList<Group> sortByFitness() {
		groups.sort((group1, group2) -> {
			if (group1.getFitness() > group2.getFitness()) return -1;
			else if (group1.getFitness() < group2.getFitness()) return 1;
			return 0;
		});
		return groups;
	}
}
