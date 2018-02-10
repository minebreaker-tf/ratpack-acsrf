package rip.deadcode.ratpack.acsrf;

import ratpack.handling.Handler;

/**
 * Marker interface of anti-CSRF handler.
 *
 * <p>
 * Add this interface first of the handler chain.
 *
 * {@code
 * .handlers(chain - > {
 *     chain.all ( CsrfHandler.class)
 *          .all( ctx -> ctx.render( "OK" ) );
 * });
 * }
 */
public interface CsrfHandler extends Handler {}
