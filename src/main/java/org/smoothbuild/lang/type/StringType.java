package org.smoothbuild.lang.type;

import org.smoothbuild.lang.value.SString;

public class StringType extends Type {
  protected StringType() {
    super("String", SString.class);
  }
}
