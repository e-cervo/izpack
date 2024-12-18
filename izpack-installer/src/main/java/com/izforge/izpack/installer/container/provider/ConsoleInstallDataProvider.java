/*
 * Copyright 2016 Julien Ponge, René Krell and the IzPack team.
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
import com.google.inject.Singleton;
import com.izforge.izpack.api.data.ConsolePrefs;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.resource.Locales;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.core.data.DefaultVariables;
import com.izforge.izpack.installer.data.ConsoleInstallData;
import com.izforge.izpack.util.Housekeeper;
import com.izforge.izpack.util.PlatformModelMatcher;

@Singleton
public class ConsoleInstallDataProvider extends AbstractInstallDataProvider<ConsoleInstallData> {

    private final Resources resources;
    private final Locales locales;
    private final DefaultVariables variables;
    private final Housekeeper housekeeper;
    private final PlatformModelMatcher matcher;

    @Inject
    public ConsoleInstallDataProvider(Resources resources,
                                      Locales locales,
                                      DefaultVariables variables,
                                      Housekeeper housekeeper,
                                      PlatformModelMatcher matcher) {
        this.resources = resources;
        this.locales = locales;
        this.variables = variables;
        this.housekeeper = housekeeper;
        this.matcher = matcher;
    }

    @Override
    public ConsoleInstallData loadInstallData() {
        try {
            final ConsoleInstallData consoleInstallData = new ConsoleInstallData(variables, matcher.getCurrentPlatform());
            consoleInstallData.setVariable(InstallData.INSTALLER_MODE, InstallData.INSTALLER_MODE_CONSOLE);
            loadInstallData(consoleInstallData, resources, matcher, housekeeper);
            loadConsoleInstallData(consoleInstallData, resources);
            loadInstallerRequirements(consoleInstallData, resources);
            loadDynamicVariables(variables, consoleInstallData, resources);
            loadDynamicConditions(consoleInstallData, resources);
            loadDefaultLocale(consoleInstallData, locales);
            // Load custom langpack if exist.
            AbstractInstallDataProvider.addCustomLangpack(consoleInstallData, locales);
            // Load user input langpack if exist.
            AbstractInstallDataProvider.addUserInputLangpack(consoleInstallData, locales);
            return consoleInstallData;

        } catch (Exception e) {
            throw new IllegalStateException("Unable to create console install data", e);
        }
    }

    /**
     * Load GUI preference information.
     *
     * @param installData the console installation data
     * @throws Exception
     */
    private void loadConsoleInstallData(ConsoleInstallData installData, Resources resources) throws Exception {
        installData.consolePrefs = (ConsolePrefs) resources.getObject("ConsolePrefs");
    }

}
