package org.smoothbuild.task.exec;

/*
 * TODO remove when there's separate TaskManager that invokes task execution in
 * proper order so task execution is not initiated by call to Result.result().
 */
@SuppressWarnings("serial")
public class BuildInterruptedException extends RuntimeException {}
