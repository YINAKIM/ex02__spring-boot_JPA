JPA에서 쿼리 이용하기  쿼리메서드 / @Query / Querydsl
#### 한줄요약
* 쿼리메서드 : 메서드의 이름 자체가 쿼리의 구문으로 처리됨, 정해진 키워드를 넣고 메서드이름을 만들면 쿼리실행됨 (findBy, getBy, Between, OrderBy, Desc등등)
* @Query : (필드 등의 엔티티정보를 이용 + 사용하는DB에 맞는 고유SQL문 작성) 하는 방식으로 value에 쿼리작성, 다양한 속성값 추가 가능
* Querydsl : 동적쿼리기능(Part2에서 자세히)




## 1.쿼리메서드
메서드의 이름 자체가 쿼리의 구문으로 처리됨, But 간단한 처리에만 이용, (findBy~ getBy~ deleteBy~)
* findBy, getBy, Between, OrderBy, Desc등등 의 키워드로 메서드 이름을 만든다. 키워드에 따라 파라미터 개수도 지정되어있음 (docs참고)   
  https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation


* select작업시 List나 배열로 받을 수 있다.   
  `List<Memo> findByMnoBetweenOrderByMnoDesc(Long from, Long to);`


* 페이징 : Pageable타입 파라미터를 받고, Page<E>타입으로 리턴   
  `Page<Memo> findByMnoBetween(Long from, Long to, Pageable pageableInfo); `


* delete처리시 1건씩 select > delete 하므로 @Transactional사용(안쓰면 cannot reliably process 'remove' call), 근데 쿼리메서드는 일괄삭제 안된서 잘 안씀

* 쿼리메서드는 메서드명이 쿼리라서 쿼리키워드가 사용된다. 따라서 join등의 복잡한 쿼리 사용은 불편하다. 그래서 쿼리메서드는 간단한 처리에만 사용하고, @Query를 사용하는 경우가 더 많음




## @Query 어노테이션 사용
* 필요한 데이터만 선별적으로 추출 가능 
* DB에 맞는 순수 SQL사용 가능
* insert, update, delete등의 DML(select제외) 처리 - @Modifying과 함께 사용
* 메서드이름과 상관X, 추가한 어노테이션으로 원하는 처리 
* *JPQL*로 value 작성  
```
    @Query(value = "select m from Memo m order by m.mno desc") 
    List<Memo> getListDesc();
```
##### JPQL : 객체지향 쿼리(Java Persistence Query Language) 
1. 테이블 대신에 Entity클래스를 이용
2. 컬럼 대신 Entity클래스에 선언된 필드를 이용해서 작성
3. SQL에서 사용되는 함수들도 동일하게 사용 가능 : AVG(), COUNT(), GROUP BY, ORDER BY 등

##### @Query의 파라미터 바인딩 : where구문에 들어가는 파라미터 처리하기
* '?1, ?2' :  1부터 시작하는 파라미터 순서를 이용하는 방식 
* ':xxx' :  ':파라미터이름'을 활용하는 방식
* '#{}' :  자바빈 스타일 이용

  ```
  // 파라미터 바인딩
  @Transactional
  @Modifying
  @Query(value = "update Memo m set m.memoText = :memoText where m.mno = :mno")
  int updateMemoText(@Param("mno") Long mno, @Param("memoText") String memoText);```