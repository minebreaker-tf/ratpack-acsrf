package rip.deadcode.ratpack.acsrf;

/**
 * A module configuration bean.
 *
 * <p>
 * Default values are based on <a href="https://angular.io/guide/http#security-xsrf-protection">Angular</a> settings.
 *
 * <p>
 * Note that implementations are free not to use those values, while a default one respects them.
 */
public final class AntiCsrfConfig {

    /**
     * A cookie name of the anti-CSRF token, used to store the generated token.
     */
    private String tokenCookieName = "XSRF-TOKEN";

    /**
     * Http header name, which the clients must set the token with it.
     */
    private String tokenHeaderName = "X-XSRF-TOKEN";

    /**
     * Set secure attribute to the cookie.
     */
    private boolean secure = false;

    public String getTokenCookieName() {
        return tokenCookieName;
    }

    public void setTokenCookieName( String tokenCookieName ) {
        this.tokenCookieName = tokenCookieName;
    }

    public AntiCsrfConfig withTokenCookieName( String tokenCookieName ) {
        this.tokenCookieName = tokenCookieName;
        return this;
    }

    public String getTokenHeaderName() {
        return tokenHeaderName;
    }

    public void setTokenHeaderName( String tokenHeaderName ) {
        this.tokenHeaderName = tokenHeaderName;
    }

    public AntiCsrfConfig withTokenHeaderName( String tokenHeaderName ) {
        this.tokenHeaderName = tokenHeaderName;
        return this;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure( boolean secure ) {
        this.secure = secure;
    }

    public AntiCsrfConfig withSecure( boolean secure ) {
        this.secure = secure;
        return this;
    }
}
