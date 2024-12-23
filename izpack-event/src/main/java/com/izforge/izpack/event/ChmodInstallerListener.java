/*
 * IzPack - Copyright 2001-2005 Julien Ponge, All Rights Reserved.
 * 
 * http://izpack.org/
 * http://izpack.codehaus.org/
 * 
 * Copyright 2004 Klaus Bartz
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

package com.izforge.izpack.event;

import java.io.File;

import com.google.inject.Inject;
import com.izforge.izpack.api.data.InstallData;
import com.izforge.izpack.api.data.Pack;
import com.izforge.izpack.api.data.PackFile;
import com.izforge.izpack.api.exception.InstallerException;
import com.izforge.izpack.event.AbstractProgressInstallerListener;
import com.izforge.izpack.util.FileExecutor;
import com.izforge.izpack.util.OsVersion;

/**
 * <p>InstallerListener for file and directory permissions
 * on Unix.</p>
 *
 * @author Klaus Bartz
 */
public class ChmodInstallerListener extends AbstractProgressInstallerListener
{

    /**
     * Constructs an {@code ChmodInstallerListener}.
     *
     * @param installData the installation data
     */
    @Inject
    public ChmodInstallerListener(InstallData installData)
    {
        super(installData);
    }

    /**
     * Determines if the listener should be notified of every file and directory installation.
     *
     * @return {@code true}
     */
    @Override
    public boolean isFileListener()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterFile(File filePath, PackFile pf, Pack pack)
    {
        if (pf.getAdditionals() == null)
        {
            return;
        }
        Object file = pf.getAdditionals().get("permission.file");
        int fileVal = -1;
        if (file != null && file instanceof Integer)
        {
            fileVal = (Integer) file;
        }
        if (fileVal != -1)
        {
            chmod(filePath, fileVal);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterDir(File dirPath, PackFile pf, Pack pack)
    {
        if (pf.getAdditionals() == null)
        {
            return;
        }
        if (dirPath == null)
        {
            return;
        }
        Object dir = pf.getAdditionals().get("permission.dir");
        int dirVal = -1;
        if (dir != null && dir instanceof Integer)
        {
            dirVal = (Integer) dir;
        }
        if (dirVal != -1)
        {
            if ((dirVal & 0x000001C0) < 0x000001C0)
            {
                throw new InstallerException("Bad owner permission for directory "
                                                     + dirPath.getAbsolutePath() + "; at installation time the owner needs full rights");
            }
            chmod(dirPath, dirVal);
        }
    }

    private void chmod(File path, int permissions)
    {
        String pathSep = System.getProperty("path.separator");
        if (OsVersion.IS_WINDOWS)
        {
            throw new InstallerException("Sorry, chmod not supported yet on windows; use this class OS dependant.");
        }
        if (path == null)
        // Oops this is an error, but in this example we ignore it ...
        {
            return;
        }
        String permStr = Integer.toOctalString(permissions);
        String[] params = {"chmod", permStr, path.getAbsolutePath()};
        String[] output = new String[2];
        FileExecutor fe = new FileExecutor();
        fe.executeCommand(params, output);
    }
}
