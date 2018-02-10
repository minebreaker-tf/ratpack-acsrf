package rip.deadcode.ratpack.acsrf;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.session.Session;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A default {@link CsrfTokenManager} implementation.
 *
 * <p>
 * Generates a token which is a SHA-256 hash of the session id.
 */
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

        String tokenInRequest = context.getRequest().getHeaders().get( config.getTokenHeaderName() );

        return Promise.value( Objects.equals( tokenInRequest, hash( session.getId() ) ) );
    }

    private String hash( String sessionId ) {
        // By default, Ratpack session uses uuid-string as a session id.
        // e.g.) c43667b7-5c6a-4c8d-b42e-799481e32362 => 32 x 2
        return hash.newHasher( 72 ).putString( sessionId, StandardCharsets.UTF_8 ).hash().toString();
    }

}
