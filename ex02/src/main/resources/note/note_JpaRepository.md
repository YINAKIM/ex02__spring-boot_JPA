### JpaRepository 

* Spring Data JPA에는 여러 종류의 인터페이스의 기능을 통해서 CRUD, 페이징, 정렬 등의 처리를 메서드호출로 처리할 수 있다.
* JpaRepository를 상속받는 것 만으로 모든 기능을 사용할 수 있다.
* (참고)CrudRepository : 일반적인 기능만 이용할 경우 사용할 수 있음 

---

### CRUD 작업별 이용하는 메서드

* insert 작업 : save(엔티티객체)
* select 작업 : findByIf(키 타입), getOne(키 타입)
* update 작업 : save(엔티티객체)
* delete 작업 : deleteById(키 타입), delete(엔티티객체)

[참고]    
insert와 update작업에 동일한 save()를 이용한다. 
JPA의 구현체가 메모리상에서 객체를 비교하고, 없다면 inert, 존재한다면 update를 동작시키는 방식으로 동작하기때문.