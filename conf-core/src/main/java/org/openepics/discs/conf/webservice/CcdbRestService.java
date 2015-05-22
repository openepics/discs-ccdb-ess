/*
 * Copyright (c) 2014 European Spallation Source
 * Copyright (c) 2014 Cosylab d.d.
 *
 * This file is part of Cable Database.
 * Cable Database is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 2 of the License, or any newer version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.openepics.discs.conf.webservice;

import java.util.Arrays;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * This represents the JAX-RS application which hosts all REST resources of the CCDB.
 *
 * @author <a href="mailto:sunil.sah@cosylab.com">Sunil Sah</a>
 */
@ApplicationPath("/rest")
public class CcdbRestService extends Application {
    @Override
    public Set<Class<?>> getClasses() { // NOSONAR generic wildcard types part of the framework
        return getRestResourceClasses();
    }

    private Set<Class<?>> getRestResourceClasses() {  // NOSONAR generic wildcard types part of the framework
        return new java.util.HashSet<Class<?>>(Arrays.asList(DeviceTypeResourceImpl.class,
                InstallationSlotBasicResourceImpl.class));
    }

}
