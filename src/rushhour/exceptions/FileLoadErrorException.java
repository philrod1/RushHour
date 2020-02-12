package rushhour.exceptions;

public class FileLoadErrorException extends Exception {

	private static final long serialVersionUID = 2333105938306882725L;

	public FileLoadErrorException(String msg) {
		super(msg);
	}

	public FileLoadErrorException(String msg, Throwable t) {
		super(msg, t);
	}
}
