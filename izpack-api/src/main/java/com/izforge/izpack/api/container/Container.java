/*
 * IzPack - Copyright 2001-2012 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
 *
 * Copyright 2010 Anthonin Bonnefoy
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

package com.izforge.izpack.api.container;

import com.google.inject.Provider;
import com.izforge.izpack.api.exception.ContainerException;
import com.izforge.izpack.api.exception.IzPackClassNotFoundException;


/**
 * Component container.
 *
 * @author Anthonin Bonnefoy
 * @author Tim Anderson
 */
public interface Container
{
    /**
     * Register a component type.
     *
     * @param componentType the component type
     * @throws ContainerException if registration fails
     */
    <T> void addComponent(Class<T> componentType);

    <T> void addComponent(T component);

    <T, U extends T> void addProvider(Class<T> type, Class<? extends Provider<U>> provider);

    <T, U extends T> void addProvider(Class<T> type, Provider<U> provider);

    /**
     * Register a component.
     *
     * @param componentKey   the component identifier. This must be unique within the container
     * @param implementation the component implementation
     * @throws ContainerException if registration fails
     */
    <T, U extends T> void addComponent(Class<T> componentKey, Class<U> implementation);

    <T, U extends T> void addComponent(Class<T> componentKey, U implementation);

    <T, U extends T> void addComponent(String componentKey, Class<T> type, U implementation);

    <T, U extends T> void addComponent(String componentKey, Class<T> type, Class<U> implementation);

    default void addConfig(String componentKey, String value) {
        addComponent(componentKey, String.class, value);
    }

    <T> void removeComponent(Class<T> componentType);

    /**
     * Retrieve a component by its component type.
     * <p/>
     * If the component type is registered but an instance does not exist, then it will be created.
     *
     * @param componentType the type of the component
     * @return the corresponding object instance, or <tt>null</tt> if it does not exist
     * @throws ContainerException if component creation fails
     */
    <T> T getComponent(Class<T> componentType);

    /**
     * Retrieve a component by its component key or type.
     * <p/>
     * If the component type is registered but an instance does not exist, then it will be created.
     *
     * @param key the key of the component
     * @return the corresponding object instance, or <tt>null</tt> if it does not exist
     * @throws ContainerException if component creation fails
     */
    <T> T getComponent(String key, Class<T> type);

    /**
     * Creates a child container.
     * <p/>
     * A child container:
     * <ul>
     * <li>may have different objects keyed on the same identifiers as its parent.</li>
     * <li>will query its parent for dependencies if they aren't available</li>
     * <li>is disposed when its parent is disposed</li>
     * </ul>
     *
     * @return a new container
     * @throws ContainerException if creation fails
     */
    Container createChildContainer();

    /**
     * Removes a child container.
     *
     * @param child the container to remove
     * @return <tt>true</tt> if the container was removed
     */
    boolean removeChildContainer(Container child);

    /**
     * Disposes of the container and all of its child containers.
     */
    void dispose();

    /**
     * Returns a class given its name.
     *
     * @param className the class name
     * @param superType the super type
     * @return the corresponding class
     * @throws ClassCastException           if <tt>className</tt> does not implement or extend <tt>superType</tt>
     * @throws IzPackClassNotFoundException if the class cannot be found
     */
    <T> Class<T> getClass(String className, Class<T> superType);
}
