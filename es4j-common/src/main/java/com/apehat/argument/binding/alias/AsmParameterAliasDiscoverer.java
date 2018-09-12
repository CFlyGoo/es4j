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

package com.apehat.argument.binding.alias;

import com.apehat.ConcurrentCache;
import com.apehat.NestedIOException;
import com.apehat.util.ReflectionUtils;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
public class AsmParameterAliasDiscoverer implements ParameterAliasDiscoverer {

    private static final String THIS = "this";

    private static final int ASM_API = Opcodes.ASM6;

    private static final ConcurrentCache<Executable, ArrayList<String>> CACHE =
        new ConcurrentCache<>(16);

    @Override
    public String getAlias(Parameter param) {
        return getAlias(param.getDeclaringExecutable())
            [ReflectionUtils.getParameterIndex(param)];
    }

    public String[] getAlias(Executable exec) {
        final int count = exec.getParameterCount();
        if (count == 0) {
            return new String[0];
        }
        ArrayList<String> aliases = CACHE.get(exec);
        if (aliases == null) {
            aliases = resolve(exec);
        }
        return aliases.toArray(new String[0]);
    }

    private ArrayList<String> resolve(Executable exec) {
        final int count = exec.getParameterCount();
        final ArrayList<String> aliases = new ArrayList<>(count);
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
                int access, String name, String descriptor, String signature,
                String[] exceptions) {
                if (!execDescriptor(exec).equals(descriptor)) {
                    return super
                        .visitMethod(access, name, descriptor, signature, exceptions);
                }
                return new MethodVisitor(ASM_API) {
                    @Override
                    public void visitLocalVariable(String localVarName, String descriptor,
                        String signature, Label start, Label end, int index) {
                        if (THIS.equals(localVarName)) {
                            return;
                        }
                        if (aliases.size() < count) {
                            aliases.add(localVarName);
                        }
                    }
                };
            }
        }, 0);
        return aliases;
    }

    private String execDescriptor(Executable exec) {
        return (exec instanceof Method) ?
            Type.getMethodDescriptor((Method) exec) :
            Type.getConstructorDescriptor((Constructor<?>) exec);
    }
}
