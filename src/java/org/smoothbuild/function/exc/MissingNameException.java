package org.smoothbuild.function.exc;

import org.smoothbuild.plugin.FunctionName;

@SuppressWarnings("serial")
public class MissingNameException extends FunctionImplementationException {

  public MissingNameException(Class<?> klass) {
    super(klass, "Class should be annotated with @" + FunctionName.class.getCanonicalName() + ".");
  }
}
