package com.example.demo.service.interfaceAndImp;

import org.springframework.stereotype.Service;


/*
 *建议用@Service加别名
 *如果类用@Component做注解而不是@Service，就不用了指定别名。
 * 如果用@Service注解，多个类实现接口时候就要指定别名：声明@Service("InterfaceTestImpB")，调用 @Autowired、 @Qualifier("InterfaceTestImpA")
 */
//@Service
@Service("InterfaceTestImpA")
//@Component
public class InterfaceTestImpA implements InterfaceTest{

    @Override
    public String fun() {
        return "InterfaceTestImpA";
    }
}
