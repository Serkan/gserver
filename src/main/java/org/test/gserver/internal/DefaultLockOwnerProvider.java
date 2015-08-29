package org.test.gserver.internal;

import java.lang.management.ManagementFactory;

/**
 * Prepares lock owner id by concatenating thread, process and hostname.
 *
 * @author serkan
 */
public class DefaultLockOwnerProvider implements LockOwnerProvider {

	@Override
	public String getOwner() {
		long tid = Thread.currentThread().getId();
		String host = ManagementFactory.getRuntimeMXBean().getName();
		// return owner id as 'thread-id@process-id@hostname' on Linux and Windows machines
		return tid + "@" + host;
	}
}
