package rip.deadcode.ratpack.acsrf;

import com.google.inject.Inject;
import ratpack.handling.Context;
import ratpack.http.HttpMethod;

import static rip.deadcode.ratpack.acsrf.Utils.*;

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

        if ( m.equals( HttpMethod.GET ) ) {

            if ( !getCookieOf( ctx, config.getTokenCookieName() ).isPresent() ) {
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
