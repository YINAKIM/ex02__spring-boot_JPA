package org.zerock.ex02.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.zerock.ex02.entity.Memo;

import javax.transaction.Transactional;
import java.util.List;
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

    @Test
    public void testSort(){
        Sort sort1 = Sort.by("mno").descending(); //Sort타입으로 필드, 조건 지정하여 정렬조건 정의

        Pageable pageable = PageRequest.of(0,10,sort1); // Sort타입 파라미터를 PageRequest에 정렬조건으로 보냄
        Page<Memo> result = memoRepository.findAll(pageable);

        result.get().forEach(memo -> {
            System.out.println(memo);
        });
            /*
            Hibernate:
                select
                    memo0_.mno as mno1_0_,
                    memo0_.memo_text as memo_tex2_0_
                from
                    tbl_memo memo0_
                order by
                    memo0_.mno desc limit ?  // --> order by절 추가됨
            Hibernate:
                select
                    count(memo0_.mno) as col_0_0_
                from
                    tbl_memo memo0_
            Memo(mno=99, memoText=sample...99)
            Memo(mno=98, memoText=sample...98)
            Memo(mno=97, memoText=sample...97)
            Memo(mno=96, memoText=sample...96)
            Memo(mno=95, memoText=sample...95)
            Memo(mno=94, memoText=sample...94)
            Memo(mno=93, memoText=sample...93)
            Memo(mno=92, memoText=sample...92)
            Memo(mno=91, memoText=sample...91)
            Memo(mno=90, memoText=sample...90) // desc정렬됨
            */
    }

    @Test
    public void testSortAnd(){
        Sort sort1 = Sort.by("mno").descending();
        Sort sort2 = Sort.by("memoText").ascending();
        Sort sortAll = sort1.and(sort2); // and()를 이용해서 정렬조건 연결

        Pageable pageable = PageRequest.of(0,10,sortAll); // and로 결합시킨 정렬조건을 적용
        Page<Memo> result = memoRepository.findAll(pageable);

        result.get().forEach(memo -> {
            System.out.println(memo);
        });
            /*
                Hibernate:
                    select
                        memo0_.mno as mno1_0_,
                        memo0_.memo_text as memo_tex2_0_
                    from
                        tbl_memo memo0_
                    order by
                        memo0_.mno desc,                    //--------------> mno desc (sort1조건)
                        memo0_.memo_text asc limit ?        //--------------> memoText asc (sort2조건)
            */
    }

    /**
     * 쿼리메서드로 엔티티객체 List select
     * 쿼리키워드: findBy, Between, OrderBy, Desc
     */
    @Test
    public void testQueryMethods(){
        List<Memo> list = memoRepository.findByMnoBetweenOrderByMnoDesc(70L,80L);

        for(Memo memo:list){
            System.out.println(memo);
        }
        /*
            Hibernate:
                select
                    memo0_.mno as mno1_0_,
                    memo0_.memo_text as memo_tex2_0_
                from
                    tbl_memo memo0_
                where
                    memo0_.mno between ? and ?
                order by
                    memo0_.mno desc  // ------> findBy로 시작하면서, BETWEEN과 ORDER BY를 사용한 메서드이름대로 쿼리가 실행됨
            Memo(mno=80, memoText=sample...80) // 80번부터 Desc해서 읽어옴
            Memo(mno=79, memoText=sample...79)
            Memo(mno=78, memoText=sample...78)
            Memo(mno=77, memoText=sample...77)
            Memo(mno=76, memoText=sample...76)
            Memo(mno=75, memoText=sample...75)
            Memo(mno=74, memoText=sample...74)
            Memo(mno=73, memoText=sample...73)
            Memo(mno=72, memoText=sample...72)
            Memo(mno=71, memoText=sample...71)
            Memo(mno=70, memoText=sample...70) //---> List<Memo엔티티 객체>로 리턴,
        */
    }


    /**
     * 쿼리메서드로 페이징처리하기
     * 쿼리키워드: findBy, Between
     * [1] vs [2] 결과log보고 어떻게 가져오는지 이해할 것
     */
    @Test
    public void testQueryMethodWithPageable(){

        // [1] asc
        Pageable pageableInfoAsc = PageRequest.of(0,8,Sort.by("mno").ascending());  //--> 0페이지부터, 1페이지에 10건씩, mno acs로 정렬해서 페이징하라는 PageableInfoAsc
        Page<Memo> result = memoRepository.findByMnoBetween(10L,50L, pageableInfoAsc);  // --> pageableInfo에서 asc했으니까 from인 10번부터 1페이지(사이즈 8건짜리:10~17) 먼저 읽어옴

        result.get().forEach(memo -> { System.out.println(memo); });
        /********************************************************************************
            Hibernate:
                select
                    memo0_.mno as mno1_0_,
                    memo0_.memo_text as memo_tex2_0_
                from
                    tbl_memo memo0_
                where
                    memo0_.mno between ? and ?
                order by
                    memo0_.mno asc limit ?
            Hibernate:
                select
                    count(memo0_.mno) as col_0_0_
                from
                    tbl_memo memo0_
                where
                    memo0_.mno between ? and ?
            Memo(mno=10, memoText=sample...10)  asc했으니까 from인 10번부터 1페이지먼저 읽어옴
            Memo(mno=11, memoText=sample...11)
            Memo(mno=12, memoText=sample...12)
            Memo(mno=13, memoText=sample...13)
            Memo(mno=14, memoText=sample...14)
            Memo(mno=15, memoText=sample...15)
            Memo(mno=16, memoText=sample...16)
            Memo(mno=17, memoText=sample...17) (size 8건짜리:10~17)
        *******************************************************************************/




        // [2] desc
        Pageable pageableInfoDesc = PageRequest.of(0,10,Sort.by("mno").descending());  //--> 0페이지부터, 1페이지에 10건씩, mno decs로 정렬해서 페이징하라는 PageableInfoDesc
        Page<Memo> resultDesc = memoRepository.findByMnoBetween(10L,50L, pageableInfoDesc);  // --> pageableInfo에서 desc했으니까 to인 50번부터 1페이지(사이즈10건짜리:50~41) 먼저 읽어옴

        resultDesc.get().forEach(memo -> { System.out.println(memo); });
        /*******************************************************************************
            Hibernate:
                select
                    memo0_.mno as mno1_0_,
                    memo0_.memo_text as memo_tex2_0_
                from
                    tbl_memo memo0_
                where
                    memo0_.mno between ? and ?
                order by
                    memo0_.mno desc limit ?
            Hibernate:
                select
                    count(memo0_.mno) as col_0_0_
                from
                    tbl_memo memo0_
                where
                    memo0_.mno between ? and ?
            Memo(mno=50, memoText=sample...50)  desc했으니까 to인 50번부터 1페이지 먼저 읽어옴
            Memo(mno=49, memoText=sample...49)
            Memo(mno=48, memoText=sample...48)
            Memo(mno=47, memoText=sample...47)
            Memo(mno=46, memoText=sample...46)
            Memo(mno=45, memoText=sample...45)
            Memo(mno=44, memoText=sample...44)
            Memo(mno=43, memoText=sample...43)
            Memo(mno=42, memoText=sample...42)
            Memo(mno=41, memoText=sample...41)  (size10건 짜리니까 :50~41)
        *******************************************************************************/
    }


    /**
     * 쿼리메서드로 삭제하기
     * 키워드: deleteBy
     * 어노테이션: @Transactional, @Commit(테스트코드는 이거 안쓰면 rollback이 default라서)
     */
    @Transactional
    @Commit
    @Test
    public void testDeleteQueryMethod(){
        memoRepository.deleteMemoByMnoLessThan(5L); // 5번미만은 삭제

        /**** 정상실행log *******************************
         Hibernate:
         select
         memo0_.mno as mno1_0_,
         memo0_.memo_text as memo_tex2_0_
         from
         tbl_memo memo0_
         where
         memo0_.mno<?
         ----------------------> 일단 구간select 해옴
         Hibernate:
         delete
         from
         tbl_memo
         where
         mno=?
         .
         .
         . 이렇게 5번의 delete쿼리가 실행된다 ( 1건씩 delete )
         .
         .
         Hibernate:
         delete
         from
         tbl_memo
         where
         mno=?

         deleteBy 쿼리메서드로 delete하면 이렇게 일단 구간을 select해온 후 "1건씩 delete" 하기때문에 일괄삭제가 안된다.
        ***********************************************/


        /**** 그리고, 잘못된 상황 log 참고 ******************************************************************************
        [1] @Transactional 사용안하면? ERROR 로그 확인하기
        Hibernate:
        select
        memo0_.mno as mno1_0_,
        memo0_.memo_text as memo_tex2_0_
        from
        tbl_memo memo0_
        where
        memo0_.mno<?

         org.springframework.dao.InvalidDataAccessApiUsageException: No EntityManager with actual transaction available for current thread - cannot reliably process 'remove' call;...
         Caused by: javax.persistence.TransactionRequiredException: No EntityManager with actual transaction available for current thread - cannot reliably process 'remove' call
         .
         .
         1 test completed, 1 failed
         > Task :test FAILED


        [2] @Commit안하고 test코드 돌리면? 5건만 테스트 후 DB조회 해보기
         2021-09-26 11:53:29.591  INFO 3589 --- [    Test worker] o.s.t.c.transaction.TransactionContext   : Began transaction (1) for test context
         [DefaultTestContext@4d3d9b6d
            testClass = MemoRepositoryTests, testInstance = org.zerock.ex02.repository.MemoRepositoryTests@57b61cd7, testMethod = testDeleteQueryMethod@MemoRepositoryTests, testException = [null],
            mergedContextConfiguration =
            [
                 WebMergedContextConfiguration@70b60f5c testClass = MemoRepositoryTests, locations = '{}', classes = '{class org.zerock.ex02.Ex02Application}', contextInitializerClasses = '[]',
                 activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}',
                 contextCustomizers = set[
                                         org.springframework.boot.test.autoconfigure.actuate.metrics.MetricsExportContextCustomizerFactory$DisableMetricExportContextCustomizer@23be09f9,
                                         .
                                         .
                                         .
                                         .
                                        ],
                 attributes = map[
                                 'org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true,
                                 .
                                 .
                                 .
                                 'org.springframework.test.context.event.ApplicationEventsTestExecutionListener.recordApplicationEvents' -> false
                                 ]
            ];
         transaction manager [org.springframework.orm.jpa.JpaTransactionManager@1dba10c0];
         rollback [true]
         -------------------------------------------------(rollbak된거 확인!!)

         Hibernate:
         select
         memo0_.mno as mno1_0_,
         memo0_.memo_text as memo_tex2_0_
         from
         tbl_memo memo0_
         where
         memo0_.mno<?
         2021-09-26 11:53:29.823  INFO 3589 --- [    Test worker] o.s.t.c.transaction.TransactionContext   : Rolled back transaction for test: .....-------------(rollbak된거 확인!!)


         2021-09-26 11:53:29.840  INFO 3589 --- [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
         2021-09-26 11:53:29.843  INFO 3589 --- [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
         2021-09-26 11:53:29.848  INFO 3589 --- [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
         BUILD SUCCESSFUL in 7s
         4 actionable tasks: 2 executed, 2 up-to-date
         11:53:29 오전: Task execution finished ':test --tests "org.zerock.ex02.repository.MemoRepositoryTests.testDeleteQueryMethod"'.

         --->  application에서는 에러없이실행된거같지만 DB가서 조회해보면 삭제 안되고 rollback되어있음
        **********************************************************************************/
    }


}
