package com.pb.stratus.controller.util;

import com.pb.stratus.controller.print.template.XslFoUtils;
import com.pb.stratus.core.util.ObjectUtils;
import org.apache.commons.lang.ArrayUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A mock helper that allow us to verify method invocation order in a stricter
 * way than Mockito's InOrder. Unless {@link #allowIntermediateCalls()} was 
 * invoked, this class fails if any unverified intermediate calls happened. 
 * Also, {@link #allowIntermediateCalls()} is reset after each verification. 
 */
public class MockContentHandlerWatcher implements InvocationHandler
{
    
    private List<Call> calls = new LinkedList<Call>();
    
    private int cursor;
    
    private boolean allowIntermediateCalls = false;

    private String namespace;

    private Object prefix;
    

    public void useNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    public void usePrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public ContentHandler getMock()
    {
        return (ContentHandler) Proxy.newProxyInstance(
                MockContentHandlerWatcher.class.getClassLoader(), new Class[] {
                    ContentHandler.class}, this);
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
    {
        // Ignore object methods
        if (method.getDeclaringClass() == Object.class)
        {
            return null;
        }
        calls.add(new Call(method, args));
        return null;
    }
    
    private static class Call
    {
        
        private Method method;
        
        private Object[] args;
        
        public Call(Method m, Object[] args)
        {
            this.method = m;
            this.args = args;
        }
        
        public String toString()
        {
            return method.getDeclaringClass().getName() + "." 
                    + method.getName() + "(" + Arrays.deepToString(args) + ")";
        }
        
        public boolean matches(Call other)
        {
            if (!method.equals(other.method))
            {
                return false;
            }
            if (other.args == null)
            {
                return true;
            }
            if (args == null)
            {
                return false;
            }
            if (args.length != other.args.length)
            {
                return false;
            }
            for (int i = 0; i < args.length; i++)
            {
                if (other.args[i] != null) 
                {
                    if (other.args[i] instanceof Attributes)
                    {
                        if (!containsAllAttributes((Attributes) other.args[i], 
                                (Attributes) args[i]))
                        {
                            return false;
                        }
                    }
                    else if (other.args[i].getClass().isArray())
                    {
                        if (args[i] == null)
                        {
                            return false;
                        }
                        if (ArrayUtils.getLength(args[i]) != ArrayUtils.getLength(other.args[i]))
                        {
                            return false;
                        }
                        for (int j = 0; j < ArrayUtils.getLength(other.args[i]); j++)
                        {
                            Object ths = Array.get(args[i], j);
                            Object that = Array.get(other.args[i], j);
                            if (!ObjectUtils.equals(ths, that))
                            {
                                return false;
                            }
                        }
                    }
                    else if (!ObjectUtils.equals(args[i], other.args[i]))
                    {
                        return false;
                    }
                }
            }
            return true;
        }
        
        public boolean containsAllAttributes(Attributes expected, 
                Attributes actual)
        {
            if (expected == null)
            {
                return actual == null;
            }
            if (actual == null)
            {
                return false;
            }
            for (int i = 0; i < expected.getLength(); i++)
            {
                String name = expected.getLocalName(i);
                String expectedValue = expected.getValue(name);
                String actualValue = actual.getValue(name);
                if (!ObjectUtils.equals(expectedValue, actualValue))
                {
                    return false;
                }
            }
            return true;
        }
        
        
    }
    
    public void verifyStartElement(String elementName, Attributes attrs)
    {
        Method m;
        try
        {   
            m = ContentHandler.class.getMethod("startElement", new Class[] {
                    String.class, String.class, String.class, 
                    Attributes.class});
        }
        catch (NoSuchMethodException x)
        {
            throw new RuntimeException(x);
        }
        verifyCall(new Call(m, new Object[] {namespace, elementName, 
                prefix + ":" + elementName, attrs}));
    }
    
    public void verifyEndElement(String elementName)
    {
        Method m;
        try
        {   
            m = ContentHandler.class.getMethod("endElement", new Class[] {
                    String.class, String.class, String.class});
        }
        catch (NoSuchMethodException x)
        {
            throw new RuntimeException(x);
        }
        verifyCall(new Call(m, new Object[] {namespace, elementName, 
                prefix + ":" + elementName}));
    }
    
    public void verifyCharacters(String expectedText)
    {
        Method m;
        try
        {   
            m = ContentHandler.class.getMethod("characters", new Class[] {
                    char[].class, Integer.TYPE, Integer.TYPE});
        }
        catch (NoSuchMethodException x)
        {
            throw new RuntimeException(x);
        }
        verifyCall(new Call(m, new Object[] {expectedText.toCharArray(), 0, expectedText.length()}));
    }
    
    public void verifyCall(Call expectedCall)
    {
        try
        {
            if (allowIntermediateCalls)
            {
                Call c = getNextMatchingCallAndMoveCursor(expectedCall);
                if (c == null)
                {
                    throw new IllegalArgumentException("No matching " 
                            + "calls found for " + expectedCall);
                }
            }
            else
            {
                Call c = getCallAtCursorAndMoveCursor();
                if (!c.matches(expectedCall))
                {
                    throw new IllegalStateException("Expected '" + expectedCall 
                            + "' but found '" + c + "'");
                }
            }
        }
        finally
        {
            allowIntermediateCalls = false;
        }
    }
    
    private Call getCallAtCursorAndMoveCursor()
    {
        if (cursor >= calls.size())
        {
            throw new IllegalArgumentException("No more calls");
        }
        return calls.get(cursor++);
    }
    
    private Call getNextMatchingCallAndMoveCursor(Call expected)
    {
        if (cursor >= calls.size())
        {
            throw new IllegalArgumentException("No more calls");
        }
        for (int i = cursor; i < calls.size(); i++)
        {
            Call c = calls.get(i);
            if (c.matches(expected))
            {
                cursor = i + 1;
                return c;
            }
        }
        return null;
    }
    
    public void verifyNoMoreInteractions()
    {
        if (cursor < calls.size())
        {
            throw new IllegalStateException("No more interactions expected, " 
                    + "but found " + calls.get(cursor));
        }
    }
    
    public void allowIntermediateCalls()
    {
        allowIntermediateCalls = true;
    }
    
    public static void main(String[] args) throws Exception
    {
        MockContentHandlerWatcher watcher = new MockContentHandlerWatcher();
        ContentHandler mockHandler = watcher.getMock();
        mockHandler.startDocument();
        mockHandler.startElement("", "testElement", "", 
                XslFoUtils.createAttribute("a", "b"));
        mockHandler.startDocument();
        mockHandler.startElement("", "testElement", "", null);
        watcher.allowIntermediateCalls();
        watcher.verifyStartElement("testElement", 
                XslFoUtils.createAttribute("a", "b"));
        watcher.allowIntermediateCalls();
    }
    
}
