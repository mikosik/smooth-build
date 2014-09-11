package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;

import java.lang.reflect.Method;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.builtin.convert.FileArrayToBlobArrayFunction;
import org.smoothbuild.lang.builtin.convert.FileToBlobFunction;
import org.smoothbuild.lang.builtin.convert.NilToBlobArrayFunction;
import org.smoothbuild.lang.builtin.convert.NilToFileArrayFunction;
import org.smoothbuild.lang.builtin.convert.NilToStringArrayFunction;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.function.nativ.NativeFunctionFactory;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.HashCode;

public class Convert {
  /**
   * Name of the only parameter in every conversion function.
   */
  private static final String PARAM_NAME = "input";

  private static final ImmutableMap<SType<?>, ImmutableMap<SType<?>, Function<?>>> FUNCTIONS = createFunctions();

  public static <T extends SValue> ImmutableList<Expr<T>> convertExprs(SType<T> type,
      Iterable<? extends Expr<?>> expressions) {
    ImmutableList.Builder<Expr<T>> builder = ImmutableList.builder();
    for (Expr<?> expr : expressions) {
      builder.add(convertExpr(type, expr));
    }
    return builder.build();
  }

  public static ImmutableSet<SType<?>> superTypesOf(SType<?> type) {
    return FUNCTIONS.get(type).keySet();
  }

  public static <T extends SValue> Expr<T> convertExpr(SType<T> destinationType, Expr<?> source) {
    SType<?> sourceType = source.type();
    if (sourceType == destinationType) {
      /*
       * Cast is safe as 'if' above checked that source has proper type.
       */
      @SuppressWarnings("unchecked")
      Expr<T> expr = (Expr<T>) source;
      return expr;
    }

    ImmutableMap<SType<?>, Function<?>> availableFunctions = FUNCTIONS.get(sourceType);

    /*
     * Cast is safe as FUNCTIONS map is immutable and constructed properly.
     */
    @SuppressWarnings("unchecked")
    Function<T> function = (Function<T>) availableFunctions.get(destinationType);

    return new CallExpr<>(function, source.codeLocation(), ImmutableMap.of(PARAM_NAME, source));
  }

  private static ImmutableMap<SType<?>, ImmutableMap<SType<?>, Function<?>>> createFunctions() {
    Builder<SType<?>, ImmutableMap<SType<?>, Function<?>>> builder = ImmutableMap.builder();

    builder.put(STRING, Empty.typeFunctionMap());
    builder.put(BLOB, Empty.typeFunctionMap());
    builder.put(FILE, functionsMap(FileToBlobFunction.class));
    builder.put(NOTHING, Empty.typeFunctionMap());

    builder.put(STRING_ARRAY, Empty.typeFunctionMap());
    builder.put(BLOB_ARRAY, Empty.typeFunctionMap());
    builder.put(FILE_ARRAY, functionsMap(FileArrayToBlobArrayFunction.class));

    builder.put(NIL, functionsMap(NilToStringArrayFunction.class, NilToBlobArrayFunction.class,
        NilToFileArrayFunction.class));

    return builder.build();
  }

  private static ImmutableMap<SType<?>, Function<?>> functionsMap(Class<?>... functionClasses) {
    Builder<SType<?>, Function<?>> builder = ImmutableMap.builder();
    for (Class<?> functionClass : functionClasses) {
      NativeFunction<?> function = function(functionClass.getMethods()[0]);
      builder.put(function.signature().type(), function);
    }
    return builder.build();
  }

  private static NativeFunction<?> function(Method functionMethod) {
    try {
      // TODO This artificial hash won't be needed once convert functions become
      // part of funcs.jar
      HashCode jarHash = Hash.integer(123);
      return NativeFunctionFactory.createNativeFunction(jarHash, functionMethod);
    } catch (NativeImplementationException e) {
      throw new RuntimeException(e);
    }
  }
}
