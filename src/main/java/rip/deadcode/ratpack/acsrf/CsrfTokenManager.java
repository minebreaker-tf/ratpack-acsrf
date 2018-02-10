package rip.deadcode.ratpack.acsrf;

import ratpack.exec.Promise;
import ratpack.handling.Context;

public interface CsrfTokenManager {

    public Promise<String> generate( Context context );

    public Promise<Boolean> verify( Context context );

}
