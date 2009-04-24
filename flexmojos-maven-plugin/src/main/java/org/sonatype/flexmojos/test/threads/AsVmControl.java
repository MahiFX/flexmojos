/**
 * Flexmojos is a set of maven goals to allow maven users to compile, optimize and test Flex SWF, Flex SWC, Air SWF and Air SWC.
 * Copyright (C) 2008-2012  Marvin Froeder <marvin@flexmojos.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sonatype.flexmojos.test.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * This class will ping Action Script virtual machine to make sure if the application still running
 * 
 * @plexus.component role="org.sonatype.flexmojos.test.threads.AsVmControl"
 * @author velo
 */
public class AsVmControl
    extends AbstractSocketThread
{
    public static final String ROLE = AsVmControl.class.getName();

    public static final String STATUS = "Server Status";

    public static final String OK = "OK";

    @Override
    protected void handleRequest()
        throws SocketTimeoutException, SocketException, IOException
    {
        getLogger().debug( "AsVmControl handleRequest" );

        while ( true )
        {
            getLogger().debug( "get status" );
            BufferedWriter out = new BufferedWriter( new OutputStreamWriter( super.out ) );
            out.write( STATUS );
            out.flush();

            try
            {
                getLogger().debug( "got status" );
                BufferedReader in = new BufferedReader( new InputStreamReader( super.in ) );
                String result = in.readLine();
                getLogger().debug( "status: " + result );
                if ( !result.equals( OK ) )
                {
                    setError( "Invalid virtual machine status: " + result, null );
                }
            }
            catch ( SocketTimeoutException e )
            {
                setError( "Remote virtual machine didn't reply, looks to be stucked", e );
            }
        }
    }

    public void init( int testControlPort, int firstConnectionTimeout )
    {
        super.init( testControlPort );
        super.firstConnectionTimeout = firstConnectionTimeout;
    }

}