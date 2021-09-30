package org.smoothbuild.lang;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.stream;
import static org.smoothbuild.lang.base.define.TestingLocation.loc;
import static org.smoothbuild.lang.base.define.TestingModulePath.modulePath;
import static org.smoothbuild.lang.base.type.Types.blobT;
import static org.smoothbuild.lang.base.type.Types.intT;
import static org.smoothbuild.lang.base.type.Types.stringT;
import static org.smoothbuild.lang.base.type.Types.structT;
import static org.smoothbuild.util.Lists.list;

import java.math.BigInteger;
import java.util.Optional;

import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.DefinedFunction;
import org.smoothbuild.lang.base.define.DefinedValue;
import org.smoothbuild.lang.base.define.GlobalReferencable;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.NativeFunction;
import org.smoothbuild.lang.base.define.NativeValue;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Types;
import org.smoothbuild.lang.expr.AnnotationExpression;
import org.smoothbuild.lang.expr.ArrayLiteralExpression;
import org.smoothbuild.lang.expr.BlobLiteralExpression;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.IntLiteralExpression;
import org.smoothbuild.lang.expr.ParameterReferenceExpression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.lang.expr.SelectExpression;
import org.smoothbuild.lang.expr.StringLiteralExpression;

import com.google.common.collect.ImmutableList;

import okio.ByteString;

public class TestingLang {
  public static BlobLiteralExpression blob(int data) {
    return blob(1, data);
  }

  public static BlobLiteralExpression blob(int line, int data) {
    return new BlobLiteralExpression(blobT(), ByteString.of((byte) data), loc(line));
  }

  public static IntLiteralExpression int_(int value) {
    return int_(1, value);
  }

  public static IntLiteralExpression int_(int line, int value) {
    return new IntLiteralExpression(intT(), BigInteger.valueOf(value), loc(line));
  }

  public static StringLiteralExpression string(int line, String data) {
    return new StringLiteralExpression(stringT(), data, loc(line));
  }

  public static ArrayLiteralExpression arrayE(int line, Type elemType, Expression... expressions) {
    return new ArrayLiteralExpression(
        Types.arrayT(elemType), ImmutableList.copyOf(expressions), loc(line));
  }

  public static ReferenceExpression reference(GlobalReferencable referencable) {
    return reference(1, referencable.type(), referencable.name());
  }

  public static ReferenceExpression reference(int line, Type type, String name) {
    return new ReferenceExpression(type, name, loc(line));
  }

  public static ParameterReferenceExpression parameterRef(Type type, String name) {
    return parameterRef(1, type, name);
  }

  public static ParameterReferenceExpression parameterRef(int line, Type type, String name) {
    return new ParameterReferenceExpression(type, name, loc(line));
  }

  public static SelectExpression select(
      int line, ItemSignature field, Expression expression) {
    return new SelectExpression(field, expression, loc(line));
  }

  public static CallExpression call(Type type, Expression expression, Expression... arguments) {
    return call(1, type, expression, arguments);
  }

  public static CallExpression call(
      int line, Type type, Expression expression, Expression... arguments) {
    Location loc = loc(line);
    var args = stream(arguments).map(Optional::of).collect(toImmutableList());
    return new CallExpression(type, expression, args, loc);
  }

  public static NativeFunction function(Type type, String name, Item... parameters) {
    return function(1, type, name, nativ(1, string(1, "Impl.met")), parameters);
  }

  public static NativeFunction function(int line, Type type, String name,
      AnnotationExpression nativ, Item... parameters) {
    return new NativeFunction(type, modulePath(), name, list(parameters), nativ, loc(line));
  }

  public static DefinedFunction function(Type type, String name, Expression body,
      Item... parameters) {
    return function(1, type, name, body, parameters);
  }

  public static DefinedFunction function(int line, Type type, String name, Expression body,
      Item... parameters) {
    return new DefinedFunction(type, modulePath(), name, list(parameters), body, loc(line));
  }

  public static DefinedValue value(int line, Type type, String name, Expression expression) {
    return new DefinedValue(type, modulePath(), name, expression, loc(line));
  }

  public static NativeValue value(int line, Type type, String name, AnnotationExpression nativ) {
    return new NativeValue(type, modulePath(), name, nativ, loc(line));
  }

  public static AnnotationExpression nativ(int line, StringLiteralExpression implementedBy) {
    return nativ(line, implementedBy, true);
  }

  public static AnnotationExpression nativ(
      int line, StringLiteralExpression implementedBy, boolean pure) {
    StructType type = structT("Native", list(
        new ItemSignature(stringT(), "path", Optional.empty()),
        new ItemSignature(blobT(), "content", Optional.empty())));
    return new AnnotationExpression(type, implementedBy, pure, loc(line));
  }

  public static StructType struct(String name, ItemSignature... fields) {
    return Types.structT(name, list(fields));
  }

  public static Constructor constr(int line, Type resultType, String name, Item... parameters) {
    return new Constructor(
        resultType, modulePath(), name, ImmutableList.copyOf(parameters), loc(line));
  }

  public static Item parameter(Type type, String name) {
    return parameter(1, type, name);
  }

  public static Item parameter(int line, Type type, String name) {
    return parameter(line, type, name, Optional.empty());
  }

  public static Item parameter(int line, Type type, String name, Expression defaultArg) {
    return parameter(line, type, name, Optional.of(defaultArg));
  }

  private static Item parameter(int line, Type type, String name,
      Optional<Expression> defaultArg) {
    return new Item(type, modulePath(), name, defaultArg, loc(line));
  }

  public static Item field(Type type, String name) {
    return new Item(type, modulePath(), name, Optional.empty(), loc(1));
  }
}
