package solver;

public class NodeStack 
{
	private Node[] nodeArray;
	private int top;
	
	public NodeStack(int maxSize)
	{
		nodeArray = new Node[maxSize];
		top = -1;
	}
	
	public void push(Node n)
	{
		nodeArray[++top] = n;
	}
	
	public Node pop()
	{
		return nodeArray[top--];
	}
	
	public Node peek()
	{
		return nodeArray[top];
	}
	
	public boolean isEmpty()
	{
		return (top < 0);
	}
}
