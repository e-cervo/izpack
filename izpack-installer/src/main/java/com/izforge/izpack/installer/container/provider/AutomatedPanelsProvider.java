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

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.izforge.izpack.api.data.AutomatedInstallData;
import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.exception.IzPackException;
import com.izforge.izpack.api.factory.ObjectFactory;
import com.izforge.izpack.installer.automation.AutomatedPanelView;
import com.izforge.izpack.installer.automation.AutomatedPanels;
import com.izforge.izpack.installer.automation.PanelAutomationHelper;
import com.izforge.izpack.util.PlatformModelMatcher;


/**
 * Provider of {@link AutomatedPanels}.
 *
 * @author Tim Anderson
 */
public class AutomatedPanelsProvider extends PanelsProvider<AutomatedPanels>
{
    private final ObjectFactory factory;
    private final AutomatedInstallData installData;
    private final PanelAutomationHelper helper;
    private final PlatformModelMatcher matcher;

    @Inject
    public AutomatedPanelsProvider(ObjectFactory factory,
                                   AutomatedInstallData installData,
                                   PanelAutomationHelper helper,
                                   PlatformModelMatcher matcher) {
        this.factory = factory;
        this.installData = installData;
        this.helper = helper;
        this.matcher = matcher;
    }

    /**
     * Creates the panels.
     * <p/>
     * This invokes any pre-construction actions associated with them.
     *
     * @throws IzPackException if a panel doesn't have unique identifier
     */
    @Override
    public AutomatedPanels get()
    {
        List<AutomatedPanelView> panels = new ArrayList<AutomatedPanelView>();

        for (Panel panel : prepare(installData, matcher))
        {
            AutomatedPanelView panelView = new AutomatedPanelView(panel, factory, installData, helper);
            panels.add(panelView);
        }
        return new AutomatedPanels(panels, installData);
    }

}
