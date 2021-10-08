package org.smoothbuild.lang.base.type.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;

import org.smoothbuild.db.object.db.SpecDb;
import org.smoothbuild.lang.base.type.api.AnyType;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.BlobType;
import org.smoothbuild.lang.base.type.api.BoolType;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.IntType;
import org.smoothbuild.lang.base.type.api.ItemSignature;
import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.StringType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableList;

public class TypeFactoryImpl implements TypeFactory {
  private static final AnyType ANY = new AnyTypeImpl();
  private static final BlobType BLOB = new BlobTypeImpl();
  private static final BoolType BOOL = new BoolTypeImpl();
  private static final IntType INT = new IntTypeImpl();
  private static final NothingType NOTHING = new NothingTypeImpl();
  private static final StringType STRING = new StringTypeImpl();

  private final SpecDb specDb;

  public TypeFactoryImpl(SpecDb specDb) {
    this.specDb = specDb;
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
  public FunctionType function(Type resultType, Iterable<ItemSignature> parameters) {
    return new FunctionTypeImpl(resultType, ImmutableList.copyOf(parameters));
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
  public StructType struct(String name, ImmutableList<ItemSignature> fields) {
    return new StructTypeImpl(name, fields);
  }

  @Override
  public Variable variable(String name) {
    checkArgument(isVariableName(name), "Illegal type variable name '%s'", name);
    return new VariableImpl(name);
  }
}
