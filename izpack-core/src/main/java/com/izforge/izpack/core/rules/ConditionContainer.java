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

package com.izforge.izpack.core.rules;

import com.izforge.izpack.api.container.Container;
import com.izforge.izpack.core.container.AbstractDelegatingContainer;


/**
 * Condition container.
 *
 * @author Anthonin Bonnefoy
 */
public class ConditionContainer extends AbstractDelegatingContainer
{

    /**
     * Constructs a <tt>ConditionContainer</tt>.
     *
     * @param parent the parent container
     */
    public ConditionContainer(Container parent)
    {
        super(parent::createChildContainer);
    }

}
