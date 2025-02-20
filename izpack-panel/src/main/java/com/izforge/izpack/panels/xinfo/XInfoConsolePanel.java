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

package com.izforge.izpack.panels.xinfo;

import com.google.inject.Inject;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.installer.console.AbstractTextConsolePanel;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.installer.util.PanelHelper;
import com.izforge.izpack.panels.info.InfoPanel;

/**
 * Console implementation of {@link XInfoPanel}.
 *
 * @author Tim Anderson
 */
public class XInfoConsolePanel extends AbstractTextConsolePanel
{
    /**
     * The resources.
     */
    private final Resources resources;

    /**
     * Resource name for panel content.
     */
    private final String panelResourceName;

    /**
     * Constructs an <tt>XInfoConsolePanel</tt>.
     *
     * @param resources the resources
     * @param panel     the parent panel/view. May be {@code null}
     */
    @Inject
    public XInfoConsolePanel(Resources resources, PanelView<ConsolePanel> panel)
    {
        super(panel);
        this.resources = resources;
        panelResourceName = PanelHelper.getPanelResourceName(panel.getPanel(), "info", resources);
    }

    /**
     * Returns the text to display.
     *
     * @return the text
     */
    @Override
    protected String getText()
    {
        return resources.getString(panelResourceName, "Error : could not load the info text!");
    }
}
