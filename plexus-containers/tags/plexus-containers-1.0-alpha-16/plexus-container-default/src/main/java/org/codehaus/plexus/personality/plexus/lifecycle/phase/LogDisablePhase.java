package org.codehaus.plexus.personality.plexus.lifecycle.phase;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.manager.ComponentManager;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.lifecycle.phase.AbstractPhase;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.LoggerManager;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LogDisablePhase
    extends AbstractPhase
{
    public void execute( Object object, ComponentManager componentManager, ClassRealm lookupRealm )
        throws PhaseExecutionException
    {
        LoggerManager loggerManager;
        ComponentDescriptor descriptor;

        if ( object instanceof LogEnabled )
        {
            try
            {
                loggerManager = (LoggerManager) componentManager.getContainer().lookup( LoggerManager.ROLE, lookupRealm );
            }
            catch ( ComponentLookupException e )
            {
                throw new PhaseExecutionException( "Unable to locate logger manager", e );
            }
            descriptor = componentManager.getComponentDescriptor();
            loggerManager.returnComponentLogger( descriptor.getRole(), descriptor.getRoleHint() );
        }
    }
}
