package org.smoothbuild.function.plugin.exc;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public class IllegalConstructorParamException extends FunctionImplementationException {

  public IllegalConstructorParamException(Class<?> klass, Class<?> paramType,
      ImmutableList<String> allowedTypes) {
    super(klass, "The only public constructor has parameter with illegal type '"
        + paramType.getCanonicalName() + "'. Allowed types = " + allowedString(allowedTypes));
  }

  private static String allowedString(ImmutableList<String> allowedTypes) {
    return "{" + Joiner.on(", ").join(allowedTypes) + "}";
  }
}
