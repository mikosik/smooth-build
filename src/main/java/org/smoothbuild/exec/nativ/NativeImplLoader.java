package org.smoothbuild.exec.nativ;

import static java.lang.String.join;
import static org.smoothbuild.exec.nativ.FindNatives.findNatives;
import static org.smoothbuild.exec.nativ.MapTypeToJType.mapTypeToJType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.lang.base.Evaluable;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.NativeValue;
import org.smoothbuild.lang.base.type.Type;

@Singleton
public class NativeImplLoader {
  private final Map<Path, Map<String, Native>> nativesMap = new HashMap<>();

  public synchronized Native loadNative(NativeFunction function) throws LoadingNativeImplException {
    Native nativ = loadNativeImpl(function);
    nativeResultMatchesDeclared(function, nativ);
    nativeParameterTypesMatchesFuncParameters(nativ, function);
    return nativ;
  }

  public synchronized Native loadNative(NativeValue value) throws LoadingNativeImplException {
    Native nativ = loadNativeImpl(value);
    nativeResultMatchesDeclared(value, nativ);
    nativeHasOneParameter(nativ, value);
    return nativ;
  }

  private Native loadNativeImpl(Evaluable evaluable) throws LoadingNativeImplException {
    Path path = evaluable.location().modulePath().nativ().path();
    Map<String, Native> natives = nativesInJar(path, evaluable.name());
    Native nativ = natives.get(evaluable.name());
    if (nativ == null) {
      throw newException(evaluable, "Error loading native implementation for `"
          + evaluable.name() + "`. Jar '" + path + "' does not contain implementation for `"
          + evaluable.name() + "`. It contains {" + join(",", natives.keySet()) + "}.");
    }
    return nativ;
  }

  private Map<String, Native> nativesInJar(Path path, String name)
      throws LoadingNativeImplException {
    try {
      return nativesInJar(path);
    } catch (LoadingNativeJarException e) {
      throw new LoadingNativeImplException(
          "Error loading native implementation for `" + name + "`. " + e.getMessage(), e);
    }
  }

  private Map<String, Native> nativesInJar(Path path) throws LoadingNativeJarException {
    Map<String, Native> nativesInJar = nativesMap.get(path);
    if (nativesInJar == null) {
      nativesInJar = findNatives(path);
      nativesMap.put(path, nativesInJar);
    }
    return nativesInJar;
  }

  private void nativeResultMatchesDeclared(Evaluable evaluable, Native nativ)
      throws LoadingNativeImplException {
    Method method = nativ.method();
    Type resultType = evaluable.type();
    Class<?> resultJType = method.getReturnType();
    if (!mapTypeToJType(resultType).equals(resultJType)) {
      throw newException(evaluable, "'" + evaluable.name() + "' declares type " + resultType.q()
          + " so its native implementation result type must be "
          + mapTypeToJType(resultType).getCanonicalName() + " but it is "
          + resultJType.getCanonicalName() + ".");
    }
  }

  private void nativeParameterTypesMatchesFuncParameters(Native nativ, NativeFunction function)
      throws LoadingNativeImplException {
    Parameter[] nativeParams = nativ.method().getParameters();
    List<org.smoothbuild.lang.base.Parameter> params =
        function.parameters();
    if (params.size() != nativeParams.length - 1) {
      throw newException(function, "Function '" + function.name() + "' has "
          + params.size() + " parameter(s) but its native implementation has "
          + (nativeParams.length - 1) + " parameter(s).");
    }
    for (int i = 0; i < params.size(); i++) {
      String declaredName = params.get(i).name();
      Parameter nativeParam = nativeParams[i + 1];
      Type paramType = params.get(i).type();
      Class<?> paramJType = nativeParam.getType();
      Class<? extends Obj> expectedParamJType = mapTypeToJType(paramType);
      if (!expectedParamJType.equals(paramJType)) {
        throw newException(function, "Function '" + function.name()
            + "' parameter '" + declaredName + "' has type "
            + paramType.name() + " so its native implementation type must be "
            + expectedParamJType.getCanonicalName() + " but it is "
            + paramJType.getCanonicalName() + ".");
      }
    }
  }

  private void nativeHasOneParameter(Native nativ, NativeValue value) throws
      LoadingNativeImplException {
    int paramCount = nativ.method().getParameters().length;
    if (paramCount != 1) {
      throw newException(value, "'" + value.name()
          + "' has native implementation that has too many parameter(s) = " + paramCount);
    }
  }

  private static LoadingNativeImplException newException(Evaluable evaluable, String message) {
    return new LoadingNativeImplException(evaluable.location().toString() + ": " + message);
  }
}
