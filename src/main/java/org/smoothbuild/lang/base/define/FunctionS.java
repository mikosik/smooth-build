package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NamedList;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class FunctionS extends GlobalReferencable {
  public static final String PARENTHESES = "()";
  private final NamedList<Item> parameters;

  public FunctionS(FunctionTypeS type, ModulePath modulePath, String name,
      NamedList<Item> parameters, Location location) {
    super(type, modulePath, name, location);
    this.parameters = requireNonNull(parameters);
  }

  @Override
  public FunctionTypeS type() {
    return (FunctionTypeS) super.type();
  }

  @Override
  public String extendedName() {
    return name() + PARENTHESES;
  }

  public TypeS resultType() {
    return type().result();
  }

  public NamedList<Item> parameters() {
    return parameters;
  }

  @Override
  public TypeS evaluationType() {
    return resultType();
  }

  @Override
  public NamedList<Item> evaluationParameters() {
    return parameters;
  }

  public boolean canBeCalledArgless() {
    return parameters.list().stream()
        .allMatch(p -> p.defaultValue().isPresent());
  }

  protected String signature() {
    return resultType().name() + " " + name() + "(" + parametersToString() + ")";
  }

  protected String parametersToString() {
    return toCommaSeparatedString(parameters.list(), Defined::typeAndName);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(`" + signature() + "`)";
  }
}
