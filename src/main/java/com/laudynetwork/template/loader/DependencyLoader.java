package com.laudynetwork.template.loader;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.JarLibrary;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;


@SuppressWarnings("ALL")
public class DependencyLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        classpathBuilder.addLibrary(new JarLibrary(Path.of("NetworkUtils.jar")));
        classpathBuilder.addLibrary(new JarLibrary(Path.of("GameEngine.jar")));
    }
}
