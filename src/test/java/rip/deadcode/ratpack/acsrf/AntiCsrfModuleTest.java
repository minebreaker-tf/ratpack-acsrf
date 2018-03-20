package rip.deadcode.ratpack.acsrf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import ratpack.guice.Guice;
import ratpack.http.HttpMethod;
import ratpack.http.client.ReceivedResponse;
import ratpack.server.RatpackServer;
import ratpack.session.SessionModule;
import ratpack.test.embed.EmbeddedApp;

import static com.google.common.truth.Truth.assertThat;

public class AntiCsrfModuleTest {

    private RatpackServer server;

    @BeforeEach
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
            assertThat( cookie ).matches( "XSRF-TOKEN=[a-z0-9]{64}; Path=/" );
            assertThat( response.getBody().getText() ).isEqualTo( "OK" );
        } );
    }

    @Test
    public void testTokenIsSetWhenSendOptions() throws Exception {

        EmbeddedApp.fromServer( server ).test( httpClient -> {

            ReceivedResponse response = httpClient.options();

            String cookie = response.getHeaders().get( "set-cookie" );

            assertThat( response.getStatusCode() ).isEqualTo( 200 );
            assertThat( cookie ).matches( "XSRF-TOKEN=[a-z0-9]{64}; Path=/" );
            assertThat( response.getBody().getText() ).isEqualTo( "OK" );
        } );
    }

    @Test
    public void testWillNotGenerateNewTokenIfAlreadySet() throws Exception {

        EmbeddedApp.fromServer( server ).test( httpClient -> {

            ReceivedResponse response = httpClient.request(
                    request -> request.method( HttpMethod.GET )
                                      .headers( headers -> headers.set( "Cookie", "XSRF-TOKEN=abc" ) ) );

            String cookie = response.getHeaders().get( "set-cookie" );

            assertThat( response.getStatusCode() ).isEqualTo( 200 );
            assertThat( cookie ).isNull();
            assertThat( response.getBody().getText() ).isEqualTo( "OK" );
        } );
    }

    @Test
    public void testGenerateKeyIfTokenIsSetButEmpty() throws Exception {

        EmbeddedApp.fromServer( server ).test( httpClient -> {

            ReceivedResponse response = httpClient.request(
                    request -> request.method( HttpMethod.GET )
                                      .headers( headers -> headers.set( "Cookie", "XSRF-TOKEN=" ) ) );

            String cookie = response.getHeaders().get( "set-cookie" );

            assertThat( response.getStatusCode() ).isEqualTo( 200 );
            assertThat( cookie ).matches( "XSRF-TOKEN=[a-z0-9]{64}; Path=/" );
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
            assertThat( secondResponse.getStatusCode() ).isEqualTo( 403 );

            String token = firstResponse.getHeaders().get( "set-cookie" ).split( "=" )[1];
            ReceivedResponse thirdResponse = httpClient.request(
                    request -> request.headers(
                            header -> header.add( "X-XSRF-TOKEN", token ) ) );
            assertThat( thirdResponse.getStatusCode() ).isEqualTo( 200 );
            assertThat( thirdResponse.getBody().getText() ).isEqualTo( "OK" );
        } );
    }

    @Test
    public void testConfigurable() throws Exception {

        server = RatpackServer.of( spec -> {
            spec.registry( Guice.registry( bindings -> {
                bindings.module( SessionModule.class )
                        .module( new AntiCsrfModule().withConfig( new AntiCsrfConfig().withTokenCookieName( "XXX" )
                                                                                      .withTokenHeaderName( "YYY" ) ) );
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

            ReceivedResponse secondResponse = httpClient.post();
            assertThat( secondResponse.getStatusCode() ).isEqualTo( 403 );

            String[] token = firstResponse.getHeaders().get( "set-cookie" ).split( "=" );
            assertThat( token[0] ).isEqualTo( "XXX" );

            ReceivedResponse thirdResponse = httpClient.request(
                    request -> request.headers(
                            header -> header.add( "YYY", token[1] ) ) );
            assertThat( thirdResponse.getStatusCode() ).isEqualTo( 200 );
            assertThat( thirdResponse.getBody().getText() ).isEqualTo( "OK" );
        } );
    }

}