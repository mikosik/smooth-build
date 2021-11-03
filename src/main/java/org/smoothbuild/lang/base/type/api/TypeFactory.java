package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public interface TypeFactory {
  /**
   * Inferable base types are types that can be inferred but `Any` type is not legal in smooth
   * language.
   */
  public ImmutableSet<BaseType> inferableBaseTypes();

  /**
   * Base types that are legal in smooth language.
   */
  public ImmutableSet<BaseType> baseTypes();

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
