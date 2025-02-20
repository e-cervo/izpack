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

package com.izforge.izpack.compiler.container;

import java.util.jar.JarFile;

import com.izforge.izpack.installer.container.impl.InstallerContainer;
import org.junit.runners.model.FrameworkMethod;

import com.izforge.izpack.compiler.data.CompilerData;
import com.izforge.izpack.core.container.AbstractContainer;

/**
 * Abstract implementation of a container for testing purposes.
 *
 * @author Anthonin Bonnefoy
 * @author Tim Anderson
 */
public abstract class AbstractTestInstallationContainer extends AbstractContainer
{
    protected Class<?> klass;
    protected FrameworkMethod frameworkMethod;

    public AbstractTestInstallationContainer(Class<?> klass, FrameworkMethod frameworkMethod, boolean fillContainer)
    {
        super(fillContainer);
        this.klass = klass;
        this.frameworkMethod = frameworkMethod;
    }

    @Override
    protected void fillContainer()
    {
        TestCompilationContainer compiler = new TestCompilationContainer(klass, frameworkMethod);
        compiler.launchCompilation();

        // propagate compilation objects to the installer container so the installation test can use them
        CompilerData data = compiler.getComponent(CompilerData.class);
        JarFile installer = compiler.getComponent(JarFile.class);
        addComponent(data);
        addComponent(installer);

        addModules(fillInstallerContainer().getModules());
    }

    protected abstract InstallerContainer fillInstallerContainer();
}
