package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.type.FuncSchemaS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class and all its subclasses are immutable.
 */
public sealed interface FuncS extends EvaluableS
    permits NamedFuncS {
  public NList<ItemS> params();

  @Override
  public FuncSchemaS schema();

  public default TypeS resT() {
    return schema().type().res();
  }

  public default boolean canBeCalledArgless() {
    return params().stream()
        .allMatch(p -> p.defaultValue().isPresent());
  }

  public default String fieldsToString() {
    return joinToString("\n",
        "schema = " + schema(),
        "params = [\n" + indent(paramsToString()) + "\n]",
        "loc = " + loc()
    );
  }

  public default String paramsToString() {
    return joinToString(params(), FuncS::paramToString, "\n");
  }

  private static String paramToString(ItemS itemS) {
    return itemS.type().name() + " " + itemS.name()
        + itemS.defaultValue().map(b -> " = " + b).orElse("");
  }
}
