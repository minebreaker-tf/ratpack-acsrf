package rip.deadcode.ratpack.acsrf;

import lombok.Data;

/**
 * A module configuration bean.
 *
 * Default values are based on
 */
@Data
public final class AntiCsrfConfig {

    // https://docs.angularjs.org/api/ng/service/$http#cross-site-request-forgery-xsrf-protection

    /**
     * A cookie name of the anti-CSRF token, used to store the generated token.
     */
    private String tokenCookieName = "XSRF-TOKEN";

    /**
     * Http header name, which the clients must set the token.
     */
    private String tokenHeaderName = "X-XSRF-TOKEN";

    public AntiCsrfConfig withTokenCookieName( String tokenCookieName ) {
        this.tokenCookieName = tokenCookieName;
        return this;
    }

    public AntiCsrfConfig withTokenHeaderName( String tokenHeaderName ) {
        this.tokenHeaderName = tokenHeaderName;
        return this;
    }

}
