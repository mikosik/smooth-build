package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.smoothbuild.lang.type.Types.jTypeToType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.nativ.err.DuplicatedAnnotationException;
import org.smoothbuild.lang.function.nativ.err.IllegalParameterNameException;
import org.smoothbuild.lang.function.nativ.err.IllegalParameterTypeException;
import org.smoothbuild.lang.function.nativ.err.MissingNameAnnotationException;
import org.smoothbuild.lang.function.nativ.err.NativeFunctionImplementationException;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.TypeLiteral;

public class NativeParameterFactory {

  public static Parameter createParameter(Method method, java.lang.reflect.Type reflectType,
      Annotation[] annotations) throws NativeFunctionImplementationException {
    Multimap<Class<?>, Annotation> annotationMap = annotationsMap(annotations);
    Type type = type(method, reflectType);
    boolean isRequired = isRequired(method, annotationMap);
    String name = name(method, annotationMap);
    return parameter(type, name, isRequired);
  }

  private static Type type(Method method, java.lang.reflect.Type reflectType)
      throws IllegalParameterTypeException {
    Type type = jTypeToType(TypeLiteral.get(reflectType));
    if (type == null || !type.isAllowedAsParameter()) {
      throw new IllegalParameterTypeException(method, reflectType);
    }
    return type;
  }

  private static String name(Method method, Multimap<Class<?>, Annotation> annotations)
      throws NativeFunctionImplementationException {
    Collection<Annotation> names = annotations.get(org.smoothbuild.lang.plugin.Name.class);
    switch (names.size()) {
      case 0:
        throw new MissingNameAnnotationException(method);
      case 1:
        String name = ((org.smoothbuild.lang.plugin.Name) names.iterator().next()).value();
        if (Name.isLegalName(name)) {
          return name;
        } else {
          throw new IllegalParameterNameException(method, name);
        }
      default:
        throw new DuplicatedAnnotationException(method, org.smoothbuild.lang.plugin.Name.class);
    }
  }

  private static boolean isRequired(Method method, Multimap<Class<?>, Annotation> annotations)
      throws DuplicatedAnnotationException {
    switch (annotations.get(Required.class).size()) {
      case 0:
        return false;
      case 1:
        return true;
      default:
        throw new DuplicatedAnnotationException(method, Required.class);
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
