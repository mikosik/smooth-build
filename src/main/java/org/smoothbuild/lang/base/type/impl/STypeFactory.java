package org.smoothbuild.lang.base.type.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;

import org.smoothbuild.lang.base.type.api.AbstractTypeFactory;
import org.smoothbuild.lang.base.type.api.AnyType;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.BlobType;
import org.smoothbuild.lang.base.type.api.BoolType;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.IntType;
import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.StringType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Variable;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

public class STypeFactory extends AbstractTypeFactory {
  private static final AnyType ANY = new AnySType();
  private static final BlobType BLOB = new BlobSType();
  private static final BoolType BOOL = new BoolSType();
  private static final IntType INT = new IntSType();
  private static final NothingType NOTHING = new NothingSType();
  private static final StringType STRING = new StringTypeImpl();

  private final Sides sides;

  public STypeFactory() {
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
  public AnyType any() {
    return ANY;
  }

  @Override
  public ArrayType array(Type elemType) {
    return new ArraySType(elemType);
  }

  @Override
  public BlobType blob() {
    return BLOB;
  }

  @Override
  public BoolType bool() {
    return BOOL;
  }

  @Override
  public FunctionType function(Type result, ImmutableList<? extends Type> parameters) {
    return new FunctionSType(result, ImmutableList.copyOf(parameters));
  }

  @Override
  public IntType int_() {
    return INT;
  }

  @Override
  public NothingType nothing() {
    return NOTHING;
  }

  @Override
  public StringType string() {
    return STRING;
  }

  @Override
  public StructType struct(String name, NamedList<? extends Type> fields) {
    return new StructTypeImpl(name, (NamedList<Type>) fields);
  }

  @Override
  public Variable variable(String name) {
    checkArgument(isVariableName(name), "Illegal type variable name '%s'.", name);
    return new VariableSType(name);
  }
}
