package rip.deadcode.ratpack.acsrf;

import com.google.common.hash.HashFunction;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.netty.handler.codec.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.server.ServerConfig;
import ratpack.session.Session;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static rip.deadcode.ratpack.acsrf.AntiCsrfModule.HASH_FUNCTION_NAME;

/**
 * A default {@link CsrfTokenManager} implementation.
 *
 * <p>
 * Generates a token which is a hash of the session id.
 * You can DI {@link HashFunction} with name {@code AntiCsrfModule.HASH_FUNCTION_NAME}.
 */
public final class SessionIdCsrfTokenManager implements CsrfTokenManager {

    private static final Logger logger = LoggerFactory.getLogger( SessionIdCsrfTokenManager.class );

    private final ServerConfig ratpackConfig;
    private final AntiCsrfConfig config;
    private final HashFunction hashFunction;

    @Inject
    public SessionIdCsrfTokenManager(
            ServerConfig ratpackConfig,
            AntiCsrfConfig config,
            @Named( HASH_FUNCTION_NAME ) HashFunction hashFunction ) {

        this.ratpackConfig = ratpackConfig;
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
        String expectedToken = hash( session.getId() );
        boolean result = Objects.equals( tokenInRequest, expectedToken );

        if ( !result && ratpackConfig.isDevelopment() ) {
            logger.warn( "Anti-CSRF token error. Expected: {}; Actual: {}", expectedToken, tokenInRequest );
        }

        return Promise.value( result );
    }

    private String hash( String sessionId ) {
        // By default, Ratpack session uses uuid-string as a session id.
        // e.g.) c43667b7-5c6a-4c8d-b42e-799481e32362 => 36 x 2 = 72
        return hashFunction.newHasher( 72 ).putString( sessionId, StandardCharsets.UTF_8 ).hash().toString();
    }

}
