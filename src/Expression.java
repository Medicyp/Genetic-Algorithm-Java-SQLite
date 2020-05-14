package mc.datamining.logicaloperators;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.StringTokenizer;
public class Expression {
	private boolean isLeaf = false;
	private Expression leftExpression = null;
	private Expression rightExpression = null;
	private String operator = null;
	//public static final ArrayList<String> OPERATORS = new ArrayList<String>(Arrays.asList("&", "|", "^"));
	public static final ArrayList<String> OPERATORS = new ArrayList<String>(Arrays.asList("&", "|", "^", "@", "%", "#"));
	private Integer expectedResult;
	private Integer calculatedResult;
	private String postfixExpression = null;
	private Integer numbOfOperands  = null;
	public int getExpectedResult() { return expectedResult; }
	public int getCalculatedResult() { return calculatedResult; }
	public String getPostfixExpression() { return postfixExpression; }
	public Integer getOperatorIndex() { return numbOfOperands*2 + 1; }
	public Integer getCrossoverPoint() { return numbOfOperands*2 + (int)(((numbOfOperands - 1)*2 - 1)/2); }
	public Integer getNumberOfOperands() { return numbOfOperands; }
	public void setExpectedResult(Integer expectedResult) { this.expectedResult = expectedResult; }
	public void setCalculatedResult(Integer result) { this.calculatedResult = result; }
	public Expression(int leafExpression) { 
		isLeaf = true;
		postfixExpression = String.valueOf(leafExpression);
		calculatedResult = calculateResult(); 
	}
	public Expression(Expression rightExpression, Expression leftExpression, String operator, Integer numbOfOperands) {
		this.numbOfOperands = numbOfOperands;
		this.operator = operator;
		this.leftExpression = leftExpression;
		this.rightExpression = rightExpression;
		this.postfixExpression = leftExpression.toString() +" "+  rightExpression.toString() + " "+operator;
		calculatedResult = calculateResult();
	}
	public int calculateResult() { 
		Integer returnValue = 0;
		boolean leftFlag = false;
		boolean rightFlag = false;
		if (!isLeaf) {
			if (leftExpression.calculateResult() == 1) leftFlag = true;
			if (rightExpression.calculateResult() == 1) rightFlag = true;
			if ((operator.equals("&") && (leftFlag && rightFlag)) ||//and
				(operator.equals("|") && (leftFlag || rightFlag)) ||//or
				(operator.equals("^") && (leftFlag != rightFlag)) ||//xor
				(operator.equals("@") && (!leftFlag || !rightFlag)) ||//nand
				(operator.equals("%") && (!leftFlag && !rightFlag)) ||//nor
				(operator.equals("#") && (leftFlag == rightFlag))) returnValue = 1;//xnor
		} else returnValue =  Integer.valueOf(getPostfixExpression());
		return returnValue;
	}
	public String getPostfixFormula() {
		StringBuffer stringBuffer = new StringBuffer();
		StringTokenizer stringTokenizer = new StringTokenizer(this.postfixExpression, " ");
		String token = null;
		int i =0;
		while (stringTokenizer.hasMoreTokens()) {
			token = stringTokenizer.nextToken();
			if (Expression.OPERATORS.contains(token)) stringBuffer.append(mapOperator(token)+" ");
			else stringBuffer.append("e" + i++ + " ");
		}
		return stringBuffer.toString();
	}
	public String getRegularFormula() {
		Stack<String> stack = new Stack<String>();
		StringTokenizer stringTokenizer = new StringTokenizer(this.postfixExpression, " ");
		String token = null;
		String left = null;
		String right = null;
		int i = 0;
		while (stringTokenizer.hasMoreTokens()) {
			token = stringTokenizer.nextToken();
			if (OPERATORS.contains(token)) {
				right = stack.pop();
				left = stack.pop();
				stack.push("("+left+" "+mapOperator(token)+" "+right+")");
			} else stack.push("e" + i++);
		}
		return stack.pop();
	}
	public String getRegularExpression() {
		Stack<String> stack = new Stack<String>();
		StringTokenizer stringTokenizer = new StringTokenizer(this.postfixExpression, " ");
		String token = null;
		String left = null;
		String right = null;
		while (stringTokenizer.hasMoreTokens() ) {
			token = stringTokenizer.nextToken();
			if (OPERATORS.contains(token)) {
				right = stack.pop();
				left = stack.pop();
				stack.push("("+left+" "+mapOperator(token)+" "+right+")");
			} else stack.push(token);
		}
		return stack.pop();
	}
	
	private String mapOperator(String operator) {
			String returnValue = null;
			if (operator.contentEquals("&")) returnValue = "and";
			else if (operator.equals("|")) returnValue = "or";
			else if (operator.equals("^")) returnValue = "xor";
			else if (operator.equals("@")) returnValue = "nand";
			else if (operator.equals("%")) returnValue = "nor";
			else if (operator.equals("#")) returnValue = "xnor";
			return returnValue;
		}
	public String toString() { return postfixExpression; }
}
