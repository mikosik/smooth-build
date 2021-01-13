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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.install.FullPathResolver;
import org.smoothbuild.lang.base.Defined;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.base.type.Type;

@Singleton
public class NativeImplLoader {
  private final Map<Path, Map<String, Native>> nativesMap = new HashMap<>();
  private final FullPathResolver pathResolver;

  @Inject
  public NativeImplLoader(FullPathResolver pathResolver) {
    this.pathResolver = pathResolver;
  }

  public synchronized Native loadNative(Function function) throws LoadingNativeImplException {
    Native nativ = loadNativeImpl(function);
    nativeResultMatchesDeclared(function, nativ, function.type().resultType());
    nativeParameterTypesMatchesFuncParameters(nativ, function);
    return nativ;
  }

  public synchronized Native loadNative(Value value) throws LoadingNativeImplException {
    Native nativ = loadNativeImpl(value);
    nativeResultMatchesDeclared(value, nativ, value.type());
    nativeHasOneParameter(nativ, value);
    return nativ;
  }

  private Native loadNativeImpl(Defined defined) throws LoadingNativeImplException {
    Path path = pathResolver.resolve(defined.location().module().toNative());
    Map<String, Native> natives = nativesInJar(path, defined.name());
    Native nativ = natives.get(defined.name());
    if (nativ == null) {
      throw newException(defined, "Error loading native implementation for `"
          + defined.name() + "`. Jar '" + path + "' does not contain implementation for `"
          + defined.name() + "`. It contains {" + join(",", natives.keySet()) + "}.");
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

  private void nativeResultMatchesDeclared(Defined defined, Native nativ, Type resultType)
      throws LoadingNativeImplException {
    Method method = nativ.method();
    Class<?> resultJType = method.getReturnType();
    if (!mapTypeToJType(resultType).equals(resultJType)) {
      throw newException(defined, defined.q() + " declares type " + resultType.q()
          + " so its native implementation result type must be "
          + mapTypeToJType(resultType).getCanonicalName() + " but it is "
          + resultJType.getCanonicalName() + ".");
    }
  }

  private void nativeParameterTypesMatchesFuncParameters(Native nativ, Function function)
      throws LoadingNativeImplException {
    Parameter[] nativeParams = nativ.method().getParameters();
    List<Item> params = function.parameters();
    if (params.size() != nativeParams.length - 1) {
      throw newException(function, "Function " + function.q() + " has "
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
        throw newException(function, "Function " + function.q() + " parameter `"
            + declaredName + "` has type " + paramType.q()
            + " so its native implementation type must be " + expectedParamJType.getCanonicalName()
            + " but it is " + paramJType.getCanonicalName() + ".");
      }
    }
  }

  private void nativeHasOneParameter(Native nativ, Value value) throws
      LoadingNativeImplException {
    int paramCount = nativ.method().getParameters().length;
    if (paramCount != 1) {
      throw newException(value, value.q()
          + " has native implementation that has too many parameter(s) = " + paramCount);
    }
  }

  private static LoadingNativeImplException newException(Defined defined, String message) {
    return new LoadingNativeImplException(defined.location().toString() + ": " + message);
  }
}
