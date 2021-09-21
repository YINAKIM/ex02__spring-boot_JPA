package org.zerock.ex02.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.ex02.entity.Memo;

import javax.transaction.Transactional;
import java.util.Optional;
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
    }

    @Test
    public void testSelect1(){ // use findById() : 있는지 확인 후 객체 바로 읽어옴
        Long mno = 100L;

        Optional<Memo> result = memoRepository.findById(mno);

        System.out.println("==========================================");

        if(result.isPresent()){
            Memo memo = result.get();
            System.out.println(memo);
        }
    }


    @Transactional
    @Test
    public void testSelect2(){ // use getById() (getOne() is deprecated from JPA 2.5.4)
        Long mno = 100L;

        Memo memo = memoRepository.getById(mno); //일단 참조값을 반환,

        System.out.println("==========================================");
        System.out.println(memo);// id 이외의 값에 대한 요청이 들어오면 그때 객체를 반환한다.
    }

    @Test
    public void testUpdate(){
        Memo memo = Memo.builder().mno(101L).memoText("update Test by save()").build();
        System.out.println(memoRepository.save(memo));
        /*
        JpaRepository.save(엔티티객체)는 내부적으로 해당엔티티의 존재여부(@Id값의 일치여부) 먼저 확인 후,
        존재하면(일치하면) --> UPDATE 실행
        존재하지 않으면 (@Id와 일치하지 않으면) ---> INSERT실행

        [ UPDATE 실행된 log : 100L로 save() 실행]
        Hibernate:
            select
                memo0_.mno as mno1_0_0_,
                memo0_.memo_text as memo_tex2_0_0_
            from
                tbl_memo memo0_
            where
                memo0_.mno=?
        Hibernate:
            update
                tbl_memo
            set
                memo_text=?
            where
                mno=?
        Memo(mno=100, memoText=update Test by save())



        [ INSERT 실행된 log : 101L로 save() 실행]
        Hibernate:
            select
                memo0_.mno as mno1_0_0_,
                memo0_.memo_text as memo_tex2_0_0_
            from
                tbl_memo memo0_
            where
                memo0_.mno=?
        Hibernate:
            insert
            into
                tbl_memo
                (memo_text)
            values
                (?)
        Memo(mno=101, memoText=update Test by save())  ---> 101번 Id값이 없기때문에 insert쿼리가 실행됨
        */
    }




}
