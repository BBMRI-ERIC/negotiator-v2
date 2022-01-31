package de.samply.bbmri.negotiator.util;

public class RedirectUrlGenerator {
    public RedirectUrlGenerator() {}

    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppandNewQueryToRequestUrl(NToken token) {
        if(url.contains("locator")) {
            return getAppandNewQueryToRequestUrlForLocator(token);
        } else {
            return getAppandNewQueryToRequestUrlForOthers(token);
        }
    }

    private String getAppandNewQueryToRequestUrlForLocator(NToken token) {
        String urlEnd = "/";
        if(url.endsWith("/")) {
            urlEnd = "";
        }
        url = url + urlEnd + "?ntoken=" + token.getRequestToken() + "__search__" + token.getNewQueryToken();
        return url;
    }

    private String getAppandNewQueryToRequestUrlForOthers(NToken token) {
        String urlEnd = "/";
        if(url.endsWith("/")) {
            urlEnd = "";
        }
        url = url + urlEnd + "#/?nToken=" + token.getRequestToken() + "__search__";
        return url;
    }
}
