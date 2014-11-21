// Copyright © 2011-2014, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.actors.generator;

import fi.jumi.actors.generator.codegen.JavaType;

public class TargetPackageResolver {

    private final String targetPackage;

    public TargetPackageResolver(String targetPackage) {
        this.targetPackage = targetPackage;
    }

    public String getEventizerPackage() {
        return targetPackage;
    }

    public String getStubsPackage(JavaType listenerInterface) {
        // TODO: should we support type-parameterized interfaces?
        return targetPackage + "." + toLowerCamelCase(listenerInterface.getSimpleName());
    }

    private static String toLowerCamelCase(String s) {
        String head = s.substring(0, 1);
        String tail = s.substring(1);
        return head.toLowerCase() + tail;
    }
}
