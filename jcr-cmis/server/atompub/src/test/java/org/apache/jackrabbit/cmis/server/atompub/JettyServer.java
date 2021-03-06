/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.cmis.server.atompub;

import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * Jetty server used for testing.
 */
public class JettyServer {

    public static final int DEFAULT_PORT = 9002;

    private final int port;
    private Server server;

    public JettyServer() {
        this(DEFAULT_PORT);
    }

    public JettyServer(int port) {
        this.port = port;
    }

    public void start(Class<? extends Provider> _class) throws Exception {
        server = new Server(port);
        Context context = new Context(server, "/", Context.SESSIONS);
        ServletHolder servletHolder = new ServletHolder(new AbderaServlet());
        servletHolder.setInitParameter(ServiceManager.PROVIDER, _class
                .getName());
        context.addServlet(servletHolder, "/*");
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

}
