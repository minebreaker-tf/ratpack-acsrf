package rip.deadcode.ratpack.acsrf;

import com.google.inject.Inject;
import ratpack.handling.Context;
import ratpack.http.HttpMethod;

public final class DefaultCsrfHandler implements CsrfHandler {

    private CsrfTokenManager tokenManager;

    @Inject
    public DefaultCsrfHandler( CsrfTokenManager tokenManager ) {
        this.tokenManager = tokenManager;
    }

    @Override
    public void handle( Context ctx ) throws Exception {

        HttpMethod m = ctx.getRequest().getMethod();

        if ( m.equals( HttpMethod.GET ) ) {

            tokenManager.generate( ctx ).then( token -> {
                ctx.next();
            } );

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
