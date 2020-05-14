package mc.datamining.logicaloperators;
import java.util.stream.IntStream;
public class GeneticAlgorithm {
	public static final double MUTATION_RATE = 0.3;
	public static final double CROSSOVER_RATE = 0.9;
	public static final int TOURNAMENT_SELECTION_SIZE = 2;
	public static final int POPULATION_SIZE = 6;
	public static final int NUMB_OF_ELITE_GROUPS = 0;
	public static String TRAINING_DATA[][];
	public Population evolve(Population population, Integer numbOfOperands) { 
		return mutatePopulation(crossoverPopulation(population, numbOfOperands), numbOfOperands); 
	}
	Population crossoverPopulation(Population population, Integer numbOfOperands) {
		Population crossoverPopulation = new Population(population);
		IntStream.range(0, NUMB_OF_ELITE_GROUPS).forEach(x -> crossoverPopulation.getGroups().set(x, population.getGroups().get(x)));
		IntStream.range(NUMB_OF_ELITE_GROUPS, crossoverPopulation.getGroups().size()).forEach(x -> {
			Group group1 = selectTournamentPopulation(population, numbOfOperands).getGroups().get(0);
			Group group2 = selectTournamentPopulation(population, numbOfOperands).getGroups().get(0);
			crossoverPopulation.getGroups().set(x, crossoverGroup(group1, group2, numbOfOperands));
		});
		return crossoverPopulation;
	}
	Population mutatePopulation(Population population, Integer numbOfOperands) {
		Population mutatePopulation = new Population(POPULATION_SIZE, numbOfOperands);
		IntStream.range(0, NUMB_OF_ELITE_GROUPS).forEach(x -> mutatePopulation.getGroups().set(x, population.getGroups().get(x)));
		IntStream.range(NUMB_OF_ELITE_GROUPS, mutatePopulation.getGroups().size()).forEach(x -> 
        						mutatePopulation.getGroups().set(x, mutateGroup(population.getGroups().get(x), numbOfOperands)));
		return mutatePopulation;
	}
	Group mutateGroup(Group group, Integer numbOfOperands) {
		if (Math.random() < MUTATION_RATE) {
			int random = (int)(Math.random() * (numbOfOperands - 1))*2;
			String originalOperator = group.getExpressions().get(0).getPostfixExpression().substring(
					            	  group.getExpressions().get(0).getOperatorIndex()-1+random, 
					            	  group.getExpressions().get(0).getOperatorIndex()+random);
			String replacementOperator = originalOperator;
			while (originalOperator.equals(replacementOperator)) {
				replacementOperator = Expression.OPERATORS.get(((int)(Math.random() * Expression.OPERATORS.size())));
			}
			String newOperator = replacementOperator;
			group.getExpressions().stream().forEach(x -> {
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(x.getPostfixExpression().substring(0,x.getOperatorIndex()-1+random)+newOperator +
						                                                 x.getPostfixExpression().substring(x.getOperatorIndex()+random));
				group.getExpressions().set(group.getExpressions().indexOf(x), 
						                   group.generateExpression(
						                         stringBuffer.toString(), 
						                         String.valueOf(x.getExpectedResult()), numbOfOperands));
			});
		}
		return group;
	}
	Group crossoverGroup(Group group1, Group group2, Integer numbOfOperands) {
		Group crossoverGroup = new Group();
		double random = Math.random();
		for (int i = 0; i < group1.getExpressions().size(); i++) {
			Expression x = group1.getExpressions().get(i);
			StringBuffer stringBuffer = new StringBuffer();
			if (CROSSOVER_RATE <= random) stringBuffer.append(x.getPostfixExpression());
			else stringBuffer.append(x.getPostfixExpression().substring(0, x.getOperatorIndex()-1)+
					                 x.getPostfixExpression().substring(x.getOperatorIndex()-1, x.getCrossoverPoint())+
							         group2.getExpressions().get(0).getPostfixExpression().substring(x.getCrossoverPoint()));
			crossoverGroup.getExpressions().add(group1.generateExpression(stringBuffer.toString(), 
					                                                      String.valueOf(x.getExpectedResult()), 
					                                                      numbOfOperands));
		}
		return crossoverGroup;
	}
	Population selectTournamentPopulation(Population population, Integer numbOfOperands) {
		Population tournamentPopulation = new Population();
		IntStream.range(0, TOURNAMENT_SELECTION_SIZE).forEach(x -> {
			tournamentPopulation.getGroups().add(population.getGroups().get((int)(Math.random()*population.getGroups().size())));
		});
		tournamentPopulation.sortByFitness();
		return tournamentPopulation;
	}
}
