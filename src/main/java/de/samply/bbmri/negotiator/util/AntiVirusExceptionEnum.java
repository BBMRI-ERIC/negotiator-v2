package de.samply.bbmri.negotiator.util;

/**
 * Created on 4/10/2018.
 */
public class AntiVirusExceptionEnum {

    /**
     * Enum for different types of anti-virus messages
     */
    public enum antiVirusMessageType {

        /**
         * The socket timeout exception
         */
        SocketTimeoutException("Read timed out"),

        /**
         * The connection refused exception
         */
        ConnectException("Connection refused");


        private String text;

        antiVirusMessageType(String text) {
            this.text = text;
        }

        public String text() {
            return text;
        }

    }

}
