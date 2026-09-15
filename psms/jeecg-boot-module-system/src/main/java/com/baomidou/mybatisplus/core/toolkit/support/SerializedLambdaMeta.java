package com.baomidou.mybatisplus.core.toolkit.support;

import com.baomidou.mybatisplus.core.toolkit.ClassUtils;

/**
 * 遮蔽（shadow）mybatis-plus-core 3.4.3.1 中的同名类。
 *
 * 原类的静态块通过反射访问 java.lang.invoke.SerializedLambda 的私有字段
 * capturingClass 并 setAccessible，在 JDK 16+ 上被模块系统禁止（InaccessibleObjectException），
 * 导致类初始化失败，所有 Lambda 条件构造器查询报
 * NoClassDefFoundError: Could not initialize class ...SerializedLambdaMeta。
 *
 * 本类与原类行为一致，但只用 SerializedLambda 的公开方法（JDK 8 起可用），
 * 因此在 JDK 8 / JDK 21 上均可正常运行，无需 --add-opens 启动参数。
 *
 * 注意：本类位于应用模块（BOOT-INF/classes / IDEA 模块输出），类加载顺序
 * 先于依赖 jar（BOOT-INF/lib），故优先于 mybatis-plus-core 中的原类被加载。
 * 若升级 mybatis-plus 版本，请删除本类。
 */
public class SerializedLambdaMeta implements LambdaMeta {

    private final java.lang.invoke.SerializedLambda lambda;

    public SerializedLambdaMeta(java.lang.invoke.SerializedLambda lambda) {
        this.lambda = lambda;
    }

    @Override
    public String getImplMethodName() {
        return lambda.getImplMethodName();
    }

    @Override
    public Class<?> getInstantiatedClass() {
        String type = lambda.getInstantiatedMethodType();
        String name = type.substring(2, type.indexOf(';')).replace('/', '.');
        return ClassUtils.toClassConfident(name, getCapturingClass().getClassLoader());
    }

    /**
     * 原实现通过反射读取私有字段；此处改用公开方法 getCapturingClass()，
     * 其返回 VM 内部类名（如 com/example/Foo，斜杠分隔）。
     */
    public Class<?> getCapturingClass() {
        String cn = lambda.getCapturingClass();
        return ClassUtils.toClassConfident(cn.replace('/', '.'));
    }
}