/***
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *    Project: www.simpledbm.org
 *    Author : Dibyendu Majumdar
 *    Email  : d dot majumdar at gmail dot com ignore
 */
package org.simpledbm.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Various Class Loader utilities. 
 * @author Dibyendu Majumdar
 * @since 14.Jan.2005
 */
public final class ClassUtils {

    public ClassUtils() {
    }
    

    /**
     * Get the ClassLoader to use. We always use the current Thread's
     * Context ClassLoader. Assumption is that all threads within the
     * application share the same ClassLoader.
     */
    private ClassLoader getClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return cl;
    }

    /**
     * A wrapper for Class.forName() so that we can change the behaviour
     * globally without changing the rest of the code.
     * 
     * @param name Name of the class to be loaded
     * @throws ClassNotFoundException
     */
    public Class<?> forName(String name) throws ClassNotFoundException {
        ClassLoader cl = getClassLoader();
        Class<?> clazz = null;
        clazz = Class.forName(name, true, cl);
        return clazz;
    }

    /**
     * Load a properties file from the classpath.
     * 
     * @param name Name of the properties file
     * @throws IOException If the properties file could not be loaded
     */
    public InputStream getResourceAsStream(String name)
            throws IOException {
        ClassLoader cl = getClassLoader();
        InputStream is = null;
        is = cl.getResourceAsStream(name);
        if (is == null) {
            throw new IOException("Unable to load resource" + name);
        }
        return is;
    }

    /**
     * Load a properties file from the classpath.
     * 
     * @param name Name of the properties file
     * @throws IOException If the properties file could not be loaded
     */
    public Properties getResourceAsProperties(String name)
            throws IOException {
        ClassLoader cl = getClassLoader();
        InputStream is = null;
        is = cl.getResourceAsStream(name);
        if (is == null) {
            throw new IOException("Unable to load resource " + name);
        }
        Properties props = new Properties();
        try {
            props.load(is);
        } finally {
            is.close();
        }
        return props;
    }

    /**
     * Helper for invoking an instance method that takes a single parameter.
     * This method also handles parameters of primitive type.
     * 
     * @param cl
     *            The class that the instance belongs to
     * @param instance
     *            The object on which we will invoke the method
     * @param methodName
     *            The method name
     * @param param
     *            The parameter
     * @throws Throwable
     */
    public Object invokeMethod(Class<?> cl, Object instance,
            String methodName, Object param) throws Throwable {
        Class<? extends Object> paramClass;
        if (param instanceof Integer)
            paramClass = Integer.TYPE;
        else if (param instanceof Long)
            paramClass = Long.TYPE;
        else if (param instanceof Short)
            paramClass = Short.TYPE;
        else if (param instanceof Boolean)
            paramClass = Boolean.TYPE;
        else if (param instanceof Double)
            paramClass = Double.TYPE;
        else if (param instanceof Float)
            paramClass = Float.TYPE;
        else if (param instanceof Character)
            paramClass = Character.TYPE;
        else if (param instanceof Byte)
            paramClass = Byte.TYPE;
        else
            paramClass = param.getClass();
        Method method = cl.getMethod(methodName, new Class[] { paramClass });
        try {
            return method.invoke(instance, new Object[] { param });
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    /**
     * Helper for invoking a static method that takes one parameter.
     * 
     * @param cl
     *            The class that implements the static method
     * @param methodName
     *            The method name
     * @param param
     *            A parameter
     * @param paramClass
     *            Class of the parameter
     * @throws Throwable
     */
    public Object invokeStaticMethod(Class<?> cl, String methodName,
            Object param, Class<?> paramClass) throws Throwable {
        Method method = cl.getMethod(methodName, new Class[] { paramClass });
        try {
            return method.invoke(null, new Object[] { param });
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    /**
     * Helper for invoking a constructor with one parameter.
     * 
     * @param className Class of which an instance is to be allocated
     * @param param Parameter
     * @param paramClass Type of the parameter
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Object createObject(String className, Object param,
            Class<?> paramClass) throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Class<?> clazzImpl = forName(className);
        Constructor<?> ctor = clazzImpl.getConstructor(new Class[] { paramClass });
        Object instance = ctor.newInstance(new Object[] { param });
        return instance;
    }
}