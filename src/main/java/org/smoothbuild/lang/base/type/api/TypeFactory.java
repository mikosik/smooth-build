package org.smoothbuild.lang.base.type.api;

import com.google.common.collect.ImmutableList;

public interface TypeFactory {
  public AnyType any();

  public ArrayType array(Type elemType);

  public BlobType blob();

  public BoolType bool();

  public FunctionType function(Type result, Iterable<Type> parameters);

  public IntType int_();

  public NothingType nothing();

  public StringType string();

  public StructType struct(String name, ImmutableList<ItemSignature> fields);

  public Variable variable(String name);
}
