package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import org.smoothbuild.compile.fs.lang.type.FuncSchemaS;
import org.smoothbuild.util.collect.NList;

/**
 * This class and all its subclasses are immutable.
 */
public sealed interface FuncS
    extends EvaluableS
    permits ExprFuncS, NamedFuncS {
  public NList<ItemS> params();

  @Override
  public FuncSchemaS schema();

  public default boolean canBeCalledArgless() {
    return params().stream()
        .allMatch(p -> p.defaultValue().isPresent());
  }

  public default String fieldsToString() {
    return joinToString("\n",
        "schema = " + schema(),
        "params = [",
        indent(joinToString(params(), "\n")),
        "]",
        "location = " + location()
    );
  }
}
