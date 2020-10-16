package eu.bbmri.eric.csit.service.negotiator.sync.directory;

public class DirectoryInvalidResponseException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 4081507164956643754L;

    /**
     * Create an instance with the given message.
     */
    public DirectoryInvalidResponseException(final String message) {
        super(message);
    }
}