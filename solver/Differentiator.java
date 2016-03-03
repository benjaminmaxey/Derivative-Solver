package solver;

public class Differentiator 
{
	private CharStack cStack;
	private NodeStack nStack;
	
	public Differentiator(int maxSize)
	{
		cStack = new CharStack(maxSize);
		nStack = new NodeStack(maxSize);
	}
	
	public String translate(String input) throws InvalidSymbolException
	{
		System.out.println("translate: " + input);
		input = input.trim().replaceAll(" +", " "); //remove extra whitespace
		String output = "";
		boolean wasDigit = false;
		int i = 0;

		String test = "";
		
		while (i < input.length())
		{
			char current = input.charAt(i);
			if (current != ' ') //if current is a space, move to next character
			{
				if (current == ')') //if current is a close parenthesis, pop operators from stack until open parenthesis is reached
				{
					while (!cStack.isEmpty() && cStack.peek() != '(')
					{
						output += cStack.pop();
						output += " ";
					}
					cStack.pop();
					wasDigit = false;
				}
				else if (current == '(')
				{
					if (wasDigit) //if last character was a digit, supply an extra '*' before dealing with '('
					{
						while (!cStack.isEmpty() && (cStack.peek() == '*' || cStack.peek() == '/' || cStack.peek() == '^'))
						{
							output += cStack.pop();
							output += " ";
						}
						cStack.push('*');
					}
					cStack.push(current); //if last character wasn't a digit, just push '(' to the stack
					wasDigit = false;
				}
				else if (current == '+' || current == '-')
				{
					while (!cStack.isEmpty() && (cStack.peek() == '+' || cStack.peek() == '-' || cStack.peek() == '*' || cStack.peek() == '/' || cStack.peek() == '^')) //operators with a greater than or equal precedence can be popped from stack
					{
						output += cStack.pop();
						output += " ";
					}
					cStack.push(current);
					wasDigit = false;
				}
				else if (current == '*' || current == '/')
				{
					while (!cStack.isEmpty() && (cStack.peek() == '*' || cStack.peek() == '/' || cStack.peek() == '^')) //operators with a greater than or equal precedence can be popped from stack
					{
						output += cStack.pop();
						output += " ";
					}
					cStack.push(current);
					wasDigit = false;
				}
				else if (current == '^')
				{
					while (!cStack.isEmpty() && cStack.peek() == '^') //operator with equal precedence can be popped from stack
					{
						output += cStack.pop();
						output += " ";
					}
					cStack.push(current);
					wasDigit = false;
				}
				else if (current <= '9' && current >= '0' || current == '.') //push operands to output automatically
				{
					while (current <= '9' && current >= '0' || current == '.') //continue pushing digits to output until end of number (whitespace) is reached
					{
						output += current;
						if (i + 1 < input.length())
						{
							i++;
							current = input.charAt(i);
						}
						else
							break;
					}
					output += " ";
					wasDigit = true;
				}
				else if (current == 'x') 
				{
					if (wasDigit) //if last character was a digit, supply an extra '*' before dealing with '('
					{
						while (!cStack.isEmpty() && (cStack.peek() == '*' || cStack.peek() == '/' || cStack.peek() == '^'))
						{
							output += cStack.pop();
							output += " ";
						}
						cStack.push('*');
						wasDigit = false;
					}
					output += 'x';
					output += " ";
				}
				else 
					throw new InvalidSymbolException("Invalid symbol: " + current);
			}
			i++; //move to next character
		}
		
		while (!cStack.isEmpty()) //pop whatever is left on the stack
		{
			output += cStack.pop();
			output += " ";
		}
		output = output.substring(0, output.length() - 1); //remove last character of the output, which will be a space
		return output;
	}
	
	public Node formTree(String input) throws InvalidSymbolException
	{
		input = this.translate(input); //translate input to postfix
		int i = 0;
		System.out.println("formTree: " + input);
		
		while (i < input.length())
		{
			char current = input.charAt(i);
			if (current <= '9' && current >= '0' || current == '.')
			{
				int j = i;
				if (input.length() > 1)
					while (j < input.length() && input.charAt(++j) != ' '); //set j to the index of the next whitespace
				String temp = input.substring(i, j);
				nStack.push(new Node(temp)); //push a new operand node to the stack
				i = j; //set i to the index of the whitespace
			}
			else if (current == 'x')
			{
				nStack.push(new Node("x"));
			}
			else if (current == '+') //create operator node with two operand children
			{
				Node n = new Node("+");
				n.setRightChild(nStack.pop());
				n.setLeftChild(nStack.pop());
				nStack.push(n); //push the new operator node back onto the stack to act as an operand node
			}
			else if (current == '-')
			{
				Node n = new Node("-");
				n.setRightChild(nStack.pop());
				n.setLeftChild(nStack.pop());
				nStack.push(n);
			}
			else if (current == '*')
			{
				Node n = new Node("*");
				n.setRightChild(nStack.pop());
				n.setLeftChild(nStack.pop());
				nStack.push(n);
			}
			else if (current == '/')
			{
				Node n = new Node("/");
				n.setRightChild(nStack.pop());
				n.setLeftChild(nStack.pop());
				nStack.push(n);
			}
			else if (current == '^')
			{
				Node n = new Node("^");
				n.setRightChild(nStack.pop());
				n.setLeftChild(nStack.pop());
				nStack.push(n);
			}
			i++;
		}
		
		return nStack.pop(); //there should be only one node left on the stack 
	}

	public boolean isComplicatedFunction(Node n)
	{
		boolean flag = false;
		if (n.getLeftChild() != null)
			flag = isComplicatedFunction(n.getLeftChild());
		if (n.getData().equals("x"))
			flag = true;
		if (n.getRightChild() != null)
			flag = isComplicatedFunction(n.getRightChild());
		return flag;
	}
	
	public Polynomial evaluate(Node n) throws TooComplicatedException
	{
		System.out.println("evaluate: " + n.getData());
		Polynomial output = new Polynomial(new Term(0, 0));

		if (n.getData().equals("+"))
		{
			output.add(evaluate(n.getLeftChild()));
			output.add(evaluate(n.getRightChild()));
		}
		else if (n.getData().equals("-"))
		{
			output.add(evaluate(n.getLeftChild()));
			Polynomial temp = evaluate(n.getRightChild());
			temp.multiply(new Term(-1, 0));
			output.add(temp);
		}
		else if (n.getData().equals("*"))
		{
			Polynomial temp = evaluate(n.getLeftChild());
			temp.multiply(evaluate(n.getRightChild()));
			output.add(temp);
		}
		else if (n.getData().equals("/"))
		{
			if (isComplicatedFunction(n.getRightChild()))
				throw new TooComplicatedException("Sorry, I can't differentiate rational functions!");
			else
			{
				Polynomial temp = evaluate(n.getLeftChild());
				temp.divide(evaluate(n.getRightChild()));
				output.add(temp);
			}
		}
		else if (n.getData().equals("^"))
		{
			if (isComplicatedFunction(n.getRightChild()))
				throw new TooComplicatedException("Sorry, I can't differentiate exponential functions!");
			else
			{
				Polynomial temp = evaluate(n.getLeftChild());
				temp.pow(evaluate(n.getRightChild()));
				output.add(temp);
			}
		}
		else if (n.getData().equals("x"))
		{
			output.add(new Term(1, 1));
		}
		else
		{
			output.add(new Term(Double.valueOf(n.getData()), 0));
		}

		return output;
	}
	
	public Polynomial differentiate(Node n) throws TooComplicatedException
	{
		System.out.println("differentiate: " + n.getData());
		Polynomial output = new Polynomial(new Term(0, 0));
		if (n.getData().equals("+")) //(u + v)' = u' + v'
		{
			output.add(differentiate(n.getLeftChild()));
			output.add(differentiate(n.getRightChild()));
		}
		else if (n.getData().equals("-")) //(u - v)' = u' - v'
		{
			output.add(differentiate(n.getLeftChild()));
			Polynomial temp = differentiate(n.getRightChild());
			temp.multiply(new Term(-1, 0));
			output.add(temp);
		}
		else if (n.getData().equals("*")) //(uv)' = u'v + uv'
		{
			Polynomial temp1 = differentiate(n.getLeftChild());
			temp1.multiply(evaluate(n.getRightChild()));
			Polynomial temp2 = differentiate(n.getRightChild());
			temp2.multiply(evaluate(n.getLeftChild()));
			output.add(temp1); 
			output.add(temp2);
		}
		else if (n.getData().equals("/")) //same as multiplying, but with 1/c instead of c
		{
			if (isComplicatedFunction(n.getRightChild()))
				throw new TooComplicatedException("Sorry, I can't differentiate rational functions!");
			else
			{
				Polynomial temp1 = differentiate(n.getLeftChild());
				temp1.divide(evaluate(n.getRightChild()));
				Polynomial temp2 = differentiate(n.getRightChild());
				temp2.divide(evaluate(n.getLeftChild()));
				output.add(temp1);
				output.add(temp2);
			}
		}
		else if (n.getData().equals("^")) //(u^c)' = cu^(c-1)
		{
			if (isComplicatedFunction(n.getRightChild()))
				throw new TooComplicatedException("Sorry, I can't differentiate exponential functions!");
			else
			{
				Polynomial temp = evaluate(n.getLeftChild());
				double coefficient = evaluate(n.getRightChild()).getFirst().getCoefficient();
				double exponent = coefficient - 1;
				output.add(new Term(coefficient, exponent));
			}
		}
		else if (n.getData().equals("x")) //d/dx(x) = 1
		{
			output.add(new Term(1, 0));
		}
		else //d/dx(c) = 0
		{
			output.add(new Term(0, 0));
		}
		
		return output;
	}
}