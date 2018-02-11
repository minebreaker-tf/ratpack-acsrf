package rip.deadcode.ratpack.acsrf;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;
import ratpack.exec.Promise;
import ratpack.handling.Context;

@Beta
public final class CustomHeaderCsrfTokenManager implements CsrfTokenManager {

    private AntiCsrfConfig config;

    @Inject
    public CustomHeaderCsrfTokenManager( AntiCsrfConfig config ) {
        this.config = config;
    }

    @Override
    public Promise<String> generate( Context context ) {
        // noop
        return Promise.value( "" );
    }

    @Override
    public Promise<Boolean> verify( Context context ) {
        return Promise.value( context.getRequest().getHeaders().contains( config.getTokenHeaderName() ) );
    }

}
