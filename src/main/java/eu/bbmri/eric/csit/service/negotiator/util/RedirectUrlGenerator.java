package eu.bbmri.eric.csit.service.negotiator.util;

public class RedirectUrlGenerator {
    public RedirectUrlGenerator() {}

    private String url;

    public void setUrl(String url) {
        this.url = url.replaceAll("[\\?&]nToken=[A-Za-z0-9\\-]*__search__[A-Za-z0-9\\-]*", "");
    }

    public String getAppandNewQueryToRequestUrl(NToken token) {
        if(url.contains("locator")) {
            return getAppandNewQueryToRequestUrlForLocator(token);
        } else if(url.contains("finder")) {
            return getAppandNewQueryToRequestUrlForFinder(token);
        } else {
            return getAppandNewQueryToRequestUrlForOthers(token);
        }
    }

    public String getNewRequestUrl() {
        if(url.contains("locator")) {
            return getNewRequestUrlForLocator();
        } else if(url.contains("finder")) {
            return getNewRequestUrlForFinder();
        } else {
            return getNewRequestUrlForOthers();
        }
    }

    private String getAppandNewQueryToRequestUrlForLocator(NToken token) {
        String urlEnd = "/";
        if(url.endsWith("/")) {
            urlEnd = "";
        }
        url = url + urlEnd + "?" + token.getNTokenForUrl("nToken");
        return url;
    }

    private String getAppandNewQueryToRequestUrlForFinder(NToken token) {
        String urlEnd = "/";
        if(url.endsWith("/")) {
            urlEnd = "";
        }
        url = url + urlEnd + "?" + token.getNTokenForUrl("nToken");
        return url;
    }

    private String getAppandNewQueryToRequestUrlForOthers(NToken token) {
        String urlEnd = "/";
        if(url.endsWith("/")) {
            urlEnd = "";
        }
        url = url + urlEnd + "#/?" + token.getRequestTokenForUrl("nToken");
        return url;
    }

    private String getNewRequestUrlForLocator() {
        String urlEnd = "/";
        if(url.endsWith("/")) {
            urlEnd = "";
        }
        NToken token = new NToken();
        url = url + urlEnd + "?" + token.getRequestTokenForUrl("nToken");
        return url;
    }

    private String getNewRequestUrlForFinder() {
        String urlEnd = "/";
        if(url.endsWith("/")) {
            urlEnd = "";
        }
        url = url + urlEnd;
        return url;
    }

    private String getNewRequestUrlForOthers() {
        String urlEnd = "/";
        if(url.endsWith("/")) {
            urlEnd = "";
        }
        url = url + urlEnd + "#/";
        return url;
    }


}
