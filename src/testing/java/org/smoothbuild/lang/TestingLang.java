package org.smoothbuild.lang;

import static org.smoothbuild.testing.common.TestingLocation.loc;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Types;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.FieldReadExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;
import org.smoothbuild.lang.expr.ValueReferenceExpression;

import com.google.common.collect.ImmutableList;

import okio.ByteString;

public class TestingLang {
  public static BlobLiteralExpression blob(int line, int data) {
    return new BlobLiteralExpression(ByteString.of((byte) data), loc(line));
  }

  public static StringLiteralExpression string(int line, String data) {
    return new StringLiteralExpression(data, loc(line));
  }

  public static ArrayLiteralExpression array(int line, Type elemType, Expression... expressions) {
    return new ArrayLiteralExpression(
        Types.array(elemType), ImmutableList.copyOf(expressions), loc(line));
  }

  public static ValueReferenceExpression valueRef(int line, String name) {
    return new ValueReferenceExpression(name, loc(line));
  }

  public static ParameterReferenceExpression parameterRef(String name, int line) {
    return new ParameterReferenceExpression(name, loc(line));
  }

  public static FieldReadExpression fieldRead(int line, Field field, Expression expression) {
    return new FieldReadExpression(0, field, expression, loc(line));
  }

  public static CallExpression call(int line, Callable callable, Expression... arguments) {
    return new CallExpression(callable, ImmutableList.copyOf(arguments), loc(line));
  }

  public static Function function(int line, Type type, String name, Parameter... parameters) {
    return function(line, type, name, parameters, Optional.empty());
  }

  public static Function function(int line, Type type, String name, Expression body,
      Parameter... parameters) {
    return function(line, type, name, parameters, Optional.of(body));
  }

  private static Function function(int line, Type type, String name, Parameter[] parameters,
      Optional<Expression> body) {
    Signature signature = new Signature(type, name, ImmutableList.copyOf(parameters));
    return new Function(signature, body, loc(line));
  }

  public static Value value(int line, Type type, String name) {
    return value(line, type, name, Optional.empty());
  }

  public static Value value(int line, Type type, String name, Expression expression) {
    return value(line, type, name, Optional.of(expression));
  }

  private static Value value(int line, Type type, String name,
      Optional<Expression> expression) {
    return new Value(type, name, expression, loc(line));
  }

  public static StructType struct(int line, String name, Field field) {
    return Types.struct(name, loc(line), List.of(field));
  }

  public static Constructor constr(int line, Signature signature) {
    return new Constructor(signature, loc(line));
  }

  public static Signature signature(StructType myStruct, String name, Parameter param) {
    return new Signature(myStruct, name, ImmutableList.of(param));
  }

  public static Parameter parameter(int line, Type type, String name) {
    return parameter(line, type, name, Optional.empty());
  }

  public static Parameter parameter(int line, Type type, String name,
      Expression defaultValue) {
    return parameter(line, type, name, Optional.of(defaultValue));
  }

  private static Parameter parameter(int line, Type type, String name,
      Optional<Expression> defaultValue) {
    return new Parameter(type, name, defaultValue, loc(line));
  }

  public static Field field(int line, Type type, String name) {
    return new Field(type, name, loc(line));
  }
}
