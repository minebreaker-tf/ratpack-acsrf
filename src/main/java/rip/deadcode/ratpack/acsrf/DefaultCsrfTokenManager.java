package rip.deadcode.ratpack.acsrf;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import io.netty.handler.codec.http.cookie.Cookie;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.session.Session;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class DefaultCsrfTokenManager implements CsrfTokenManager {

    private final AntiCsrfConfig config;
    private final HashFunction hash = Hashing.sha256();

    @Inject
    public DefaultCsrfTokenManager( AntiCsrfConfig config ) {
        this.config = config;
    }

    @Override
    public Promise<String> generate( Context context ) {

        Session session = context.get( Session.class );
        String token = hash( session.getId() );

        context.getResponse().cookie( config.getTokenCookieName(), token );

        return Promise.value( token );
    }

    @Override
    public Promise<Boolean> verify( Context context ) {

        Session session = context.get( Session.class );

        Optional<Cookie> tokenInRequest = context.getRequest().getCookies().stream()
                                                 .filter( c -> c.name().equals( config.getTokenCookieName() ) )
                                                 .findAny();

        if ( tokenInRequest.isPresent() && tokenInRequest.get().value().equals( hash( session.getId() ) ) ) {
            return Promise.value( Boolean.TRUE );
        } else {
            return Promise.value( Boolean.FALSE );
        }
    }

    private String hash( String sessionId ) {
        // By default, Ratpack session uses uuid-string as a session id.
        return hash.newHasher( 72 ).putString( sessionId, StandardCharsets.UTF_8 ).hash().toString();
    }

}
