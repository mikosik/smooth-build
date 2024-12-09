package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compilerfrontend.lang.type.SFuncSchema;

/**
 * This class and all its subclasses are immutable.
 */
public sealed interface SFunc extends SEvaluable permits SExprFunc, SNamedFunc {
  public NList<SItem> params();

  @Override
  public SFuncSchema schema();

  public default boolean canBeCalledArgless() {
    return params().stream().allMatch(p -> p.defaultValueFullName().isSome());
  }

  public default String fieldsToString() {
    var paramsString = params().list().toString("\n");
    return list(
            "schema = " + schema(),
            "params = [",
            indent(paramsString),
            "]",
            "location = " + location())
        .toString("\n");
  }
}
