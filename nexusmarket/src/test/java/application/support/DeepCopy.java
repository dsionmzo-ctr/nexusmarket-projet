package application.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reflection-based deep copy of Domain Model graphs. The in-memory ports copy on save and on read, as a
 * real database does, so services that forget to persist a change are detected by the tests.
 * Value Objects (catalogs, records), enums, strings, numbers and dates are immutable and shared.
 */
public final class DeepCopy {

    private DeepCopy() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T of(T source) {
        return (T) copy(source, new IdentityHashMap<>());
    }

    private static Object copy(Object source, Map<Object, Object> seen) {
        if (source == null || isImmutable(source)) {
            return source;
        }
        if (seen.containsKey(source)) {
            return seen.get(source);
        }
        if (source instanceof List<?> list) {
            List<Object> target = new ArrayList<>();
            seen.put(source, target);
            for (Object element : list) {
                target.add(copy(element, seen));
            }
            return target;
        }
        if (source instanceof Map<?, ?> map) {
            Map<Object, Object> target = new LinkedHashMap<>();
            seen.put(source, target);
            map.forEach((key, value) -> target.put(copy(key, seen), copy(value, seen)));
            return target;
        }
        if (source instanceof Collection<?>) {
            throw new IllegalStateException("Unsupported collection " + source.getClass());
        }
        try {
            Object target = instantiate(source.getClass());
            seen.put(source, target);
            for (Class<?> type = source.getClass(); type != null && type != Object.class; type = type.getSuperclass()) {
                for (Field field : type.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    field.setAccessible(true);
                    field.set(target, copy(field.get(source), seen));
                }
            }
            return target;
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Cannot copy " + source.getClass(), ex);
        }
    }

    private static Object instantiate(Class<?> type) throws ReflectiveOperationException {
        var constructor = type.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static boolean isImmutable(Object value) {
        Class<?> type = value.getClass();
        return value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Character
                || value instanceof Enum || value instanceof java.time.temporal.Temporal || type.isRecord()
                || type.getPackageName().equals("application.domain.valueobjects")
                || type.getName().startsWith("java.time.");
    }
}
