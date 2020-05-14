package mc.datamining.logicaloperators;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;
public class Group {
	private int fitness = 0;
	private ArrayList <Expression> expressions = new ArrayList <Expression>(GeneticAlgorithm.TRAINING_DATA.length);
	public ArrayList<Expression> getExpressions() { return expressions; } 
	public int getFitness() {
		fitness  = 0;
		expressions.forEach(x -> { if (x.getExpectedResult() == x.getCalculatedResult()) fitness++;});
		return fitness;
	}
	public Expression generateExpression(String postfixStringExpression, String expectedResult, Integer numbOfOperands) {
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
			} else stack.push( new Expression(Integer.valueOf(token)));
		}
		returnExpression = stack.pop();
		returnExpression.setExpectedResult(new Integer(expectedResult));
		returnExpression.setCalculatedResult(returnExpression.calculateResult());
		return returnExpression;
	}
}
