package org.smoothbuild.function.nativ;

import static java.lang.reflect.Proxy.newProxyInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.smoothbuild.plugin.Value;

import com.google.common.collect.ImmutableMap;

public class ArgumentsCreator {
  private final Class<?> paramsInterface;

  public ArgumentsCreator(Class<?> paramsInterface) {
    this.paramsInterface = paramsInterface;
  }

  public Object create(Map<String, Value> arguments) {
    ClassLoader classLoader = this.getClass().getClassLoader();
    Class<?>[] args = new Class<?>[] { paramsInterface };
    InvocationHandler invocationHandler = new MapInvocationHandler(arguments);

    return newProxyInstance(classLoader, args, invocationHandler);
  }

  private final class MapInvocationHandler implements InvocationHandler {
    private final ImmutableMap<String, Value> arguments;

    private MapInvocationHandler(Map<String, Value> arguments) {
      this.arguments = ImmutableMap.copyOf(arguments);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      return arguments.get(method.getName());
    }
  }
}
