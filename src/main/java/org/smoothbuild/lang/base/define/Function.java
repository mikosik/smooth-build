package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.define.Item.toItemSignatures;

import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Function extends Referencable {
  public static final String PARENTHESES = "()";
  private final ImmutableList<Item> parameters;

  public Function(Type resultType, ModulePath modulePath, String name,
      ImmutableList<Item> parameters, Location location) {
    super(functionType(resultType, parameters), modulePath, name, location);
    this.parameters = requireNonNull(parameters);
  }

  private static FunctionType functionType(Type resultType, ImmutableList<Item> parameters) {
    return new FunctionType(resultType, toItemSignatures(parameters));
  }

  @Override
  public FunctionType type() {
    return (FunctionType) super.type();
  }

  @Override
  public String extendedName() {
    return name() + PARENTHESES;
  }

  public Type resultType() {
    return type().resultType();
  }

  public ImmutableList<Item> parameters() {
    return parameters;
  }

  public boolean canBeCalledArgless() {
    return parameters.stream()
        .allMatch(p -> p.defaultValue().isPresent());
  }
}
