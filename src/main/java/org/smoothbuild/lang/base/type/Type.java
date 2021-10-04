package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class Type {
  private final String name;
  private final TypeConstructor typeConstructor;
  private final ImmutableList<Type> covariants;
  private final ImmutableList<Type> contravariants;
  private final ImmutableSet<Variable> variables;

  protected Type(String name, TypeConstructor typeConstructor, ImmutableSet<Variable> variables) {
    this(name, typeConstructor, list(), list(), variables);
  }

  protected Type(String name, TypeConstructor typeConstructor, ImmutableList<Type> covariants,
      ImmutableList<Type> contravariants, ImmutableSet<Variable> variables) {
    this.name = name;
    this.typeConstructor = typeConstructor;
    this.covariants = covariants;
    this.contravariants = contravariants;
    this.variables = variables;
  }

  public String name() {
    return name;
  }

  public TypeConstructor typeConstructor() {
    return typeConstructor;
  }

  public ImmutableList<Type> covariants() {
    return covariants;
  }

  public ImmutableList<Type> contravariants() {
    return contravariants;
  }

  public String q() {
    return "`" + name + "`";
  }

  /**
   * @return true iff this type contains type variable(s).
   */
  public boolean isPolytype() {
    return !variables().isEmpty();
  }

  public boolean contains(Type that) {
    return this.equals(that)
        || covariants.stream().anyMatch(c -> c.contains(that))
        || contravariants.stream().anyMatch(c -> c.contains(that));
  }

  /**
   * @return type variables sorted alphabetically
   */
  public ImmutableSet<Variable> variables() {
      return variables;
  }

  public Type strip() {
    if (this instanceof FunctionType || this instanceof ArrayType) {
      return typeConstructor.construct(stripTypes(covariants), stripTypes(contravariants));
    } else {
      return this;
    }
  }

  private ImmutableList<Type> stripTypes(ImmutableList<Type> types) {
    return map(types, Type::strip);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Type that
        && this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }

  @Override
  public String toString() {
    return "Type(`" + name() + "`)";
  }

  public static ImmutableList<ItemSignature> toItemSignatures(List<Type> types) {
    return map(types, ItemSignature::itemSignature);
  }
}
