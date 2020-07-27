package com.pb.stratus.controller.json;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class JavaBeanStrategyTest
{
    
    private JavaBeanStrategy strategy;
    
    private TypedConverterStrategy mockOwner;

    private StringBuilder stringBuilder;
    
    @Before
    public void setUp()
    {
        mockOwner = new TypedConverterStrategy()
        {
            int counter = 0;
            
            public void processValue(Object value, StringBuilder b)
            {
                b.append(counter++);
            }
        };
        mockOwner = spy(mockOwner);
        strategy = new JavaBeanStrategy();
        strategy.setOwner(mockOwner);
    }
    
    @Test
    public void shouldPassPropertiesBackToOwner()
    {
        TestBean tb = new TestBean("value1", 1234);
        assertEquals("{\"prop1\": 0, \"prop2\": 1}", serialize(tb));
        verify(mockOwner).processValue("value1", stringBuilder);
        verify(mockOwner).processValue(1234, stringBuilder);
    }
    
    @Test
    public void shouldIgnoreExcludedProperties()
    {
        strategy.excludeProperty(TestBean.class, "prop1");
        TestBean tb = new TestBean("value1", 1234);
        assertEquals("{\"prop2\": 0}", serialize(tb));
    }
    
    private String serialize(Object o)
    {
        stringBuilder = new StringBuilder();
        strategy.processValue(o, stringBuilder);
        return stringBuilder.toString();
    }
    
    public static class TestBean
    {
        
        public TestBean(String prop1, int prop2)
        {
            this.prop1 = prop1;
            this.prop2 = prop2;
        }
        private String prop1;
        
        private int prop2;

        public String getProp1()
        {
            return prop1;
        }

        public int getProp2()
        {
            return prop2;
        }
        
        
    }

}
