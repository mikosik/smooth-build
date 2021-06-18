package org.smoothbuild.lang;

import static java.lang.Math.max;
import static org.smoothbuild.io.fs.base.TestingFilePath.nativeFilePath;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;

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
import org.smoothbuild.lang.expr.NativeExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;

import com.google.common.collect.ImmutableList;

import okio.ByteString;

public class TestingLang {
  public static BlobLiteralExpression blob(int data) {
    return blob(1, data);
  }

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

  public static ParameterReferenceExpression parameterRef(Type type, String name) {
    return parameterRef(1, type, name);
  }

  public static ParameterReferenceExpression parameterRef(int line, Type type, String name) {
    return new ParameterReferenceExpression(type, name, loc(line));
  }

  public static FieldReadExpression fieldRead(
      int line, ItemSignature field, Expression expression) {
    return new FieldReadExpression(field, expression, loc(line));
  }

  public static CallExpression call(Type type, Callable callable, Expression... arguments) {
    return call(1, type, callable, arguments);
  }

  public static CallExpression call(
      int line, Type type, Callable callable, Expression... arguments) {
    return new CallExpression(type, callable, ImmutableList.copyOf(arguments), loc(line));
  }

  public static Function function(Type type, String name, Item... parameters) {
    return function(1, type, name, "Impl.met", parameters);
  }

  public static Function function(int line, Type type, String name, String implementedBy,
      Item... parameters) {
    NativeExpression nativeExpression = new NativeExpression(
        implementedBy, true, loc(max(line - 1, 1)), nativeFilePath());
    return function(line, type, name, nativeExpression, parameters);
  }

  public static Function function(Type type, String name, Expression body, Item... parameters) {
    return function(1, type, name, body, parameters);
  }

  public static Function function(int line, Type type, String name, Expression body,
      Item... parameters) {
    return new Function(type, name, ImmutableList.copyOf(parameters), body, loc(line));
  }

  public static Value value(int line, Type type, String name, String implementedBy) {
    NativeExpression nativ = new NativeExpression(
        implementedBy, true, loc(line -1), nativeFilePath());
    return new Value(type, name, nativ, loc(line));
  }

  public static Value value(int line, Type type, String name, Expression expression) {
    return new Value(type, name, expression, loc(line));
  }

  public static StructType struct(String name, ItemSignature field) {
    return Types.struct(name, List.of(field));
  }

  public static Constructor constr(int line, Type resultType, String name, Item... parameters) {
    return new Constructor(resultType, name, ImmutableList.copyOf(parameters), loc(line));
  }

  public static Item parameter(Type type, String name) {
    return parameter(type, name, Optional.empty());
  }

  public static Item parameter(Type type, String name, Expression defaultValue) {
    return parameter(type, name, Optional.of(defaultValue));
  }

  private static Item parameter(Type type, String name,
      Optional<Expression> defaultValue) {
    return new Item(type, name, defaultValue);
  }

  public static Item field(Type type, String name) {
    return new Item(type, name, Optional.empty());
  }
}
