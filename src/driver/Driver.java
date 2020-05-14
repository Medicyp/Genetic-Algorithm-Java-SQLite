package st.datamining.logicaloperators.driver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.stream.IntStream;
import com.za.tutorial.ga.datamining.logicaloperators.Expression;
import com.za.tutorial.ga.datamining.logicaloperators.GeneticAlgorithm;
import com.za.tutorial.ga.datamining.logicaloperators.Group;
import com.za.tutorial.ga.datamining.logicaloperators.Population;

public class Driver {
	static String[][] DATA = new String [][] {{"1 1 1 0 1 1 0","1"},
												{"0 1 1 0 0 1 1","1"},
												{"1 0 1 0 1 1 0","1"},
												{"0 1 1 0 0 1 1","0"},
												{"0 1 0 0 1 1 1","0"},
												{"0 1 0 0 1 1 1","0"}};
	public static void main(String[] args) throws IOException {
		displayLogicalTable();
		GeneticAlgorithm.TRAINING_DATA = parseData();
		Driver driver = new Driver();
		Integer numbOfOperands = new StringTokenizer(GeneticAlgorithm.TRAINING_DATA[0][0], " ").countTokens();
		Population population = new Population(GeneticAlgorithm.POPULATION_SIZE, numbOfOperands);
		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
		int generationNumber = 0;
		driver.printHeading(population, generationNumber++);
		driver.printPopulation(population, numbOfOperands);
		driver.printGroupOfExpressionsAsTable(population.getGroups().get(0));
		while (population.sortByFitness().get(0).getFitness() != GeneticAlgorithm.TRAINING_DATA.length) {
			population = geneticAlgorithm.evolve(population, numbOfOperands);
			driver.printHeading(population, generationNumber++);
			driver.printPopulation(population, numbOfOperands);
			driver.printGroupOfExpressionsAsTable(population.getGroups().get(0));
		}
		System.out.println("\ndiscovered formula (regular notation): " + 
                		   population.getGroups().get(0).getExpressions().get(0).getRegularFormula());	
		System.out.println("discovered formula (postfix notation): " + 
                           population.getGroups().get(0).getExpressions().get(0).getPostfixFormula());	
		handleCommandLine(population.getGroups().get(0).getExpressions().get(0));
	}
	static String[][] parseData() {
		ArrayList<String[]> filteredData = new ArrayList<String[]>();
		for (int i = 0; i < DATA.length; i++) if (!contains(filteredData, DATA[i])) filteredData.add(DATA[i]);
		String[][] returnData = new String[filteredData.size()][2];
		for (int i = 0; i < filteredData.size(); i++) returnData[i] = filteredData.get(i);
		return returnData;
	}
	static boolean contains(ArrayList<String[]> container, String[] element) {
		boolean flag = false;
		for (int i = 0; i < container.size(); i++)
			if (container.get(i)[0].equals(element[0])) {
				flag = true;
				break;
			}
		return flag;
	}
	static void displayLogicalTable() {
		System.out.println(" x | y | and(&)    x | y | or(|)    x | y | xor(^)    x | y | nand(@)    x | y | nor(%)    x | y | xnor(#)");
		System.out.println("---------------   ---------------   --------------   ----------------   ---------------   ----------------");
		System.out.println(" 0 | 0 |   0       0 | 0 |   0      0 | 0 |   0       0 | 0 |    1       0 | 0 |   1       0 | 0 |    1   ");
		System.out.println(" 0 | 1 |   0       0 | 1 |   1      0 | 1 |   1       0 | 1 |    1       0 | 1 |   0       0 | 1 |    0   ");
		System.out.println(" 1 | 0 |   0       1 | 0 |   1      1 | 0 |   1       1 | 0 |    1       1 | 0 |   0       1 | 0 |    0   ");
		System.out.println(" 1 | 1 |   1       1 | 1 |   1      1 | 1 |   0       1 | 1 |    0       1 | 1 |   0       1 | 1 |    1   ");
	}
	static void handleCommandLine(Expression expression) throws IOException {
		int numbofOperands = expression.getNumberOfOperands();
		String formula = expression.getPostfixExpression();
		String operands = formula.substring(formula.length()-(numbofOperands-1)*2, formula.length());
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("> enter events vector (or e:xit) :");
			String rawEntry = bufferedReader.readLine();
			String entry = rawEntry.replaceAll("", " ").trim();
			if (entry.startsWith("e")) System.exit(0);
			else {
				if (rawEntry.length()==expression.getNumberOfOperands()) {
					try {
						String stringExpression = (entry+operands).trim();
						Expression classifyExpression = classifyExpression(stringExpression, numbofOperands);
						int result = classifyExpression.calculateResult();
						System.out.print(classifyExpression.getRegularExpression()+" = "+result);
						if (result==1) System.out.println(" ==> (+) result");
						else System.out.println(" ==> (-) result");
					} catch(Exception e) { System.out.println("invalid entry.");}
				} else System.out.println("invalid entry.");
			}
		}
	}
	public static Expression classifyExpression(String postfixStringExpression, Integer numbOfOperands) throws Exception {
		Expression returnExpression = null;
		Stack<Expression> stack = new Stack<Expression>();
		StringTokenizer stringTokenizer = new StringTokenizer(postfixStringExpression, " ");
		String token = null;
		Expression leftExpression = null;
		Expression rightExpression = null;
		while (stringTokenizer.hasMoreTokens()) {
			token = stringTokenizer.nextToken();
			if (Expression.OPERATORS.contains(token)) {
				leftExpression = stack.pop();
				rightExpression = stack.pop();
				stack.push(new Expression(leftExpression , rightExpression, token, numbOfOperands)); 
			} else {
				if (Integer.valueOf(token)==0 || Integer.valueOf(token)==1)
					stack.push( new Expression(Integer.valueOf(token)));
				else throw new Exception("invalid entry");
			}
		}
		returnExpression = stack.pop();
		returnExpression.setCalculatedResult(returnExpression.calculateResult());
		return returnExpression;
	}
	public void printHeading(Population population, int generationNumber) {
		System.out.println("\n> generation " + generationNumber);
		String heading = "Group Of Expressions ({expression} | expected result | calculated result)";
		Integer expressionLength = population.getGroups().get(0).getExpressions().get(0).getPostfixExpression().length();
		IntStream.range(0, (((expressionLength+15)*GeneticAlgorithm.TRAINING_DATA.length)/2)-(heading.length()+1)/2).forEach(x -> System.out.print(" "));
		System.out.print(heading);
		IntStream.range(0, (((expressionLength+14)*GeneticAlgorithm.TRAINING_DATA.length)/2)-(heading.length())/2).forEach(x -> System.out.print(" "));
		System.out.print("| Fitness");
		System.out.println();
		population.getGroups().get(0).getExpressions().forEach(y -> {
			IntStream.range(0, y.getPostfixExpression().length()+17).forEach(x -> System.out.print("-"));
		});
		System.out.println();
	}
	public void printPopulation(Population population, Integer numbOfOperands) {
		population.sortByFitness().forEach(x -> {
			x.getExpressions().forEach(y -> System.out.print("({"+ y.toString() + "} | "+ 
																   y.getExpectedResult() + " | "+ 
																   y.getCalculatedResult()+ ") | "));
			System.out.println(x.getFitness());
			x.getExpressions().forEach(y -> IntStream.range(0, y.getPostfixExpression().length()+17).forEach(z -> System.out.print("-")));
			System.out.println();	
		});
	}
	private void printGroupOfExpressionsAsTable(Group group) {
		int expressionSize = group.getExpressions().get(0).getPostfixExpression().length();
        System.out.println();
        IntStream.range(0, expressionSize/2 -3).forEach(x -> System.out.print(" "));
        System.out.print("expression"); 
        IntStream.range(0, expressionSize/2 -3).forEach(x -> System.out.print(" "));
        System.out.print("  | expected result | calculated result)\n"); 
        IntStream.range(0, expressionSize+45).forEach(x -> System.out.print("-"));
        System.out.println();
        group.getExpressions().forEach(x -> {
        	System.out.println(" {"+x + "}  |        " + x.getExpectedResult() + "        |        " + x.getCalculatedResult());
        });
    }
}
