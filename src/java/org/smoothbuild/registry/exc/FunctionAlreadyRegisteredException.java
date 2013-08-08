package org.smoothbuild.registry.exc;

@SuppressWarnings("serial")
public class FunctionAlreadyRegisteredException extends Exception {

  public FunctionAlreadyRegisteredException(String name) {
    super("Function '" + name + "' has been already registered.");
  }
}
