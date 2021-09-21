package org.zerock.ex02.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.ex02.entity.Memo;

import java.util.stream.IntStream;

@SpringBootTest
public class MemoRepositoryTests {

    @Autowired
    MemoRepository memoRepository;

    @Test
    public void testClass(){
        System.out.println(memoRepository.getClass().getName());  // 프록시로 생성된 클래스의 이름을 출력 : com.sun.proxy.$Proxy93 (스프링은 싱글톤으로 obj를 관리하기때문에 두줄찍어도 같은 프록시네임이 나온다)
    }

    @Test
    public void testInsertDummies(){
        //100개의 새로운 Memo객체를 생성, MempRepository를 이용해서 Insert

        IntStream.rangeClosed(1,100).forEach(i->{
            Memo memo = Memo.builder().memoText("sample..."+i).build();  // Memo에서 memoText는 notnull이므로 반드시 값을 넣어준다.
            memoRepository.save(memo);
        });

        /*
            JPA의 구현체인 Hibernate가 발생시키는 Insert구문을 돌린다. 로그에서 확인해보면
            Hibernate:
                insert
                into
                    tbl_memo
                    (memo_text)
                values
                    (?)
            Hibernate:
                insert
                into
                    tbl_memo
                    (memo_text)
                values
                    (?)
           Hibernate:
                insert
                into
                    tbl_memo
                    (memo_text)
                values
                    (?)
            .
            .
            .
            .
            이렇게 100번 돌린다.
            -----------------------
            DB조회해보면
            mno = 1, memoText = "sample...1"
            .
            .
            .
            mno = 100, memoText = "sample...100"  까지 들어가있음

        */
    }





}
