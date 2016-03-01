package solver;

public class Polynomial
{
	private Term first;
	private int size;
	
	public Polynomial()
	{
		first = null;
		size = 0;
	}
	
	public Polynomial(Term t)
	{
		first = t;
		size = 1;
	}

	public int getSize()
	{
		return size;
	}
	
	public Term getFirst()
	{
		return first;
	}
	
	public void add(Term t)
	{
		if (first == null)
		{
			first = t;
			size++;
			return;
		}
		
		Term current = first;
		Term previous = first;
		
		while (current != null && t.getExponent() > current.getExponent()) //loop until current is a greater order than t; polynomial will automatically be in ascending order
		{
			previous = current;
			current = current.getNext();
		}
		
		if (previous.getExponent() == t.getExponent()) //if there is already a term of the same order as t in the polynomial, add like terms
		{
			previous.addLikeTerms(t);
		}
		else
		{
			if (current == null) //if loop reached end of polynomial, add t to the end of the polynomial
			{
				previous.setNext(t);
			}
			else if (current == first) //if loop body never executed, first is greater order than t; set t as first and t.next as the old first
			{
				Term temp = first;
				first = t;
				first.setNext(temp);
			}
			else //insert t between previous and current
			{
				previous.setNext(t);
				t.setNext(current);
			}
			size++;
		}
	}
	
	public void add(Polynomial p)
	{
		Term current = p.getFirst();
		
		if (first == null)
		{
			first = p.getFirst();
			size++;
		}
		else
		{
			while (current != null)
			{
				this.add(current);
				current = current.getNext();
			}
		}
	}
	
	public void multiply(Term t)
	{
		Term current = first;
		
		if (first == null)
		{
			first = new Term(0, 0);
			size++;
		}
		else
		{
			while (current != null)
			{
				current.multiply(t);
				current = current.getNext();
			}
		}
	}

	public void multiply(Polynomial p)
	{
		Term current = p.getFirst();

		if (first == null)
		{
			first = p.getFirst();
			size++;
		}
		else
		{
			while (current != null)
			{
				this.multiply(current);
				current = current.getNext();
			}
		}
	}

	public void divide(Polynomial t) throws TooComplicatedException
	{
		if (t.getSize() != 1)
			throw new TooComplicatedException("This function is too complicated for me!");
		Term current = first;

		if (first == null)
			first = new Term(0, 0);
		else
		{
			while (current != null)
			{
				current.divide(t.getFirst());
				current = current.getNext();
			}
		}
	}

	public void pow(Polynomial t) throws TooComplicatedException
	{
		if (size > 1 || t.getSize() != 1)
			throw new TooComplicatedException("This function is too complicated for me!");
		else if (size == 0)
			first = new Term(0, 0);
		else
			first.pow(t.getFirst().getCoefficient());
	}

	public String toString() 
	{
		System.out.println("got to toString");
		Term current = first;
		String output = "";
		boolean isFirstTerm = true;
		
		while (current != null)
		{
			if (current.getCoefficient() != 0)
			{
				if (current.getCoefficient() < 0)
				{
					if (isFirstTerm)
						output += "-";
					else
						output += "- ";
				}
				if (current.getExponent() == 0)
					output += Math.abs(current.getCoefficient()) + " ";
				else if (current.getExponent() == 1)
					output += Math.abs(current.getCoefficient()) + "x ";
				else
					output += Math.abs(current.getCoefficient()) + "x^" + current.getExponent() + " ";
				
				isFirstTerm = false;
			}
			
			if (current.getNext() != null && current.getNext().getCoefficient() > 0)
				output += "+ ";

			if (current.getNext() != null)
				current = current.getNext();
			else
				break;
		}
		System.out.println("got through toString");
		if (output.equals(""))
			return "0.0";
		else
			return output;
	}
}