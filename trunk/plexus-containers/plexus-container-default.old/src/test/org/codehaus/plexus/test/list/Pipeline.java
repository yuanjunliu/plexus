package org.codehaus.plexus.test.list;

import org.codehaus.plexus.test.map.Activity;

import java.util.List;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id$
 */
public interface Pipeline
{
    static String ROLE = Pipeline.class.getName();

    void execute();

    List getValves();
}
