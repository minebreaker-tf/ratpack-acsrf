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

public class AntiCsrfModuleTest {

    private RatpackServer server;

    @Before
    public void setUp() throws Exception {
        server = RatpackServer.of( spec -> {
            spec.registry( Guice.registry( bindings -> {
                bindings.module( SessionModule.class )
                        .module( AntiCsrfModule.class );
            } ) )
                .handlers( chain -> {
                    chain.all( CsrfHandler.class )
                         .all( ctx -> ctx.render( "OK" ) );
                } );
        } );
    }

    @Test
    public void testTokenIsSetWhenSendGet() throws Exception {

        EmbeddedApp.fromServer( server ).test( httpClient -> {

            ReceivedResponse response = httpClient.get();

            String cookie = response.getHeaders().get( "set-cookie" );

            assertThat( response.getStatusCode() ).isEqualTo( 200 );
            assertThat( cookie ).matches( "X-XSRF-TOKEN=[a-z0-9]{64}" );
            assertThat( response.getBody().getText() ).isEqualTo( "OK" );
        } );
    }

    @Test
    public void testErrorWhenSendPostWithoutToken() throws Exception {

        EmbeddedApp.fromServer( server ).test( httpClient -> {

            ReceivedResponse response = httpClient.post();

            assertThat( response.getStatusCode() ).isEqualTo( 403 );
        } );
    }

    @Test
    public void testSuccessWhenRequestIsWithToken() throws Exception {

        EmbeddedApp.fromServer( server ).test( httpClient -> {

            ReceivedResponse firstResponse = httpClient.get();
            assertThat( firstResponse.getStatusCode() ).isEqualTo( 200 );
            assertThat( firstResponse.getBody().getText() ).isEqualTo( "OK" );

            ReceivedResponse secondResponse = httpClient.post();
            assertThat( secondResponse.getStatusCode() ).isEqualTo( 200 );
            assertThat( secondResponse.getBody().getText() ).isEqualTo( "OK" );


            ReceivedResponse thirdResponse = httpClient.request(
                    request -> request.method( HttpMethod.POST )
                                      .headers( headers -> headers.clear() ) );
            assertThat( thirdResponse.getStatusCode() ).isEqualTo( 403 );
        } );
    }

    @Test
    public void testConfigurable() throws Exception {

        server = RatpackServer.of( spec -> {
            spec.registry( Guice.registry( bindings -> {
                bindings.module( SessionModule.class )
                        .module( new AntiCsrfModule().withConfig( new AntiCsrfConfig().withTokenCookieName( "XXX" ) ) );
            } ) )
                .handlers( chain -> {
                    chain.all( CsrfHandler.class )
                         .all( ctx -> ctx.render( "OK" ) );
                } );
        } );

        EmbeddedApp.fromServer( server ).test( httpClient -> {

            ReceivedResponse firstResponse = httpClient.get();
            assertThat( firstResponse.getStatusCode() ).isEqualTo( 200 );
            assertThat( firstResponse.getBody().getText() ).isEqualTo( "OK" );

            String tokenName = firstResponse.getHeaders().get( "set-cookie" ).split( "=" )[0];
            assertThat( tokenName ).isEqualTo( "XXX" );

            ReceivedResponse secondResponse = httpClient.post();
            assertThat( secondResponse.getStatusCode() ).isEqualTo( 200 );
            assertThat( secondResponse.getBody().getText() ).isEqualTo( "OK" );


            ReceivedResponse thirdResponse = httpClient.request(
                    request -> request.method( HttpMethod.POST )
                                      .headers( headers -> headers.clear() ) );
            assertThat( thirdResponse.getStatusCode() ).isEqualTo( 403 );
        } );
    }

}