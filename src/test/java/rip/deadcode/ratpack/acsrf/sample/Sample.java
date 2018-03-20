package rip.deadcode.ratpack.acsrf.sample;

import ratpack.guice.Guice;
import ratpack.server.RatpackServer;
import ratpack.session.SessionModule;
import rip.deadcode.ratpack.acsrf.AntiCsrfModule;
import rip.deadcode.ratpack.acsrf.CsrfHandler;
import rip.deadcode.ratpack.acsrf.CsrfTokenManager;
import rip.deadcode.ratpack.acsrf.RandomCsrfTokenManager;

public final class Sample {

    public static void main( String[] args ) throws Exception {

        RatpackServer.start( spec -> {
            spec.registry( Guice.registry( bindings -> {
                bindings.module( SessionModule.class )
                        .module( AntiCsrfModule.class )
                        .bind( CsrfTokenManager.class, RandomCsrfTokenManager.class );
            } ) )
                .serverConfig( config -> config
                        .development( true )
                        .findBaseDir( "rip/deadcode/ratpack/acsrf/sample/.ratpack" ) )
                .handlers( chain -> {
                    chain.all( CsrfHandler.class )
                         .path( "sample.html", ctx -> ctx.render( ctx.file( "sample.html" ) ) )
                         .all( ctx -> ctx.render( "OK" ) );
                } );
        } );

    }
}
