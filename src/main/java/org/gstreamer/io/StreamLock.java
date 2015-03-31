/*
 * Copyright (c) 2012 chris@crazyfool.org
 * 
 * This file is part of gstreamer-java.
 * 
 * This code is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License version 3 only, as published by the Free Software Foundation.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License version 3
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3 along with this work. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.io;

public class StreamLock {
	protected boolean done;

	public StreamLock() {
		done = false;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone() {
		synchronized (this) {
			done = true;
			this.notifyAll();
		}
	}
}
