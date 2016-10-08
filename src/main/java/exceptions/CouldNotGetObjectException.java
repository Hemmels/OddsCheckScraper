package exceptions;

public class CouldNotGetObjectException extends Exception{

	private static final long serialVersionUID = 1L;

	public CouldNotGetObjectException() {
		super();
	}
	
	public CouldNotGetObjectException(String s){
		super(s);
	}
}
