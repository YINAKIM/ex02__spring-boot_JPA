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
