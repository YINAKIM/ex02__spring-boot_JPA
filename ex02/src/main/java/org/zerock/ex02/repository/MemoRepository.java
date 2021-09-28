package org.zerock.ex02.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.ex02.entity.Memo;

import javax.transaction.Transactional;
import java.util.List;

public interface MemoRepository extends JpaRepository<Memo,Long> {
// 이렇게 인터페이스 선언만 해도 스프링빈으로 등록된다(스프링이 해당인터페이스타입에 맞는 객체를 생성해서 빈으로 등록한다.)


    // 쿼리메서드 이용
    List<Memo> findByMnoBetweenOrderByMnoDesc(Long from, Long to);  //  쿼리메서드로 Mno범위에 맞는 엔티티list를 select
    Page<Memo> findByMnoBetween(Long from, Long to, Pageable pageableInfo);
    void deleteMemoByMnoLessThan(Long lassThanNum);

    // JPQL 이용
    @Query(value = "select m from Memo m order by m.mno desc")
    List<Memo> getListDesc();

    // 파라미터 바인딩
    @Transactional
    @Modifying
    @Query(value = "update Memo m set m.memoText = :memoText where m.mno = :mno")
    int updateMemoText(@Param("mno") Long mno, @Param("memoText") String memoText);
}
