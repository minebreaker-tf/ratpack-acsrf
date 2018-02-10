package rip.deadcode.ratpack.acsrf;

import ratpack.guice.ConfigurableModule;

/**
 * Anti-CSRF Token module.
 * Must include ratpack-session module before this one.
 */
public final class AntiCsrfModule extends ConfigurableModule<AntiCsrfConfig> {

    @Override
    protected void configure() {
        bind( CsrfHandler.class ).to( DefaultCsrfHandler.class );
        bind( CsrfTokenManager.class ).to( DefaultCsrfTokenManager.class );
    }

    public AntiCsrfModule withConfig( AntiCsrfConfig config ) {
        setConfig( config );
        return this;
    }

}
