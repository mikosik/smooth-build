package org.smoothbuild.compile.frontend.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compile.frontend.lang.type.FuncSchemaS;

/**
 * This class and all its subclasses are immutable.
 */
public sealed interface FuncS extends EvaluableS permits ExprFuncS, NamedFuncS {
  public NList<ItemS> params();

  @Override
  public FuncSchemaS schema();

  public default boolean canBeCalledArgless() {
    return params().stream().allMatch(p -> p.defaultValue().isSome());
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
