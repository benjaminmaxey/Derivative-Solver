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
		input = input.trim().replaceAll(" +", " "); //remove extra whitespace
		String output = "";
		boolean wasDigit = false;
		int i = 0;
		
		while (i < input.length())
		{
			char current = input.charAt(i);
			
			if (current == '(')
			{
				if (wasDigit)
				{
					while (!cStack.isEmpty() && (cStack.peek() == '*' || cStack.peek() == '/' || cStack.peek() == '^'))
					{
						output += cStack.pop();
						output += " ";
					}
					cStack.push('*');
				}
				cStack.push(current);
				wasDigit = false;
			}
			else if (current == ')')
			{
				while (!cStack.isEmpty() && cStack.peek() != '(')
				{
					output += cStack.pop();
					output += " ";
				}
				cStack.pop();
				wasDigit = true;
			}
			else if (current == '+')
			{
				while (!cStack.isEmpty() && (cStack.peek() == '+' || cStack.peek() == '-' || cStack.peek() == '*' || cStack.peek() == '/' || cStack.peek() == '^'))
				{
					output += cStack.pop();
					output += " ";
				}
				cStack.push(current);
				wasDigit = false;
			}
			else if (current == '-')
			{
				if (!wasDigit) //if '-' is meant as a negative sign instead of subtraction, add a 0 before '-'
				{
					output += "0 ";
				}
				while (!cStack.isEmpty() && (cStack.peek() == '+' || cStack.peek() == '-' || cStack.peek() == '*' || cStack.peek() == '/' || cStack.peek() == '^'))
				{
					output += cStack.pop();
					output += " ";
				}
				cStack.push(current);
				wasDigit = false;
			}
			else if (current == '*' || current == '/')
			{
				while (!cStack.isEmpty() && (cStack.peek() == '*' || cStack.peek() == '/' || cStack.peek() == '^'))
				{
					output += cStack.pop();
					output += " ";
				}
				cStack.push(current);
				wasDigit = false;
			}
			else if (current == '^')
			{
				while (!cStack.isEmpty() && cStack.peek() == '^')
				{
					output += cStack.pop();
					output += " ";
				}
				cStack.push(current);
				wasDigit = false;
			}
			else if ((current >= '0' && current <= '9') || current == '.')
			{
				int j = i;
				while (j + 1< input.length() && ((input.charAt(j + 1) >= '0' && input.charAt(j + 1) <= '9') || input.charAt(j + 1) == '.'))
				{
					output += input.charAt(j++);
				}
				output += input.charAt(j);
				output += " ";
				i = j;
				wasDigit = true;
			}
			else if (current == 'x')
			{
				if (wasDigit)
				{
					while (!cStack.isEmpty() && (cStack.peek() == '*' || cStack.peek() == '/' || cStack.peek() == '^'))
					{
						output += cStack.pop();
						output += " ";
					}
					cStack.push('*');
				}
				output += current;
				output += " ";
				wasDigit = true;
			}
			else if (current == ' ') {} //do nothing if space
			else
				throw new InvalidSymbolException("Invalid symbol: " + current);

			i++;
		}
		
		while (!cStack.isEmpty()) //pop whatever is left on the stack
		{
			output += cStack.pop();
			output += " ";
		}

		return output.substring(0, output.length() -1); //remove last character of the output, which will be a space
	}
	
	public Node formTree(String input) throws InvalidSymbolException
	{
		input = this.translate(input); //translate input to postfix
		int i = 0;
		
		while (i < input.length())
		{
			char current = input.charAt(i);
			if (current >= '0' && current <= '9' || current == '.')
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

	public String printTree(Node n)
	{
		String output = ""; //for debugging only
		output += n.getData() + " ";
		if (n.getLeftChild() != null)
			output += "L: " + printTree(n.getLeftChild());
		if (n.getRightChild() != null)
			output += "R: " + printTree(n.getRightChild());
		return output;
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
		Polynomial output = new Polynomial();

		if (n.getData().equals("+"))
		{
			Polynomial temp = evaluate(n.getLeftChild());
			Polynomial temp2 = evaluate(n.getRightChild());
			output.add(temp);
			output.add(temp2);
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
	
	public Polynomial differentiate(Polynomial p) throws TooComplicatedException
	{
		
		Polynomial output = new Polynomial();
		Term current = p.getFirst();

		while (current != null)
		{
			double coefficient = current.getCoefficient() * current.getExponent();
			double exponent = current.getExponent() - 1;
			output.add(new Term(coefficient, exponent));
			current = current.getNext();
		}

		return output;
	}
}