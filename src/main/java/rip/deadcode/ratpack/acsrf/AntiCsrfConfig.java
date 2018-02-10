package rip.deadcode.ratpack.acsrf;

import lombok.Data;

@Data
public final class AntiCsrfConfig {

    // https://docs.angularjs.org/api/ng/service/$http#cross-site-request-forgery-xsrf-protection
    private String tokenCookieName = "X-XSRF-TOKEN";

    public AntiCsrfConfig withTokenCookieName( String tokenCookieName ) {
        this.tokenCookieName = tokenCookieName;
        return this;
    }

}
