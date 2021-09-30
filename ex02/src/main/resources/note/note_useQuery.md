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
  
##### @Query이용시 페이징처리
* Pageable타입 파라미터 적용하여 페이징처리, 정렬 가능 
* Pageable 파라미터를 받으면 Page<Entity> 타입으로 리턴 : count를 계산하는 쿼리 필수! 
* @Query이용할 때는 countQuery 속성을 적용하여 Pageable타입 파라미터를 전달
```  
    //@Query이용시 페이징처리 : countQuery속성 이용, Pageable파라미터 & Page<Entity>로 리턴 
    @Query(value = "select m from Memo m where m.mno > :mno"
            ,countQuery = "select count(m) from Memo m where m.mno > :mno")
    Page<Memo> getListWithQuery(Long mno, Pageable pageableInfo);
```

##### @Query이용시 결과로 받을 수 있는 형태
* 쿼리메서드의 경우, Entity타입 데이터만 추출
* @Query이용시, 현재 필요한 데이터만을 Object[]의 형태로 선별추출 가능
* JPQL이용시, JOIN, GROUP BY등으로 여러 테이블(Entity)를 참조해서 하나의 결과를 받아와야 할 경우가 있음
  : 이럴 경우, 적당한 Entity타입이 존재하지 않음 --> Object[] 타입을 리턴타입으로 지정


[ex] mno, memoText, 현재시간 을 한번에 가져와야 한다면?    
**Memo엔티티클래스**에 시간과 관련된 부분이 필드로 선언되어있지 않으면 필드를 추가해야한다.   
즉, 필요한 값이 생길 때 마다 테이블에 컬럼을 추가하는 꼴   

**JPQL에서 제공하는 CURRENT_DATE, CURRENT_TIME,CURRENT_TIMESTAMP 등의 구문을 활용, 현재 DB의 시간을 구할 수 있다.** 
```
    //필요한 데이터만을 Object[]의 형태로 
    @Query(value = "select m.mno, m.memoText, CURRENT_DATE from Memo m where m.mno > :mno"
    ,countQuery = "select count(m) from Memo m where m.mno > :m.mno")
    Page<Object[]> getListWithQueryObject(Long mno, Pageable pageableInfo);
```



##### Native SQL처리
* @Query를 이용하는 강력한 이점, DB고유의 SQL구문을 그대로 활용할 수 있다!
* @Query의 속성으로 **nativeQuery = true** 지정하고 사용
* 복잡한 JOIN구문 등을 처리하기 위해 꼭 필요한 경우 사용
* JPA가 원래 DB에 독립적으로 구현 가능하다는장점을 버리고 이용하기 때문
```

```
