/*
 * IzPack - Copyright 2001-2012 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
 *
 * Copyright 2012 Tim Anderson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izforge.izpack.installer.container.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.izforge.izpack.api.resource.Locales;
import com.izforge.izpack.core.resource.DefaultLocales;
import com.izforge.izpack.core.resource.ResourceManager;

/**
 * Provider of {@link Locales}.
 *
 * @author Tim Anderson
 */
public class LocalesProvider implements Provider<Locales>
{
    private final ResourceManager resources;

    @Inject
    public LocalesProvider(ResourceManager resources)
    {
        this.resources = resources;
    }

    @Override
    public Locales get()
    {
        Locales locales = new DefaultLocales(resources);
        resources.setLocales(locales);
        return locales;
    }
}
