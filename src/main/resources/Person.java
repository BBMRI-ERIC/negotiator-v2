import java.util.Vector;

public class Person {
	private long _id;
	private String _authData;
	private char _personType;
	private Blob _personImage;
	public Vector<Comment> _comment = new Vector<Comment>();
	public Vector<Owner> _owner = new Vector<Owner>();
	public Vector<Researcher> _researcher = new Vector<Researcher>();
}