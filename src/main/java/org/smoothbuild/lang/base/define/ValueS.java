package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class is immutable.
 */
public sealed abstract class ValueS extends TopEvaluableS permits BoolValueS, DefinedValueS,
    NativeValueS {
  public ValueS(TypeS type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }

  @Override
  public TypeS evaluationType() {
    return type();
  }

  @Override
  public NList<Item> evaluationParameters() {
    return nList();
  }
}


