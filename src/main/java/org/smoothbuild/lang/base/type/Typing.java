package org.smoothbuild.lang.base.type;

import javax.inject.Singleton;

import com.google.common.collect.ImmutableSet;

@Singleton
public class Typing {
  public ImmutableSet<BaseType> baseTypes() {
    return Types.BASE_TYPES;
  }

  public ImmutableSet<BaseType> inferableBaseTypes() {
    return Types.INFERABLE_BASE_TYPES;
  }

  public Variable variable(String name) {
    return Types.variable(name);
  }

  public AnyType anyT() {
    return Types.anyT();
  }

  public ArrayType arrayT(Type elemType) {
    return Types.arrayT(elemType);
  }

  public BlobType blobT() {
    return Types.blobT();
  }

  public BoolType boolT() {
    return Types.boolT();
  }

  public IntType intT() {
    return Types.intT();
  }

  public NothingType nothingT() {
    return Types.nothingT();
  }

  public StringType stringT() {
    return Types.stringT();
  }

  public StructType structT(String name, Iterable<ItemSignature> fields) {
    return Types.structT(name, fields);
  }

  public FunctionType functionT(Type resultType, Iterable<ItemSignature> parameters) {
    return Types.functionT(resultType, parameters);
  }
}
