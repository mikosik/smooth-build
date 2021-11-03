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
import org.smoothbuild.lang.base.type.api.StringType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Variable;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

public class TypeFactoryImpl extends AbstractTypeFactory {
  private static final AnyType ANY = new AnyTypeImpl();
  private static final BlobType BLOB = new BlobTypeImpl();
  private static final BoolType BOOL = new BoolTypeImpl();
  private static final IntType INT = new IntTypeImpl();
  private static final NothingType NOTHING = new NothingTypeImpl();
  private static final StringType STRING = new StringTypeImpl();

  private final Sides sides;

  public TypeFactoryImpl() {
    this.sides = new Sides(any(), nothing());
  }

  @Override
  public AnyType any() {
    return ANY;
  }

  @Override
  public ArrayType array(Type elemType) {
    return new ArrayTypeImpl(elemType);
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
    return new FunctionTypeImpl(result, ImmutableList.copyOf(parameters));
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
    return new VariableImpl(name);
  }
}
