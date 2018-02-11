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
     * This method does not have to be idempotent,
     * i.e. may generate different values for each call with the same Context.
     * Usually, this method is called only the token has not been generated.
     * If the token is already set, this method may not be called.
     * This behavior assumes session id will not change, that is not true.
     * Thus, user <b>MUST</b> call this method manually if the session id is renewed.
     *
     * @param context Context
     * @return Promise of the generated token.
     */
    public Promise<String> generate( Context context );

    /**
     * Verifies the token of the given context.
     * Unlike {@link #generate(Context)}, this method must be idempotent,
     * i.e. always returns same result for each session with the same token.
     *
     * @param context Context
     * @return Promise of boolean, true if the request is valid.
     */
    public Promise<Boolean> verify( Context context );

}
