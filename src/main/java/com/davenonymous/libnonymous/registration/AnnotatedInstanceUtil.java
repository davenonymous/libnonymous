package com.davenonymous.libnonymous.registration;

import com.davenonymous.libnonymous.Libnonymous;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * This code is fully copied over from Just Enough Items
 * https://github.com/mezz/JustEnoughItems/blob/1.18/src/main/java/mezz/jei/util/AnnotatedInstanceUtil.java
 *
 * Many thanks Mezz!
 *
 * ==========================
 *
 * This files copyright is and cannot be changed:
 *
 * The MIT License (MIT)
 * Copyright (c) 2014-2015 mezz
 */

public final class AnnotatedInstanceUtil {
	@SuppressWarnings("SameParameterValue")
	public static <T> List<T> getInstances(Class<?> annotationClass, Class<T> instanceClass) {
		Type annotationType = Type.getType(annotationClass);
		List<ModFileScanData> allScanData = ModList.get().getAllScanData();

		Set<String> pluginClassNames = new LinkedHashSet<>();
		for (var scanData : allScanData) {
			for (var a : scanData.getAnnotations()) {
				if (!Objects.equals(a.annotationType(), annotationType)) {
					continue;
				}

				if(a.annotationData().containsKey("mod")) {
					String requiredMod = (String)a.annotationData().get("mod");
					if(!ModList.get().isLoaded(requiredMod)) {
						continue;
					}
				}

				String memberName = a.memberName();
				pluginClassNames.add(memberName);
			}
		}


		List<T> instances = new ArrayList<>();
		for (String className : pluginClassNames) {
			try {
				Class<?> asmClass = Class.forName(className);
				Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
				Constructor<? extends T> constructor = asmInstanceClass.getDeclaredConstructor();
				T instance = constructor.newInstance();
				instances.add(instance);
			} catch (ReflectiveOperationException | LinkageError e) {
				Libnonymous.LOGGER.error("Failed to load: {}", className, e);
			}
		}
		return instances;
	}
}