package org.zerock.ex02.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.ex02.entity.Memo;

public interface MemoRepository extends JpaRepository<Memo,Long> {
// 이렇게 인터페이스 선언만 해도 스프링빈으로 등록된다(스프링이 해당인터페이스타입에 맞는 객체를 생성해서 빈으로 등록한다.)


}
