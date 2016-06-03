import java.util.Vector;

public class Query {
	private long _id;
	private String _title;
	private String _text;
	private Timestamp _dateTime;
	public Researcher _researcher;
	public Vector<Tag> _tag = new Vector<Tag>();
	public Vector<Tag> _tag1 = new Vector<Tag>();
	public Vector<Comment> _comment = new Vector<Comment>();
	public Vector<FlaggedQuery> _flaggedQuery = new Vector<FlaggedQuery>();
}