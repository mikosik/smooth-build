package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import java.util.Objects;

import org.smoothbuild.common.collect.NList;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.FuncSchemaS;

/**
 * Structure constructor.
 * This class is immutable.
 */
public final class ConstructorS extends NamedFuncS {
  public ConstructorS(FuncSchemaS schema, String name, NList<ItemS> params, Location location) {
    super(schema, name, params, location);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ConstructorS that
        && this.schema().equals(that.schema())
        && this.name().equals(that.name())
        && this.params().equals(that.params())
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(schema(), name(), params(), location());
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "name = " + name(),
        fieldsToString());
    return "ConstructorS(\n" + indent(fields) + "\n)";
  }
}
