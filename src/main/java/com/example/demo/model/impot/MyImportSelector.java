package com.example.demo.model.impot;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
 *
 */
public class MyImportSelector  implements ImportSelector {
    /**
     * 返回值就是要导入到容器中的组件的全类名
     * AnnotationMetadata 当前标注@Import注解类的所有元注解信息（类上所有的注解）
     */
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        // 此方法不能返回null，但是可以空数组
        return new String[]{"com.example.demo.model.impot.ImportModelTest","com.example.demo.model.entity.demo.ProductTest"};
    }

}
