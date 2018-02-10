package rip.deadcode.ratpack.acsrf;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;
import io.netty.handler.codec.http.cookie.Cookie;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.session.Session;
import ratpack.session.SessionKey;

import java.util.Objects;
import java.util.UUID;

@Beta
public final class RandomCsrfTokenManager implements CsrfTokenManager {

    private static final SessionKey<String> tokenKey = SessionKey.of( UUID.randomUUID().toString(), String.class );
    private final AntiCsrfConfig config;

    @Inject
    public RandomCsrfTokenManager( AntiCsrfConfig config ) {
        this.config = config;
    }

    @Override
    public Promise<String> generate( Context context ) {

        // UUID.randomUUID() guaranteed to generate cryptographically secure UUID.
        String token = UUID.randomUUID().toString();

        context.getResponse()
               .cookie( config.getTokenCookieName(), token )
               // Expires cookie immediately when the client finished
               .setMaxAge( Cookie.UNDEFINED_MAX_AGE );

        return context.get( Session.class ).set( tokenKey, token ).map( () -> token );
    }

    @Override
    public Promise<Boolean> verify( Context context ) {

        String tokenInRequest = context.getRequest().getHeaders().get( config.getTokenHeaderName() );

        return context.get( Session.class ).get( tokenKey )
                      .map( storedToken -> storedToken.isPresent() &&
                                           Objects.equals( storedToken.get(), tokenInRequest ) );
    }

}
