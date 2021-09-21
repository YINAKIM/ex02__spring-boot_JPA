package org.zerock.ex02.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemoRepositoryTests {

    @Autowired
    MemoRepository memoRepository;

    @Test
    public void testClass(){
        System.out.println(memoRepository.getClass().getName());  // 프록시로 생성된 클래스의 이름을 출력 : com.sun.proxy.$Proxy93 (스프링은 싱글톤으로 obj를 관리하기때문에 두줄찍어도 같은 프록시네임이 나온다)
    }
}
