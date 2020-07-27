package com.pb.stratus.core.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigFileTypeResolverTest
{

    @Test
    public void shouldResolveFileWithoutExtension()
    {
        ConfigFileType type = ConfigFileType.CATALOG;
        ConfigFileTypeResolver resolver = new ConfigFileTypeResolver();
        String path = resolver.resolve("someFile", ConfigFileType.CATALOG);
        assertEquals(path, type.getPath() + "/someFile" + type.getExtension());
    }

    @Test
    public void shouldResolveFileWithExtension()
    {
        ConfigFileType type = ConfigFileType.CATALOG;
        ConfigFileTypeResolver resolver = new ConfigFileTypeResolver();
        String path = resolver.resolve("someFile.asdf", ConfigFileType.CATALOG);
        assertEquals(path, type.getPath() + "/someFile.asdf");
    }
}
