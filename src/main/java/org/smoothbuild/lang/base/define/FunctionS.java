package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.collect.NList;

/**
 * This class and all its subclasses are immutable.
 */
public sealed abstract class FunctionS extends TopEvalS
    permits ConstructorS, DefinedFunctionS, IfFunctionS, MapFunctionS, NativeFunctionS {
  public static final String PARENTHESES = "()";
  private final NList<Item> params;

  public FunctionS(FunctionTypeS type, ModulePath modulePath, String name, NList<Item> params,
      Location location) {
    super(type, modulePath, name, location);
    this.params = requireNonNull(params);
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

  public NList<Item> params() {
    return params;
  }

  public boolean canBeCalledArgless() {
    return params.stream()
        .allMatch(p -> p.defaultValue().isPresent());
  }

  protected String signature() {
    return resultType().name() + " " + name() + "(" + paramsToString() + ")";
  }

  protected String paramsToString() {
    return toCommaSeparatedString(params, Defined::typeAndName);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(`" + signature() + "`)";
  }
}
