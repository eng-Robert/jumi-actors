// Copyright © 2011-2014, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.actors.generator.codegen;

import java.lang.reflect.*;
import java.util.*;

public abstract class JavaType {

    public static JavaType of(Type type) {
        return JavaType.of(type, typeArgumentsOf(type));
    }

    public static JavaType of(Type type, JavaType... typeArguments) {
        if (type instanceof Class && typeArguments.length == 0) {
            return new RawType((Class<?>) type);
        }
        if (type instanceof Class && typeArguments.length > 0) {
            return new GenericType((Class<?>) type, typeArguments);
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType t = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) t.getRawType();
            return new GenericType(rawType, typeArguments);
        }
        if (type instanceof WildcardType) {
            return new GenericWildcardType();
        }
        throw new IllegalArgumentException("unsupported type " + type);
    }

    private static JavaType[] typeArgumentsOf(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType t = (ParameterizedType) type;
            return asJavaTypes(t.getActualTypeArguments());
        }
        return new JavaType[0];
    }

    private static JavaType[] asJavaTypes(Type[] args) {
        JavaType[] results = new JavaType[args.length];
        for (int i = 0; i < args.length; i++) {
            results[i] = JavaType.of(args[i]);
        }
        return results;
    }

    public abstract String getPackage();

    public abstract String getRawName();

    public abstract String getSimpleName();

    public abstract List<Class<?>> getRawTypesToImport();


    private static class RawType extends JavaType {
        private final Class<?> type;

        private RawType(Class<?> type) {
            super();
            this.type = type;
        }

        @Override
        public String getPackage() {
            return type.getPackage().getName();
        }

        @Override
        public String getRawName() {
            return type.getSimpleName();
        }

        @Override
        public String getSimpleName() {
            return type.getSimpleName();
        }

        @Override
        public List<Class<?>> getRawTypesToImport() {
            ArrayList<Class<?>> imports = new ArrayList<Class<?>>();
            imports.add(type);
            return imports;
        }
    }

    private static class GenericType extends JavaType {
        private final Class<?> type;
        private final JavaType[] typeArguments;

        private GenericType(Class<?> aClass, JavaType[] typeArguments) {
            super();
            this.type = aClass;
            this.typeArguments = typeArguments;
        }

        @Override
        public String getPackage() {
            return type.getPackage().getName();
        }

        @Override
        public String getRawName() {
            return type.getSimpleName();
        }

        @Override
        public String getSimpleName() {
            return type.getSimpleName() + "<" + typeArgumentsAsString() + ">";
        }

        private String typeArgumentsAsString() {
            String result = "";
            for (int i = 0; i < typeArguments.length; i++) {
                if (i > 0) {
                    result += ", ";
                }
                result += typeArguments[i].getSimpleName();
            }
            return result;
        }

        @Override
        public List<Class<?>> getRawTypesToImport() {
            List<Class<?>> imports = new ArrayList<Class<?>>();
            imports.add(type);
            for (JavaType typeArgument : typeArguments) {
                imports.addAll(typeArgument.getRawTypesToImport());
            }
            return imports;
        }
    }

    private static class GenericWildcardType extends JavaType {

        private GenericWildcardType() {
            super();
        }

        @Override
        public String getPackage() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRawName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSimpleName() {
            // TODO: upper and lower bounds
            return "?";
        }

        @Override
        public List<Class<?>> getRawTypesToImport() {
            return Collections.emptyList();
        }
    }
}
