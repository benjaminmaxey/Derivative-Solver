package solver;

import java.text.DecimalFormat;
import java.math.RoundingMode;

public class Polynomial
{
	private Term first;
	
	public Polynomial()
	{
		first = null;
	}
	
	public Polynomial(Term t)
	{
		first = t;
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
			return;
		}
		
		Term current = first;
		Term previous = first;
		
		while (current != null && t.getExponent() >= current.getExponent()) //loop until current is a greater order than t; polynomial will automatically be in ascending order
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
				Term temp = new Term(t.getCoefficient(), t.getExponent());
				previous.setNext(temp);
			}
			else if (current == first) //if loop body never executed, first is greater order than t; set t as first and t.next as the old first
			{
				Term temp = first;
				first = new Term(t.getCoefficient(), t.getExponent());
				first.setNext(temp);
			}
			else //insert t between previous and current
			{
				Term temp = new Term(t.getCoefficient(), t.getExponent());
				previous.setNext(temp);
				temp.setNext(current);
			}
		}
	}
	
	public void add(Polynomial p)
	{
		Term newCurrent = p.getFirst();
		Term oldCurrent = first;
		Polynomial output = new Polynomial();

		while (newCurrent != null)
		{
			Term temp = new Term(newCurrent.getCoefficient(), newCurrent.getExponent());
			output.add(temp);
			newCurrent = newCurrent.getNext();
		}
		while (oldCurrent != null)
		{
			Term temp = new Term(oldCurrent.getCoefficient(), oldCurrent.getExponent());
			output.add(temp);
			oldCurrent = oldCurrent.getNext();
		}

		first = output.getFirst(); //set the output as the polynomial itself
	}
	
	public void multiply(Term t)
	{
		Term current = first;
		
		if (first == null)
		{
			first = new Term(0, 0);
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
		Term newCurrent = p.getFirst();
		Term oldCurrent = first;
		Polynomial output = new Polynomial();

		while (newCurrent != null)
		{
			oldCurrent = first;
			while (oldCurrent != null)
			{
				Term temp = new Term(oldCurrent.getCoefficient(), oldCurrent.getExponent());
				temp.multiply(newCurrent);
				output.add(temp);
				oldCurrent = oldCurrent.getNext();
			}
			newCurrent = newCurrent.getNext();
		}

		first = output.getFirst(); //set the output as the polynomial itself
	}

	public void divide(Polynomial t) throws TooComplicatedException
	{
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
		first.pow(t.getFirst().getCoefficient());
	}

	public String toString() 
	{
		Term current = first;
		String output = "";

		DecimalFormat df = new DecimalFormat("#.###");
		df.setRoundingMode(RoundingMode.HALF_UP);
		
		while (current != null)
		{
			if (current.getCoefficient() != 0)
			{
				if (output.length() == 0 && current.getCoefficient() < 0)
					output += "-";

				if (current.getExponent() == 0)
				{
					double coeff = Math.abs(current.getCoefficient());
					output += df.format(coeff) + " ";
				}
				else if (current.getExponent() == 1)
				{
					double coeff = Math.abs(current.getCoefficient());
					output += df.format(coeff) + "x ";
				}
				else
				{
					double coeff = Math.abs(current.getCoefficient());
					output += df.format(coeff) + "x^" + df.format(current.getExponent()) + " ";
				}

				if (current.getNext() != null && current.getNext().getCoefficient() >= 0)
					output += "+ ";
				else if (current.getNext() != null && current.getNext().getCoefficient() < 0)
					output += "- ";
			}

			if (current.getNext() != null)
				current = current.getNext();
			else
				break;
		}

		if (output.startsWith("0.0 + "))
			output = output.substring(6, output.length());
		if (output.startsWith("0.0 - "))
		{
			String temp = output.substring(6, output.length());
			output = "-" + temp;
		}
		output = output.substring(0, output.length() - 1); //remove extra whitespace at end of output
		return output;
	}
}