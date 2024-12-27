package org.smoothbuild.compilerfrontend.lang.base;

import org.smoothbuild.compilerfrontend.lang.name.NList;

public interface NamedFunc {
  public NList<? extends Item> params();
}
