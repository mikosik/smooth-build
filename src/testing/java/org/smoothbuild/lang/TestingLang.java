package org.smoothbuild.lang;

import static org.smoothbuild.testing.common.TestingLocation.loc;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Callable;
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.Function;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Types;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.FieldReadExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;

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

  public static ReferenceExpression reference(int line, Type type, String name) {
    return new ReferenceExpression(name, type, loc(line));
  }

  public static ParameterReferenceExpression parameterRef(Type type, String name, int line) {
    return new ParameterReferenceExpression(type, name, loc(line));
  }

  public static FieldReadExpression fieldRead(
      int line, ItemSignature field, Expression expression) {
    return new FieldReadExpression(field, expression, loc(line));
  }

  public static CallExpression call(
      int line, Type type, Callable callable, Expression... arguments) {
    return new CallExpression(type, callable, ImmutableList.copyOf(arguments), loc(line));
  }

  public static Function function(int line, Type type, String name, Item... parameters) {
    return function(line, type, name, parameters, Optional.empty());
  }

  public static Function function(int line, Type type, String name, Expression body,
      Item... parameters) {
    return function(line, type, name, parameters, Optional.of(body));
  }

  private static Function function(int line, Type type, String name, Item[] parameters,
      Optional<Expression> body) {
    return new Function(type, name, ImmutableList.copyOf(parameters), body, loc(line));
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

  public static StructType struct(String name, ItemSignature field) {
    return Types.struct(name, List.of(field));
  }

  public static Constructor constr(int line, Type resultType, String name, Item... parameters) {
    return new Constructor(resultType, name, ImmutableList.copyOf(parameters), loc(line));
  }

  public static Item parameter(int line, Type type, String name) {
    return parameter(line, type, name, Optional.empty());
  }

  public static Item parameter(int line, Type type, String name,
      Expression defaultValue) {
    return parameter(line, type, name, Optional.of(defaultValue));
  }

  private static Item parameter(int line, Type type, String name,
      Optional<Expression> defaultValue) {
    return new Item(type, name, defaultValue);
  }

  public static Item field(int line, Type type, String name) {
    return new Item(type, name, Optional.empty());
  }
}
