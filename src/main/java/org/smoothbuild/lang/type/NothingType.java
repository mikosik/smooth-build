package org.smoothbuild.lang.type;

import org.smoothbuild.lang.value.Nothing;

public class NothingType extends Type {
  protected NothingType() {
    super("Nothing", Nothing.class);
  }
}
