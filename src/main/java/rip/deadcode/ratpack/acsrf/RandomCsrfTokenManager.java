package rip.deadcode.ratpack.acsrf;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;
import io.netty.handler.codec.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.server.ServerConfig;
import ratpack.session.Session;
import ratpack.session.SessionKey;

import java.util.Objects;
import java.util.UUID;

@Beta
public final class RandomCsrfTokenManager implements CsrfTokenManager {

    private static final Logger logger = LoggerFactory.getLogger( RandomCsrfTokenManager.class );

    private static final SessionKey<String> tokenKey = SessionKey.of( UUID.randomUUID().toString(), String.class );

    private final ServerConfig ratpackConfig;
    private final AntiCsrfConfig config;

    @Inject
    public RandomCsrfTokenManager( ServerConfig ratpackConfig, AntiCsrfConfig config ) {
        this.ratpackConfig = ratpackConfig;
        this.config = config;
    }

    @Override
    public Promise<String> generate( Context context ) {

        // UUID.randomUUID() guaranteed to generate cryptographically secure UUID.
        String token = UUID.randomUUID().toString();

        Cookie cookie = context.getResponse().cookie( config.getTokenCookieName(), token );
        // Expires cookie immediately when the client finished
        cookie.setMaxAge( Cookie.UNDEFINED_MAX_AGE );
        cookie.setSecure( config.isSecure() );

        return context.get( Session.class ).set( tokenKey, token ).map( () -> token );
    }

    @Override
    public Promise<Boolean> verify( Context context ) {

        String tokenInRequest = context.getRequest().getHeaders().get( config.getTokenHeaderName() );

        return context.get( Session.class ).get( tokenKey )
                      .map( storedToken -> {
                          if ( !storedToken.isPresent() ) {
                              if (ratpackConfig.isDevelopment()) {
                                  logger.warn( "Anti-CSRF token is not set." );
                              }
                              return false;
                          }

                          boolean result = Objects.equals( storedToken.get(), tokenInRequest );

                          if ( !result && ratpackConfig.isDevelopment() ) {
                              logger.warn(
                                      "Anti-CSRF token does not match. Expected: {}; Actual: {}",
                                      storedToken.get(),
                                      tokenInRequest
                              );
                          }
                          return result;
                      } );
    }

}
