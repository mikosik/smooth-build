package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.impl.FunctionSType;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Function extends GlobalReferencable {
  public static final String PARENTHESES = "()";
  private final ImmutableList<Item> parameters;

  public Function(FunctionSType type, ModulePath modulePath, String name,
      ImmutableList<Item> parameters, Location location) {
    super(type, modulePath, name, location);
    this.parameters = requireNonNull(parameters);
  }

  @Override
  public FunctionSType type() {
    return (FunctionSType) super.type();
  }

  @Override
  public String extendedName() {
    return name() + PARENTHESES;
  }

  public TypeS resultType() {
    return type().result();
  }

  public ImmutableList<Item> parameters() {
    return parameters;
  }

  @Override
  public TypeS evaluationType() {
    return resultType();
  }

  @Override
  public ImmutableList<Item> evaluationParameters() {
    return parameters();
  }

  public boolean canBeCalledArgless() {
    return parameters.stream()
        .allMatch(p -> p.defaultValue().isPresent());
  }
}
