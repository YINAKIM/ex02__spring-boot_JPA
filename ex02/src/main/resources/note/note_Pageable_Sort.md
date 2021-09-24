## JPA에서 페이징처리

public interface Pageable의 구현체인 PageRequest를 이용해 페이징처리한다.

#### org.springframework.data.domain.Pageable
[용도] Pageable은 인터페이스, 페이징처리에 필요한 정보를 전달하는 용도     
[구현체] 구현체인 PageRequest로 사용하는데, 생성자가 protected PageRequest()다      
[객체생성] PageRequest.of(page,size,sort) 로 객체생성 : static 메서드인 of()로 객체생성한다.   


* public static PageRequest of()의 오버로딩메서드
  * of(int page, int size)   
    : 0부터 시작(제로베이스)하는 페이지번호, 한페이지당 개수, 정렬은지정X   
       
  * of(int page, int size, Sort sort)   
    : 0부터시작, 한페이지당 개수, 정렬 관련정보 

  * of(int page, int size, Sort.Direction direction, String... properties)    
    : 0부터시작, 한페이지당 개수, 정렬 방향과 정렬기준 필드들   
  
```
Pageable pageable = PageRequest.of(0,10); //1페이지 10개   
Page<Memo> result = memoRepository.findAll(pageable);   
```
##

## Page< Type > 의 메서드로 페이징관련 정보 가져오기 

```
Pageable pageable = PageRequest.of(0,10); //1페이지 10개   
Page<Memo> result = memoRepository.findAll(pageable);   

result.getTotalPages();       // getTotalPages : 총 몇페이지?
result.getTotalElements();    // getTotalElements : 전체데이터는 몇건? 
result.getNumber();           // getNumber : 현재 페이지번호는? 
result.getSize();             // getSize : 한페이지에 몇건씩?
result.hasNext();       // hasNext : 다음페이지가 존재하는지 ? true/false
result.isFirst();       // isFirst : 현재 시작페이지인지(current Slice의 시작페이지여부) ? true/false
```
##




     
## Sort로 정렬, sort1.and(sort2)로 정렬조건 결합
* Sort객체로 정렬조건을 지정해서 ***PageRequest.of(page,size,sort)*** 에 지정할수있다.
* 여러개의 Sort객체로 정렬조건을 지정하고, and()를 이용해 결합하면 여러개의 조건을 지정할 수 있다.


```
Sort sort1 = Sort.by("mno").descending();
Sort sort2 = Sort.by("memoText").ascending();
Sort sortAll = sort1.and(sort2); 

Pageable pageable = PageRequest.of(0,10,sortAll); // and로 결합시킨 정렬조건을 적용
Page<Memo> result = memoRepository.findAll(pageable);
```
##