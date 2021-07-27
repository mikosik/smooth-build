package org.smoothbuild.lang.base.define;

import static java.util.stream.Collectors.joining;

import java.util.Objects;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.NativeExpression;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class NativeFunction extends Function {
  private final NativeExpression nativ;

  public NativeFunction(Type resultType, ModulePath modulePath, String name,
      ImmutableList<Item> parameters, NativeExpression nativ, Location location) {
    super(resultType, modulePath, name, parameters, location);
    this.nativ = nativ;
  }

  public NativeExpression nativ() {
    return nativ;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NativeFunction that
        && this.resultType().equals(that.resultType())
        && this.modulePath().equals(that.modulePath())
        && this.name().equals(that.name())
        && this.parameters().equals(that.parameters())
        && this.nativ.equals(that.nativ)
        && this.location().equals(that.location());
  }

  @Override
  public int hashCode() {
    return Objects.hash(resultType(), modulePath(), name(), parameters(), nativ, location());
  }

  @Override
  public String toString() {
    return nativ.toString() + " Function(`" + resultType() + "(" + parametersToString() + ")";
  }

  private String parametersToString() {
    return parameters()
        .stream()
        .map(Object::toString)
        .collect(joining(", "));
  }
}