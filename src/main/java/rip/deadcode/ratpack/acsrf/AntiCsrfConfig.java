package rip.deadcode.ratpack.acsrf;

import lombok.Data;

@Data
public final class AntiCsrfConfig {

    // https://docs.angularjs.org/api/ng/service/$http#cross-site-request-forgery-xsrf-protection

    /**
     * A cookie name of the anti-CSRF token.
     */
    private String tokenCookieName = "X-XSRF-TOKEN";

    public AntiCsrfConfig withTokenCookieName( String tokenCookieName ) {
        this.tokenCookieName = tokenCookieName;
        return this;
    }

}
