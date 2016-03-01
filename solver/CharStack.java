package solver;

public class CharStack 
{
	private char[] charArray;
	private int top;
	
	public CharStack(int maxSize)
	{
		charArray = new char[maxSize];
		top = -1;
	}
	
	public void push(char c)
	{
		charArray[++top] = c;
	}
	
	public char pop()
	{
		return charArray[top--];
	}
	
	public char peek()
	{
		return charArray[top];
	}
	
	public boolean isEmpty()
	{
		return (top < 0);
	}
}
