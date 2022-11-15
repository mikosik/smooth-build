package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class and all its subclasses are immutable.
 */
public sealed interface FuncS extends EvaluableS
    permits NamedFuncS {
  public NList<ItemS> params();

  @Override
  public FuncTS type();

  public default TypeS resT() {
    return type().res();
  }

  public default boolean canBeCalledArgless() {
    return params().stream()
        .allMatch(p -> p.defaultVal().isPresent());
  }

  public default String fieldsToString() {
    return joinToString("\n",
        "type = " + type(),
        "params = [\n" + indent(paramsToString()) + "\n]",
        "loc = " + loc()
    );
  }

  public default String paramsToString() {
    return joinToString(params(), FuncS::paramToString, "\n");
  }

  private static String paramToString(ItemS itemS) {
    return itemS.type().name() + " " + itemS.name()
        + itemS.defaultVal().map(b -> " = " + b).orElse("");
  }
}
