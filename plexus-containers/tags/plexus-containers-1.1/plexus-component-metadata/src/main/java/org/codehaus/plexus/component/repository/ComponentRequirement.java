package org.codehaus.plexus.component.repository;

import org.codehaus.plexus.PlexusConstants;

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

/**
 * This represents a component this is required by another component.
 * 
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id: ComponentRequirement.java 8109 2009-02-11 18:20:25Z dain $
 */
public class ComponentRequirement
{
    private String fieldName;

    private String role;

    private String roleHint = PlexusConstants.PLEXUS_DEFAULT_HINT;

    private String fieldMappingType;

    public ComponentRequirement()
    {
    }

    public ComponentRequirement( String fieldName, String role )
    {
        this( fieldName, role, null );
    }

    public ComponentRequirement( String fieldName, String role, String roleHint )
    {
        this.fieldName = fieldName;
        this.role = role;

        if ( roleHint != null )
        {
            this.roleHint = roleHint;
        }
    }

    public ComponentRequirement( String fieldName, Class<?> type )
    {
        this( fieldName, type.getName(), null );
    }

    public ComponentRequirement( String fieldName, Class<?> type, String roleHint )
    {
        this( fieldName, type.getName(), roleHint );
    }

    /**
     * Returns the field name that this component requirement will inject.
     * @return the field name that this component requirement will inject
     */
    public String getFieldName()
    {
        return fieldName;
    }

    /**
     * Sets the name of the field that will be populated by the required
     * component.
     * @param fieldName the name of the field to be populated
     */
    public void setFieldName( String fieldName )
    {
        this.fieldName = fieldName;
    }

    /**
     * Returns the role of the required component.
     * @return the role of the required component
     */
    public String getRole()
    {
        return role;
    }

    /**
     * Sets the role of the require component.
     * @param role the required component's role
     */
    public void setRole( String role )
    {
        this.role = role;
    }

    /**
     * Returns the role-hint of the required component.
     * @return the role-hint of the required component
     */
    public String getRoleHint()
    {
        return roleHint;
    }

    /**
     * Sets the role-hint of the require component.
     * Pasing null will set the hint to the default value.
     * @param roleHint the required component's role-hint
     */
    public void setRoleHint( String roleHint )
    {
        if ( roleHint == null || roleHint.trim().equals( "" ) )
        {
            this.roleHint = PlexusConstants.PLEXUS_DEFAULT_HINT;
        }
        else
        {
            this.roleHint = roleHint;
        }
    }

    /**
     * Returns the type of the field this component requirement will inject.
     * @return the type of the field this component requirement will inject
     */
    public String getFieldMappingType()
    {
        return fieldMappingType;
    }

    /**
     * Sets the type of the field that will be populated by the required
     * component.
     * @param fieldType the type of the field to be populated
     */
    public void setFieldMappingType( String fieldType )
    {
        this.fieldMappingType = fieldType;
    }

    public String toString()
    {
        return "ComponentRequirement{" +
            "role='" + getRole() + "'" + ", " +
            "roleHint='" + getRoleHint() + "', " +
            "fieldName='" + getFieldName() + "'" +
            "}";
    }

    /**
     * Returns a human-friendly key, suitable for display.
     * @return a human-friendly key
     */
    public String getHumanReadableKey()
    {
        StringBuffer key = new StringBuffer();

        key.append( "role: '").append( getRole() ).append( "'" );

        if ( getRoleHint() != null )
        {
            key.append( ", role-hint: '" ).append( getRoleHint() ).append( "'. " );
        }

        if ( getFieldName() != null )
        {
            key.append( ", field name: '" ).append( getFieldName() ).append( "' " );
        }

        return key.toString();
    }

    public boolean equals( Object other )
    {
        if ( other instanceof ComponentRequirement )
        {
            String myId = role + ":" + roleHint;
            
            ComponentRequirement req = (ComponentRequirement) other;
            String otherId = req.role + ":" + req.roleHint;
            
            return myId.equals( otherId );
        }
        
        return false;
    }
    
    public int hashCode()
    {
        return ( role + ":" + roleHint ).hashCode();
    }
}
