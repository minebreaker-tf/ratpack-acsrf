package rip.deadcode.ratpack.acsrf;

import com.google.common.hash.HashFunction;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.netty.handler.codec.http.cookie.Cookie;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.session.Session;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static rip.deadcode.ratpack.acsrf.AntiCsrfModule.HASH_FUNCTION_NAME;

/**
 * A default {@link CsrfTokenManager} implementation.
 *
 * <p>
 * Generates a token which is a hashFunction of the session id.
 * You can DI {@link HashFunction} with name {@code AntiCsrfModule.HASH_FUNCTION_NAME}.
 */
public final class SessionIdCsrfTokenManager implements CsrfTokenManager {

    private final AntiCsrfConfig config;
    private final HashFunction hashFunction;

    @Inject
    public SessionIdCsrfTokenManager(
            AntiCsrfConfig config,
            @Named( HASH_FUNCTION_NAME ) HashFunction hashFunction ) {

        this.config = config;
        this.hashFunction = hashFunction;
    }

    @Override
    public Promise<String> generate( Context context ) {

        Session session = context.get( Session.class );
        String token = hash( session.getId() );

        Cookie cookie = context.getResponse().cookie( config.getTokenCookieName(), token );
        // Expires cookie immediately when the client finished
        cookie.setMaxAge( Cookie.UNDEFINED_MAX_AGE );
        cookie.setSecure( config.isSecure() );

        return Promise.value( token );
    }

    @Override
    public Promise<Boolean> verify( Context context ) {

        Session session = context.get( Session.class );

        String tokenInRequest = context.getRequest().getHeaders().get( config.getTokenHeaderName() );

        return Promise.value( Objects.equals( tokenInRequest, hash( session.getId() ) ) );
    }

    private String hash( String sessionId ) {
        // By default, Ratpack session uses uuid-string as a session id.
        // e.g.) c43667b7-5c6a-4c8d-b42e-799481e32362 => 32 x 2
        return hashFunction.newHasher( 72 ).putString( sessionId, StandardCharsets.UTF_8 ).hash().toString();
    }

}
