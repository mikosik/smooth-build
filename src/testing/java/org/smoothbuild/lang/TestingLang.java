package org.smoothbuild.lang;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.stream;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.define.TestingModulePath.modulePath;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.RealFunction;
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

  public static CallExpression call(
      int line, Type type, Expression expression, Expression... arguments) {
    Location loc = loc(line);
    var args = stream(arguments).map(Optional::of).collect(toImmutableList());
    return new CallExpression(type, expression, args, loc);
  }

  public static RealFunction function(Type type, String name, Item... parameters) {
    return function(1, type, name, nativ(1, "Impl.met"), parameters);
  }

  public static RealFunction function(int line, Type type, String name, NativeExpression nativ,
      Item... parameters) {
    return function(line, type, name, (Expression) nativ, parameters);
  }

  public static RealFunction function(Type type, String name, Expression body, Item... parameters) {
    return function(1, type, name, body, parameters);
  }

  public static RealFunction function(int line, Type type, String name, Expression body,
      Item... parameters) {
    return new RealFunction(
        type, modulePath(), name, ImmutableList.copyOf(parameters), body, loc(line));
  }

  public static Value value(int line, Type type, String name, NativeExpression nativ) {
    return new Value(type, modulePath(), name, nativ, loc(line));
  }

  public static NativeExpression nativ(int line, String implementedBy) {
    return nativ(line, implementedBy, true);
  }

  public static NativeExpression nativ(int line, String implementedBy, boolean pure) {
    return new NativeExpression(implementedBy, pure, loc(line));
  }

  public static Value value(int line, Type type, String name, Expression expression) {
    return new Value(type, modulePath(), name, expression, loc(line));
  }

  public static StructType struct(String name, ItemSignature field) {
    return Types.struct(name, list(field));
  }

  public static Constructor constr(int line, Type resultType, String name, Item... parameters) {
    return new Constructor(
        resultType, modulePath(), name, ImmutableList.copyOf(parameters), loc(line));
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
