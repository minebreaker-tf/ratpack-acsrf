package rip.deadcode.ratpack.acsrf;

import org.junit.Before;
import org.junit.Test;
import ratpack.guice.Guice;
import ratpack.http.HttpMethod;
import ratpack.http.client.ReceivedResponse;
import ratpack.server.RatpackServer;
import ratpack.session.SessionModule;
import ratpack.test.embed.EmbeddedApp;

import static com.google.common.truth.Truth.assertThat;

public final class CustomHeaderCsrfTokenManagerTest {

    private RatpackServer server;

    @Before
    public void setUp() throws Exception {
        server = RatpackServer.of( spec -> {
            spec.registry( Guice.registry( bindings -> {
                bindings.module( SessionModule.class )
                        .module( new AntiCsrfModule().withConfig(
                                new AntiCsrfConfig().withTokenHeaderName( "X-Requested-With" ) ) )
                        .bind( CsrfTokenManager.class, CustomHeaderCsrfTokenManager.class );
            } ) )
                .handlers( chain -> {
                    chain.all( CsrfHandler.class )
                         .all( ctx -> ctx.render( "OK" ) );
                } );
        } );
    }

    @Test
    public void testGet() throws Exception {

        EmbeddedApp.fromServer( server ).test( httpClient -> {

            ReceivedResponse response = httpClient.get();

            assertThat( response.getStatusCode() ).isEqualTo( 200 );
            assertThat( response.getBody().getText() ).isEqualTo( "OK" );
        } );
    }

    @Test
    public void testOptions() throws Exception {

        EmbeddedApp.fromServer( server ).test( httpClient -> {

            ReceivedResponse response = httpClient.options();

            assertThat( response.getStatusCode() ).isEqualTo( 200 );
            assertThat( response.getBody().getText() ).isEqualTo( "OK" );
        } );
    }

    @Test
    public void testPost() throws Exception {

        EmbeddedApp.fromServer( server ).test( httpClient -> {

            ReceivedResponse failingResponse = httpClient.post();
            assertThat( failingResponse.getStatusCode() ).isEqualTo( 403 );

            ReceivedResponse successfulResponse = httpClient.request(
                    request -> request.method( HttpMethod.POST )
                                      .headers( headers -> headers.add( "X-Requested-With", "XHR" ) ) );
            assertThat( successfulResponse.getStatusCode() ).isEqualTo( 200 );
            assertThat( successfulResponse.getBody().getText() ).isEqualTo( "OK" );
        } );
    }

}
