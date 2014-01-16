package org.smoothbuild.message.listen;

/**
 * Thrown to stop execution of given phase. Indicates that appropriate messages
 * have been already sent to UserConsole.
 */
@SuppressWarnings("serial")
public class PhaseFailedException extends RuntimeException {}
