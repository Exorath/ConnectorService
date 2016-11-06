/*
 * Copyright 2016 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.service.connector.service;

import com.exorath.service.connector.res.Filter;
import com.exorath.service.connector.res.Server;
import com.exorath.service.connector.res.ServerInfo;
import com.exorath.service.connector.res.Success;

/**
 * Created by toonsev on 11/5/2016.
 */
public interface DatabaseProvider {
    //Server info stuff

    ServerInfo getServerInfo(String filterId);

    void updateServerInfo(String filterId, ServerInfo serverInfo);

    Iterable<Server> getServersWithFilter(Filter filter);

    //Server specific stuff

    Success putServer(Server server);

    Server getJoinableServer(Filter filter);



}