package rip.deadcode.ratpack.acsrf;

import ratpack.guice.ConfigurableModule;

public final class AntiCsrfModule extends ConfigurableModule<AntiCsrfConfig> {

    @Override
    protected void configure() {
        bind( CsrfHandler.class ).to( DefaultCsrfHandler.class );
        bind( CsrfTokenManager.class ).to( DefaultCsrfTokenManager.class );
    }

}
