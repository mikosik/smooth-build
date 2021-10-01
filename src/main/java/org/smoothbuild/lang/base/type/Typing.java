package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.ItemSignature.itemSignature;
import static org.smoothbuild.util.Lists.map;

import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;
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

  public Type strip(Type type) {
    // TODO in java 17 use pattern matching switch
    if (type instanceof ArrayType arrayType) {
      return stripArrayType(arrayType);
    } else if (type instanceof FunctionType functionType) {
      return stripFunctionType(functionType);
    } else {
      return type;
    }
  }

  private Type stripArrayType(ArrayType arrayType) {
    Type elemType = arrayType.elemType();
    Type newElemType = strip(elemType);
    if (elemType == newElemType) {
      return arrayType;
    } else {
      return newArrayType(newElemType);
    }
  }

  private Type stripFunctionType(FunctionType functionType) {
    var oldResultType = functionType.resultType();
    var newResultType = strip(oldResultType);
    var oldParameters = functionType.parameters();
    var newParameters = map(oldParameters, i -> itemSignature(strip(i.type())));
    if (oldResultType == newResultType && oldParameters.equals(newParameters)) {
      return functionType;
    }
    return newFunctionType(newResultType, newParameters);
  }

  private static ArrayType newArrayType(Type elemType) {
    return Types.arrayT(elemType);
  }

  private static FunctionType newFunctionType(
      Type result, ImmutableList<ItemSignature> parameters) {
    return Types.functionT(result, parameters);
  }
}
