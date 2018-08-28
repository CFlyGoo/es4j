/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apehat.es4j.util;

import com.apehat.es4j.NestedIOException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class AsmParameterNameDiscoverer implements ParameterNameDiscoverer {

    private static final String THIS = "this";

    private static final int ASM_API = Opcodes.ASM6;

    @Override
    public String[] getParameterNames(Executable exec) {
        final int count = exec.getParameterCount();
        if (count == 0) {
            return new String[0];
        }
        ArrayList<String> paramNames = new ArrayList<>(count);
        final Class<?> declaringClass = exec.getDeclaringClass();
        final ClassReader cr;
        try {
            cr = new ClassReader(declaringClass.getName());
        } catch (IOException e) {
            throw new NestedIOException(e);
        }
        cr.accept(new ClassVisitor(ASM_API) {
            @Override
            public MethodVisitor visitMethod(
                int access, String name, String descriptor, String signature, String[] exceptions) {
                if (!getExecDescriptor(exec).equals(descriptor)) {
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
                return new MethodVisitor(ASM_API) {
                    @Override
                    public void visitLocalVariable(String localVarName, String descriptor,
                        String signature, Label start, Label end, int index) {
                        if (THIS.equals(localVarName)) {
                            return;
                        }
                        if (paramNames.size() < count) {
                            paramNames.add(localVarName);
                        }
                    }
                };
            }
        }, 0);
        return paramNames.toArray(new String[0]);
    }

    private String getExecDescriptor(Executable exec) {
        return (exec instanceof Method) ?
            Type.getMethodDescriptor((Method) exec) :
            Type.getConstructorDescriptor((Constructor<?>) exec);
    }
}
