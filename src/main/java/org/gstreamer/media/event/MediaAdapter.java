/* 
 * Copyright (c) 2007 Wayne Meissner
 * 
 * This file is part of gstreamer-java.
 *
 * This code is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * version 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gstreamer.media.event;


public class MediaAdapter implements MediaListener, java.util.EventListener {

    public void pause(StopEvent evt) { }
    public void start(StartEvent evt) { }
    public void stop(StopEvent evt) { }
    public void endOfMedia(EndOfMediaEvent evt) { }
    public void positionChanged(PositionChangedEvent evt) { }
    public void durationChanged(DurationChangedEvent evt) { }

}
