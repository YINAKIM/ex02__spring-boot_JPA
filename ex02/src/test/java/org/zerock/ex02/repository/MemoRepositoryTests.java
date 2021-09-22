package org.zerock.ex02.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.zerock.ex02.entity.Memo;

import javax.print.attribute.standard.PageRanges;
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

    @Test
    public void testDelete(){
        Long mno = 101L;
        memoRepository.deleteById(mno);
        /*
        Hibernate:
            select
                memo0_.mno as mno1_0_0_,
                memo0_.memo_text as memo_tex2_0_0_
            from
                tbl_memo memo0_
            where
                memo0_.mno=?
        Hibernate:
            delete
            from
                tbl_memo
            where
                mno=?
          ================================
          deleteById()는
          이렇게 select해서 결과 있으면 delete하는데
          만약 해당데이터가 존재하지 않으면? --> EmptyResultDataAccessException을 반환.

          No class org.zerock.ex02.entity.Memo entity with id 100 exists!
          org.springframework.dao.EmptyResultDataAccessException at MemoRepositoryTests.java:111
        */
    }

    // JPA 페이징처리
    @Test
    public void testPageable(){

        Pageable pageable = PageRequest.of(0,10); //1페이지 10개

        Page<Memo> result = memoRepository.findAll(pageable);

        System.out.println(result);
        // Page 1 of 10 containing org.zerock.ex02.entity.Memo instances
    /*
    Hibernate:
        select
            memo0_.mno as mno1_0_,
            memo0_.memo_text as memo_tex2_0_
        from
            tbl_memo memo0_ limit ?
    Hibernate:
        select
            count(memo0_.mno) as col_0_0_
        from
            tbl_memo memo0_
    */
    }

    // Page<Entity타입>으로 이용하는 페이징처리 관련 주요 메서드
    @Test
    public void testPageable2(){

        Pageable pageable = PageRequest.of(0,10); //1페이지 10개

        Page<Memo> result = memoRepository.findAll(pageable);
        System.out.println(result);

        System.out.println("---------------- 여기부터 paging 메서드 -------------------");
        System.out.println("총 몇페이지 ? getTotalPages()..............."+ result.getTotalPages());
        System.out.println("전체 데이터 몇건? getTotalElements()........."+ result.getTotalElements());
        System.out.println("현재 페이지번호는? (0부터 시작) getNumber()....."+ result.getNumber());
        System.out.println("한페이지에 몇건씩? getSize().................."+ result.getSize());
        System.out.println("다음페이지 존재하나? hasNext()................."+ result.hasNext());
        System.out.println("시작페이지 여부? isFirst()...................."+ result.isFirst());
        /*
            Page 1 of 10 containing org.zerock.ex02.entity.Memo instances
            ---------------- 여기부터 paging 메서드 -------------------
            총 몇페이지 ? getTotalPages()...............10
            전체 데이터 몇건? getTotalElements().........99
            현재 페이지번호는? (0부터 시작) getNumber().....0
            한페이지에 몇건씩? getSize()..................10
            다음페이지 존재하나? hasNext().................true
            시작페이지 여부? isFirst()....................true
        */
    }
}
