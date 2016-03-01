package solver;

public class Term 
{
	private double coefficient;
	private double exponent;
	private Term next;
	
	public Term(double c, double e)
	{
		coefficient = c;
		exponent = e;
	}
	
	public boolean hasNext()
	{
		return (next != null);
	}
	
	public Term getNext()
	{
		return next;
	}
	
	public void setNext(Term t)
	{
		next = t;
	}
	
	public double getCoefficient()
	{
		return coefficient;
	}
	
	public double getExponent()
	{
		return exponent;
	}
	
	public void addLikeTerms(Term t)
	{
		coefficient += t.getCoefficient();
	}
	
	public void multiply(Term t)
	{
		coefficient *= t.getCoefficient();
		exponent += t.getExponent();
	}

	public void divide(Term t)
	{
		coefficient /= t.getCoefficient();
	}
	
	public void pow(double d)
	{
		coefficient = Math.pow(coefficient, d);
		exponent *= d;
	}
}