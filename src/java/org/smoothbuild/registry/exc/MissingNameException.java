package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.function.FunctionName;

@SuppressWarnings("serial")
public class MissingNameException extends FunctionImplementationException {

  public MissingNameException(Class<? extends Function> klass) {
    super(klass, "Class should be annotated with @" + FunctionName.class.getCanonicalName() + ".");
  }
}
