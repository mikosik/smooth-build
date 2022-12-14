package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.StructTS;

public class StructDefS extends TDefS {
  public StructDefS(StructTS type, Location location) {
    super(type, type.name(), location);
  }
}
