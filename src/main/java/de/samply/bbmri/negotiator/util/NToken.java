package de.samply.bbmri.negotiator.util;

public class NToken {
    private String requestToken;
    private String queryToken;
    private String nToken;

    public NToken(String nToken) {
        this.nToken = nToken;
        this.requestToken = nToken.replaceAll("__search__.*", "");
        this.queryToken = nToken.replaceAll(".*__search__", "");
    }

    public NToken() {
        requestToken = "";
        queryToken = "";
        nToken = "";
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public String getQueryToken() {
        return queryToken;
    }

    public void setQueryToken(String queryToken) {
        this.queryToken = queryToken;
    }

    public String getnToken() {
        return nToken;
    }

    public void setnToken(String nToken) {
        this.nToken = nToken;
    }
}
