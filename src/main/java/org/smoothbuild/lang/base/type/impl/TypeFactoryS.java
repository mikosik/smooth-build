package org.smoothbuild.lang.base.type.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;

import org.smoothbuild.lang.base.type.api.AbstractTypeFactory;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

public class TypeFactoryS extends AbstractTypeFactory {
  private static final AnySType ANY = new AnySType();
  private static final BlobSType BLOB = new BlobSType();
  private static final BoolSType BOOL = new BoolSType();
  private static final IntSType INT = new IntSType();
  private static final NothingSType NOTHING = new NothingSType();
  private static final StringSType STRING = new StringSType();

  private final Sides sides;

  public TypeFactoryS() {
    this.sides = new Sides(any(), nothing());
  }

  @Override
  public Side upper() {
    return sides.upper();
  }

  @Override
  public Side lower() {
    return sides.lower();
  }

  @Override
  public AnySType any() {
    return ANY;
  }

  @Override
  public ArraySType array(Type elemType) {
    return new ArraySType(elemType);
  }

  @Override
  public BlobSType blob() {
    return BLOB;
  }

  @Override
  public BoolSType bool() {
    return BOOL;
  }

  @Override
  public FunctionSType function(Type result, ImmutableList<? extends Type> parameters) {
    return new FunctionSType(result, ImmutableList.copyOf(parameters));
  }

  @Override
  public IntSType int_() {
    return INT;
  }

  @Override
  public NothingSType nothing() {
    return NOTHING;
  }

  @Override
  public StringSType string() {
    return STRING;
  }

  @Override
  public StructSType struct(String name, NamedList<? extends Type> fields) {
    return new StructSType(name, (NamedList<Type>) fields);
  }

  @Override
  public VariableSType variable(String name) {
    checkArgument(isVariableName(name), "Illegal type variable name '%s'.", name);
    return new VariableSType(name);
  }
}
