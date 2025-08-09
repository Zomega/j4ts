package java.net;

import static def.js.Globals.eval;
import static jsweet.util.Lang.*;

import def.dom.URL;
import def.js.Function;
import def.js.Object;
import java.util.function.Supplier;

public class InternalJsURLFactory {
  static final Function jsURLCtor = getJsURLConstructor();

  private static Function getJsURLConstructor() {
    Function URLConstructor;
    if (System.ENVIRONMENT_IS_NODE) {
      URLConstructor = eval("global.URL || (global.URL = require(\"url\").URL)");
    } else if (System.ENVIRONMENT_IS_SHELL) {
      Class<InternalJsURLForShell> internalJsURLForShellClass = InternalJsURLForShell.class;
      URLConstructor = eval("this.URL || (this.URL = internalJsURLForShellClass)");
    } else {
      URLConstructor = (Function) function((Supplier<Object>) () -> eval("URL")).call(null);
    }
    return URLConstructor;
  }

  public static URL newJsURL(java.lang.Object... objects) {

    Function ctor = jsURLCtor;
    return $new($insert("ctor.bind.apply(ctor, [null].concat(objects))"));
  }

  private InternalJsURLFactory() {
    // Hides the constructor.
  }
}
