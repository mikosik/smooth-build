package org.smoothbuild.lang.function.nativ;

import static java.lang.reflect.Proxy.newProxyInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.smoothbuild.lang.base.SValue;

import com.google.common.collect.ImmutableMap;

public class ArgsCreator {
  private final Class<?> paramsInterface;

  public ArgsCreator(Class<?> paramsInterface) {
    this.paramsInterface = paramsInterface;
  }

  public Object create(Map<String, SValue> arguments) {
    ClassLoader classLoader = this.getClass().getClassLoader();
    Class<?>[] args = new Class<?>[] { paramsInterface };
    InvocationHandler invocationHandler = new MapInvocationHandler(arguments);

    return newProxyInstance(classLoader, args, invocationHandler);
  }

  private final class MapInvocationHandler implements InvocationHandler {
    private final ImmutableMap<String, SValue> arguments;

    private MapInvocationHandler(Map<String, SValue> arguments) {
      this.arguments = ImmutableMap.copyOf(arguments);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return arguments.get(method.getName());
    }
  }
}
