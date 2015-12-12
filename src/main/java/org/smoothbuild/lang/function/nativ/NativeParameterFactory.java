package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.smoothbuild.lang.type.Types.jTypeToType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.TypeLiteral;

public class NativeParameterFactory {

  public static Parameter createParameter(Method method, java.lang.reflect.Parameter jParameter,
      Annotation[] annotations) throws NativeFunctionImplementationException {
    Multimap<Class<?>, Annotation> annotationMap = annotationsMap(annotations);
    Type type = type(method, jParameter.getParameterizedType());
    String name = jParameter.getName();
    boolean isRequired = isRequired(method, annotationMap);
    return parameter(type, name, isRequired);
  }

  private static Type type(Method method, java.lang.reflect.Type reflectType)
      throws NativeFunctionImplementationException {
    Type type = jTypeToType(TypeLiteral.get(reflectType));
    if (type == null) {
      throw new NativeFunctionImplementationException(method,
          "It has parameter with illegal java type '" + reflectType.getTypeName() + "'.");
    }
    return type;
  }

  private static boolean isRequired(Method method, Multimap<Class<?>, Annotation> annotations)
      throws NativeFunctionImplementationException {
    switch (annotations.get(Required.class).size()) {
      case 0:
        return false;
      case 1:
        return true;
      default:
        throw new NativeFunctionImplementationException(method,
            "One of its parameters is annotated twice as " + Required.class.getName());
    }
  }

  private static Multimap<Class<?>, Annotation> annotationsMap(Annotation[] annotations) {
    ImmutableMultimap.Builder<Class<?>, Annotation> builder = ImmutableMultimap.builder();
    for (Annotation annotation : annotations) {
      builder.put(annotation.annotationType(), annotation);
    }
    return builder.build();
  }
}
