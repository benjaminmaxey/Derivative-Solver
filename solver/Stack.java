package solver;

public class Stack 
{
	private char[] charArray;
	private double[] doubleArray;
	private int topChar;
	private int topDouble;
	
	public Stack(int maxSize)
	{
		charArray = new char[maxSize];
		doubleArray = new double[maxSize];
		topChar = -1;
		topDouble = -1;
	}
	
	public boolean isEmpty()
	{
		return (topChar < 0);
	}
	
	public void push(char input)
	{
		charArray[++topChar] = input;
	}
	
	public char pop()
	{
		return charArray[topChar--];
	}
	
	public char peek()
	{
		return charArray[topChar];
	}
	
	public void pushDouble(double input)
	{
		doubleArray[++topDouble] = input;
	}
	
	public double popDouble()
	{
		return doubleArray[topDouble--];
	}
	
	public double peekDouble()
	{
		return doubleArray[topDouble];
	}
}