package solver;

public class Node 
{
	private Node leftChild;
	private Node rightChild;
	private String data;
	
	public Node(String s)
	{
		data = s;
	}
	
	public void setLeftChild(Node n)
	{
		leftChild = n;
	}
	
	public void setRightChild(Node n)
	{
		rightChild = n;
	}
	
	public Node getLeftChild()
	{
		return leftChild;
	}
	
	public Node getRightChild()
	{
		return rightChild;
	}
	
	public String getData()
	{
		return data;
	}
}
