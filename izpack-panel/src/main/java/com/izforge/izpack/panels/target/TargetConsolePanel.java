/*
 * IzPack - Copyright 2001-2012 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
 *
 * Copyright 2002 Jan Blok
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

package com.izforge.izpack.panels.target;

import com.google.inject.Inject;
import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.handler.Prompt;
import com.izforge.izpack.installer.console.ConsolePanel;
import com.izforge.izpack.installer.panel.PanelView;
import com.izforge.izpack.installer.util.InstallPathHelper;
import com.izforge.izpack.panels.path.PathInputBase;
import com.izforge.izpack.panels.path.PathInputConsolePanel;
import com.izforge.izpack.util.Console;
import com.izforge.izpack.api.config.Options;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import static com.izforge.izpack.panels.target.TargetPanel.PANEL_NAME;

/**
 * Console implementation of the {@link TargetPanel}.
 *
 * @author Mounir El Hajj
 */
public class TargetConsolePanel extends PathInputConsolePanel implements ConsolePanel
{
    private final InstallData installData;
    /**
     * Constructs a {@code TargetConsolePanel}.
     *
     * @param panel the parent panel/view. May be {@code null}
     */
    @Inject
    public TargetConsolePanel(PanelView<ConsolePanel> panel, InstallData installData, Prompt prompt)
    {
        super(panel, PANEL_NAME, installData, prompt);
        this.installData = installData;
    }

    @Override
    public boolean generateOptions(InstallData installData, Options options)
    {
        final String name = InstallData.INSTALL_PATH;
        options.add(name, installData.getInstallPath());
        options.addEmptyLine(name);
        options.putComment(name, Arrays.asList(getPanel().getPanelId()));
        return true;
    }

    @Override
    public boolean run(InstallData installData, Properties properties)
    {
        boolean result = false;
        String path = properties.getProperty(InstallData.INSTALL_PATH);
        if (path == null || "".equals(path.trim()))
        {
            System.err.println("Missing mandatory target path!");
        }
        else if (TargetPanelHelper.isIncompatibleInstallation(path, installData.getInfo().isReadInstallationInformation()))
        {
            System.err.println(getMessage("incompatibleInstallation"));
        }
        else
        {
            path = installData.getVariables().replace(path);
            installData.setInstallPath(path);
            result = true;
        }
        return result;
    }

    /**
     * Runs the panel using the specified console.
     *
     * @param installData the installation data
     * @param console     the console
     * @return <tt>true</tt> if the panel ran successfully, otherwise <tt>false</tt>
     */
    @Override
    public boolean run(InstallData installData, Console console)
    {
        printHeadLine(installData, console);

        String introText = getI18nStringForClass("intro", PANEL_NAME, installData);
        if (introText != null)
        {
            console.println(introText);
            console.println();
        }

        String defaultPath = InstallPathHelper.getPath(installData);
        PathInputBase.setInstallData(installData);

        if (defaultPath == null)
        {
            defaultPath = "";
        }

        while (true)
        {
            String path = console.promptLocation(getMessage("info") + " [" + defaultPath + "] ", defaultPath);
            if (path != null)
            {
                path = installData.getVariables().replace(path);
                String normalizedPath = PathInputBase.normalizePath(path);
                File pathFile = new File(normalizedPath);

                if (TargetPanelHelper.isIncompatibleInstallation(normalizedPath, installData.getInfo().isReadInstallationInformation()))
                {
                    console.println(getMessage("incompatibleInstallation"));
                    continue;
                }
                else if (!PathInputBase.isWritable(normalizedPath))
                {
                    console.println(getMessage("notwritable"));
                    continue;
                }
                else if (!normalizedPath.isEmpty())
                {
                    if (pathFile.isFile())
                    {
                        console.println(getMessage("isfile"));
                        continue;
                    }
                    else if (pathFile.exists())
                    {
                        if (!checkOverwrite(pathFile, console))
                        {
                            continue;
                        }
                    }
                    else if (!checkCreateDirectory(pathFile, console))
                    {
                        continue;
                    }
                    else if (!installData.getPlatform().isValidDirectoryPath(pathFile))
                    {
                        console.println(getMessage("syntax.error"));
                        continue;
                    }
                    installData.setInstallPath(normalizedPath);
                    return promptEndPanel(installData, console);
                }
                return run(installData, console);
            }
            else
            {
                return false;
            }
        }
    }

    @Override
    public void createInstallationRecord(IXMLElement panelRoot)
    {
        new TargetPanelAutomation().createInstallationRecord(installData, panelRoot);
    }

}
