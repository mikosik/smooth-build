package org.smoothbuild.parse;

import java.util.ArrayDeque;
import java.util.Deque;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.parse.err.CycleInCallGraphError;
import org.smoothbuild.util.LineBuilder;

public class DependencyStack {
  private final Deque<DependencyStackElem> stack = new ArrayDeque<>();

  public boolean isEmpty() {
    return stack.isEmpty();
  }

  public void push(DependencyStackElem element) {
    stack.addLast(element);
  }

  public DependencyStackElem pop() {
    return stack.removeLast();
  }

  public DependencyStackElem peek() {
    return stack.getLast();
  }

  public CycleInCallGraphError createCycleError() {
    Name lastMissing = peek().missing().functionName();
    int first = -1;
    DependencyStackElem[] array = stack.toArray(new DependencyStackElem[stack.size()]);
    for (int i = array.length - 1; 0 <= i; i--) {
      if (array[i].name().equals(lastMissing)) {
        first = i;
        break;
      }
    }
    if (first == -1) {
      throw new IllegalStateException("Couldn't find expected cycle in call graph.");
    }
    LineBuilder builder = new LineBuilder();
    for (int i = first; i < array.length; i++) {
      DependencyStackElem current = array[i];
      Dependency missing = current.missing();
      builder.addLine(current.name().value() + missing.location() + " -> "
          + missing.functionName().value());
    }

    CodeLocation location = array[first].missing().location();
    String message = builder.build();

    return new CycleInCallGraphError(location, message);
  }
}
