package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

public interface TypeFactory {
  public AnyType any();

  public ArrayType array(Type elemType);

  public BlobType blob();

  public BoolType bool();

  public FunctionType function(Type result, ImmutableList<? extends Type> parameters);

  public IntType int_();

  public NothingType nothing();

  public StringType string();

  public StructType struct(String name, NamedList<? extends Type> fields);

  public Variable variable(String name);
}
