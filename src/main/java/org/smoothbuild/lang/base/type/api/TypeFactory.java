package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

public interface TypeFactory<T extends Type> {
  public Bounds unbounded();

  public Bounds oneSideBound(Side side, T type);

  public Side upper();

  public Side lower();

  public AnyType any();

  public ArrayType array(T elemType);

  public BlobType blob();

  public BoolType bool();

  public FunctionType function(T result, ImmutableList<? extends T> parameters);

  public IntType int_();

  public NothingType nothing();

  public StringType string();

  public StructType struct(String name, NamedList<? extends T> fields);

  public Variable variable(String name);
}
