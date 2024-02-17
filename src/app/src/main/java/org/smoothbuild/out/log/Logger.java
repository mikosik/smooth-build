package org.smoothbuild.out.log;

import static com.google.common.collect.Iterators.unmodifiableIterator;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import org.smoothbuild.common.collect.List;

public class Logger extends AbstractCollection<Log> {
  private final ArrayList<Log> list = new ArrayList<>();

  @Override
  public Iterator<Log> iterator() {
    return unmodifiableIterator(list.iterator());
  }

  @Override
  public int size() {
    return list.size();
  }

  public void logAll(Iterable<? extends Log> logs) {
    logs.forEach(this::log);
  }

  public void fatal(String message) {
    log(new Log(FATAL, message));
  }

  public void error(String message) {
    log(new Log(ERROR, message));
  }

  public void warning(String message) {
    log(new Log(WARNING, message));
  }

  public void info(String message) {
    log(new Log(INFO, message));
  }

  public void log(Log log) {
    list.add(log);
  }

  public boolean containsFailure() {
    return Log.containsAnyFailure(this);
  }

  public List<Log> toList() {
    return listOfAll(list);
  }

  @Override
  public String toString() {
    return toList().toString(",");
  }
}
