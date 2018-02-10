package rip.deadcode.ratpack.acsrf;

import io.netty.handler.codec.http.cookie.Cookie;
import ratpack.handling.Context;

import java.util.Optional;

final class Utils {

    private Utils() {}

    static Optional<Cookie> getCookieOf( Context context, String name ) {
        return context.getRequest().getCookies().stream()
                      .filter( c -> c.name().equals( name ) )
                      .findAny();
    }

}
