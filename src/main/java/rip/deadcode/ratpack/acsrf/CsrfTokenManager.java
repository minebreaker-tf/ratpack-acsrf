package rip.deadcode.ratpack.acsrf;

import ratpack.exec.Promise;
import ratpack.handling.Context;

/**
 * Creates and verifies the token.
 *
 * <p>
 * The way to generate the token, how to store tokens, how to verify tokens are implementation dependent.
 */
public interface CsrfTokenManager {

    /**
     * Generates and stores token based on the given context.
     *
     * @param context Context
     * @return Promise of the generated token.
     */
    public Promise<String> generate( Context context );

    /**
     * Verifies the token of the given context.
     *
     * @param context Context
     * @return Promise of boolean, true if the request is valid.
     */
    public Promise<Boolean> verify( Context context );

}
