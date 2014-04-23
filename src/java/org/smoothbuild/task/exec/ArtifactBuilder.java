package org.smoothbuild.task.exec;

import static org.smoothbuild.message.base.CodeLocation.codeLocation;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.def.CallNode;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.task.exec.save.ArtifactSaver;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class ArtifactBuilder {
  private final ArtifactSaver artifactSaver;
  private final TaskGraph taskGraph;
  private final Map<Name, Task<?>> artifacts;

  @Inject
  public ArtifactBuilder(ArtifactSaver artifactSaver, TaskGraph taskGraph) {
    this.artifactSaver = artifactSaver;
    this.taskGraph = taskGraph;
    this.artifacts = Maps.newHashMap();
  }

  public void addArtifact(Function<?> function) {
    ImmutableMap<String, Node<?>> empty = ImmutableMap.<String, Node<?>> of();
    CallNode<?> node = new CallNode<>(function, codeLocation(1), empty);
    artifacts.put(function.name(), taskGraph.createTasks(node));
  }

  public void runBuild() {
    try {
      taskGraph.executeAll();
    } catch (BuildInterruptedException e) {
      return;
    }
    for (Entry<Name, Task<?>> artifact : artifacts.entrySet()) {
      Name name = artifact.getKey();
      Task<?> task = artifact.getValue();
      SValue value = task.output().returnValue();
      artifactSaver.save(name, value);
    }
  }
}
