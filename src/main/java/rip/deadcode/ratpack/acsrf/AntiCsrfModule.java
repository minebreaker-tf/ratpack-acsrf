package rip.deadcode.ratpack.acsrf;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.name.Names;
import ratpack.guice.ConfigurableModule;

/**
 * Anti-CSRF Token module.
 *
 * <p>
 * Must include ratpack-session module before this one.
 */
public final class AntiCsrfModule extends ConfigurableModule<AntiCsrfConfig> {

    public static final String HASH_FUNCTION_NAME = "AntiCsrfTokenHashFunction";

    @Override
    protected void configure() {

        bind( HashFunction.class ).annotatedWith( Names.named( HASH_FUNCTION_NAME ) ).toInstance( Hashing.sha256() );

        bind( CsrfHandler.class ).to( DefaultCsrfHandler.class );
        bind( CsrfTokenManager.class ).to( DefaultCsrfTokenManager.class );
    }

    public AntiCsrfModule withConfig( AntiCsrfConfig config ) {
        setConfig( config );
        return this;
    }

}
