package org.gotti.wurmunlimited.modloader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import org.gotti.wurmunlimited.modloader.interfaces.ModEntry;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(HookManagerTestRunner.class)
public class ModInstanceBuilderTest {
	
	@Test
	@Ignore // Adding a resource to the shared class loader will make the resource available in other tests as well
	public void testShared() throws ClassNotFoundException, IOException {
		checkResources(true);
	}
	
	@Test
	public void test() throws ClassNotFoundException, IOException {
		checkResources(false);
	}
	
	private void checkResources(boolean sharedClassLoader) throws IOException {
		
		ModInstanceBuilder<Object> instanceBuilder = new ModInstanceBuilder<>(Object.class);
		
		final String modName = "test";
		
		Properties properties = new Properties();
		properties.setProperty("sharedClassLoader", Boolean.toString(sharedClassLoader));
		properties.setProperty("classname", "DummyMod");
		properties.setProperty("classpath", "test.jar,files");
		final ModEntry<?> entry = instanceBuilder.createModEntry(new ModInfo(properties, modName));
		
		final Path modPath = Paths.get("mods").resolve(entry.getName());
		
		final URL jarUrl = new URL("jar:" + modPath.resolve("test.jar").toUri().toString() + "!/test.txt");
		final URL fileUrl = modPath.resolve("files/test.txt").toUri().toURL();

		final ClassLoader classLoader = entry.getModClassLoader();
		assertThat(jarUrl).isNotNull();

		ArrayList<URL> files = Collections.list(classLoader.getResources("test.txt"));
		assertThat(files).containsExactly(jarUrl, fileUrl);
	}
	
	@Test
	public void testWildcard() throws ClassNotFoundException, IOException {
		
		ModInstanceBuilder<Object> instanceBuilder = new ModInstanceBuilder<>(Object.class);
		
		final String modName = "test";
		
		Properties properties = new Properties();
		properties.setProperty("sharedClassLoader", Boolean.toString(false));
		properties.setProperty("classname", "DummyMod");
		properties.setProperty("classpath", "*.jar,files");
		ModEntry<?> entry = instanceBuilder.createModEntry(new ModInfo(properties, modName));
		
		final Path modPath = Paths.get("mods").resolve(entry.getName());
		
		final URL jarUrl = new URL("jar:" + modPath.resolve("test.jar").toUri().toString() + "!/test.txt");
		final URL fileUrl = modPath.resolve("files/test.txt").toUri().toURL();

		final ClassLoader classLoader = entry.getModClassLoader();
		assertThat(jarUrl).isNotNull();

		ArrayList<URL> files = Collections.list(classLoader.getResources("test.txt"));
		assertThat(files).containsExactly(jarUrl, fileUrl);
	}
	
}
