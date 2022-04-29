package eu.bbmri.eric.csit.service.negotiator.util;

import java.util.UUID;

public class NToken {
    private String requestToken;
    private String queryToken;
    private String nToken;

    private String tokenSplitter = "__search__";

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

    public String getNewQueryToken() {
        queryToken = UUID.randomUUID().toString();
        return queryToken;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public String getNewRequestToken() {
        requestToken = UUID.randomUUID().toString();
        return requestToken;
    }

    public String getRequestTokenForUrl(String urlIdForNToken) {
        if(requestToken.length() == 0) {
            getNewRequestToken();
        }
        return urlIdForNToken + "=" + requestToken + tokenSplitter;
    }

    public String getNTokenForUrl(String urlIdForNToken) {
        if(requestToken.length() == 0) {
            requestToken = getNewRequestToken();
        }
        if(queryToken.length() == 0) {
            getNewQueryToken();
        }
        return urlIdForNToken + "=" + requestToken + tokenSplitter + queryToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public String getQueryToken() {
        return queryToken;
    }

    public void setQueryToken(String queryToken) {
        if(queryToken.contains("tokenSplitter")) {
            this.queryToken = nToken.replaceAll(".*__search__", "");
        } else {
            this.queryToken = queryToken;
        }
    }

    public String getnToken() {
        if(nToken.length() == 0) {
            if(requestToken.length() == 0) {
                getNewRequestToken();
            }
            if(queryToken.length() == 0) {
                getNewQueryToken();
            }
            nToken = requestToken + tokenSplitter + queryToken;
        }
        return nToken;
    }

    public void setnToken(String nToken) {
        this.nToken = nToken;
    }

    public void generateQueryTokenIfNotSet() {
        if(queryToken.length() == 0) {
            getNewQueryToken();
        }
    }
}
