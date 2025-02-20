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

package com.izforge.izpack.installer.data;

import com.izforge.izpack.api.data.ConsolePrefs;
import com.izforge.izpack.api.data.Variables;
import com.izforge.izpack.util.Platform;

import java.io.Serializable;

public class ConsoleInstallData extends BasicInstallData implements Serializable
{
    private static final long serialVersionUID = -4272255846202671405L;

    /**
     * The console preferences.
     */
    public ConsolePrefs consolePrefs;

    public ConsoleInstallData(Variables variables, Platform platform)
    {
        super(variables, platform);
    }
}
