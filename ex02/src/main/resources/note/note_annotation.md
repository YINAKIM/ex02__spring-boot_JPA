### @Entity
1. 이 어노테이션이 붙은 클래스는 entity클래스임을 명시, 이 클래스의 인스턴스들이 JPA로 관리되는 엔티티객체임을 의미한다.                     
2. 옵션에 따라 자동으로 테이블을 생성할 수 있다. (@Table)                     
3. 2의 옵션에 따라 해당 클래스의 멤버변수에 따라 자동으로 컬럼이 생성된다                     



### @Table(name="tbl1")
1. @Entity어노테이션과 함께 사용
2. DBㅇ상에서 엔티티클래스를 어떤 테이블로 생성할 것인지에 대해 명시
3. name속성에 지정한 이름으로 테이블이 생성된다.
4. 인덱스 설정이 가능하다.                                          



### @Id, @GenerateValue
1. @Entity클래스의 멤버변수 중, 테이블의 PK가 될 변수에 @Id를 지정
2. 키 생성 전략을 사용하기 위해 @GenerateValue 사용.           
   ex) @GeneratedValue(strategy = GenerationType.IDENTITY)  : 자동으로 생성되는 값을 사용하기 위해            


      키 생성 전략
     * AUTO : default, JPA구현체(스프링부트에서는 Hibernate)가 생성방식을 결정하도록 한다.
     * IDENTITY : 사용하는 DB가 키 생성을 결정, MySQL이나 MaiaDB의 경우 auto increment방식을 이용
     * SEQUENCE : DB의 시퀀스를 이용해서 키를 생성.  @SequenceGenerator 와 함께사용
     * TABLE : 키 생성 전용 테ㅐ이블을 생성해서 키 생성.  @TableGenerator 와 함께사용                     

                     

### @Column( <> @Transient DB테이블에는 컬럼으로 생성되지 않는 필드)
1. DB에 컬럼으로 생성된다.
2. 각 컬럼에 필요한 정보를 지정하여 사용
ex) nullable, name, length 등                     


### @Builder, @AllArgsConstructor, @NoArgsConstructor
1. builder로 객체를 생성할 수 있도록 하기 위해 사용              
2. @Builder를 사용할 때 항상 같이 사용해야 컴파일에서가 발생하지 않는다.                                    


    참고   
    @NoArgsConstructor : 파라미터가 없는 (기본)생성자를 자동으로 생성한다.    
    
    @NoArgsContructor를 사용할 때 주의점           
    * 필드들이 final로 생성되어 있는 경우에는 필드를 초기화 할 수 없기 때문에 생성자를 만들 수 없고 에러발생.                         
        이 때는 @NoArgsConstructor(force = true) 옵션을 이용해서 final 필드를 0, false, null 등으로 초기화를 강제로 시켜서 생성자를 만들 수 있음.
    * @NonNull 같이 필드에 제약조건이 설정되어 있는 경우, 생성자내 null-check 로직이 생성되지 않는다.              
      후에 초기화를 진행하기 전까지 null-check 로직이 발생하지 않는 점을 염두하고 코드를 개발해야 한다.                
                     
    @RequiredArgsConstructor :  파라미터의 순서는 클래스에 있는 필드 순서에 맞춰 생성된다.             
                               초기화 되지 않은 모든 final 필드, @NonNull로 마크돼있는 모든 필드들에 대한 생성자를 자동으로 생성하는데,         
                               이때 파라미터에 null값이 들어온다면 NullPointerException발생할 수 있음.                                         
    @AllArgsConstructor : 클래스에 존재하는 모든 필드에 대한 생성자를 자동으로 생성. @NonNull이 있다면 null check로직도 자동생성.
    
    
