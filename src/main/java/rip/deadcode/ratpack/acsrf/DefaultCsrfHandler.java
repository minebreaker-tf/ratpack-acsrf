package rip.deadcode.ratpack.acsrf;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import io.netty.handler.codec.http.cookie.Cookie;
import ratpack.handling.Context;
import ratpack.http.HttpMethod;

import java.util.Optional;

import static rip.deadcode.ratpack.acsrf.Utils.getCookieOf;

public final class DefaultCsrfHandler implements CsrfHandler {

    private AntiCsrfConfig config;
    private CsrfTokenManager tokenManager;

    @Inject
    public DefaultCsrfHandler( AntiCsrfConfig config, CsrfTokenManager tokenManager ) {
        this.config = config;
        this.tokenManager = tokenManager;
    }

    @Override
    public void handle( Context ctx ) {

        HttpMethod m = ctx.getRequest().getMethod();

        if ( m.equals( HttpMethod.GET ) || m.equals( HttpMethod.OPTIONS ) ) {

            Optional<Cookie> cookie = getCookieOf( ctx, config.getTokenCookieName() );
            if ( !cookie.isPresent() || Strings.isNullOrEmpty( cookie.get().value() ) ) {
                tokenManager.generate( ctx ).then( token -> {
                    ctx.next();
                } );
            } else {
                // Just passes to next handlers if the token is already set.
                ctx.next();
            }

        } else {

            tokenManager.verify( ctx ).then( result -> {
                if ( result ) {
                    ctx.next();
                } else {
                    ctx.clientError( 403 );
                }
            } );
        }
    }

}
