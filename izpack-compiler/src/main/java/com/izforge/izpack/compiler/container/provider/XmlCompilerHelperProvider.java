/*
 * IzPack - Copyright 2001-2012 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
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

package com.izforge.izpack.compiler.container.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.izforge.izpack.compiler.helper.AssertionHelper;
import com.izforge.izpack.compiler.helper.XmlCompilerHelper;

/**
 * Provide xmlCompilerHelper
 *
 * @author Anthonin Bonnefoy
 */
public class XmlCompilerHelperProvider implements Provider<XmlCompilerHelper>
{

    private final AssertionHelper assertionHelper;

    @Inject
    public XmlCompilerHelperProvider(AssertionHelper assertionHelper)
    {
        this.assertionHelper = assertionHelper;
    }

    @Override
    public XmlCompilerHelper get()
    {
        return new XmlCompilerHelper(assertionHelper);
    }
}
