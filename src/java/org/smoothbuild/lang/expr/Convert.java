package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;

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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

public class Convert {
  /**
   * Name of the only parameter in every conversion function.
   */
  private static final String PARAM_NAME = "input";

  private static final ImmutableMap<SType<?>, ImmutableMap<SType<?>, Function<?>>> FUNCTIONS =
      createFunctions();

  public static boolean isAssignable(SType<?> from, SType<?> to) {
    if (from == to) {
      return true;
    }
    return FUNCTIONS.get(from).containsKey(to);
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

    return new CallExpr<T>(function, source.codeLocation(), ImmutableMap.of(PARAM_NAME, source));
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
      NativeFunction<?> function = function(functionClass);
      builder.put(function.signature().type(), function);
    }
    return builder.build();
  }

  private static NativeFunction<?> function(Class<?> functionClass) {
    try {
      return NativeFunctionFactory.create(functionClass, true);
    } catch (NativeImplementationException e) {
      throw new RuntimeException(e);
    }
  }
}
