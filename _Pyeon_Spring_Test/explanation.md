
- graddle 위치
    - C:\Users\pkc10\.gradle\wrapper\dists\gradle-8.10.2-bin\a04bxjujx95o3nb99gddekhwo\gradle-8.10.2\bin
- Java 위치
    - C:\Users\pkc10\.jdks\openjdk-18\bin

- 스프링이 모델을 만들어서 넘기고 index.html의 `<a href="/hello">hello</a>`가 실행되면 HelloController의 String hello가 수행되며 리턴하는 "hello"가 hello.html과 연결시켜줌
- `java -jar hello-spring-0.0.1-SNAPSHOT.jar` 명령은 JAR(Java ARchive) 파일을 실행하는 명령어입니다. 이 명령어는 해당 JAR 파일에 포함된 Java 애플리케이션을 실행합니다.
- 정적 컨텐츠인 hello-static.html 요청을 보내면 스프링은 controller를 우선 조사해서 관련 컨트롤러가 있는지 확인함. 없으면 resources: static/hello-static.html을 찾아서 웹 브라우저에 반환해줌.
- mvc는 viewResolver에서 HTML을 변환 후 웹 브라우저로 넘겨줌
- api로 @ResponseBody 를 사용하면 뷰 리졸버( viewResolver )를 사용하지 않음 대신에 HTTP의 BODY에 문자 내용을 직접 반환(HTML BODY TAG를 말하는 것이 아님). 소스 검사해보면 html이 아닌 그냥 문자열만 나옴
- @ResponseBody 를 사용하고, 객체를 반환하면 객체가 JSON으로 변환됨
- *정리*
    - template 방식 - static - MVC의 view에 해당
        - Model 객체에 데이터를 추가하고, 뷰 이름(여기서는 "hello")을 반환합니다.
        - Spring은 반환된 뷰 이름을 기반으로, 설정된 ViewResolver(예: Thymeleaf, JSP)를 통해 템플릿 엔진이 렌더링할 HTML 파일을 찾습니다.
        - hello.html 템플릿 파일이 렌더링되어 클라이언트에게 전달됩니다.
    - view + Dynamic 방식 - hello-mvc
        - `@RequestParam("name")`을 사용해 URL에서 쿼리 파라미터 `name`의 값을 받아옵니다. 예: `http://localhost:8080/hello-mvc?name=John`에서 `name`의 값은 `John`입니다.
        - 받아온 데이터를 `Model` 객체에 추가하고, **뷰 이름**(여기서는 `"hello-template"`)을 반환합니다.
        - Spring은 `hello-template.html` 템플릿 파일을 찾아 렌더링하고, 모델 데이터(`name`)를 삽입합니다.
        - 동적으로 데이터를 포함한 HTML 페이지를 렌더링하는 데 사용됩니다.
    - String 방식 - hello-string
        - `@ResponseBody`가 붙어 있으므로, 반환값 `"hello " + name`은 **HTTP 응답 바디**에 그대로 포함됩니다.
        - 예: `http://localhost:8080/hello-string?name=John` 요청 시, **"hello John"** 텍스트가 반환됩니다.
        - 간단한 문자열 응답을 반환하고자 할 때 사용됩니다.
        - 템플릿이나 뷰를 사용하지 않고, 직접적인 HTTP 응답만을 보냅니다.
    - API 방식 - hello-api
        - `@ResponseBody`와 객체(`Hello`)를 반환하면, Spring은 이를 **JSON 형식**으로 변환하여 클라이언트에게 전달합니다.
        - 예: `http://localhost:8080/hello-api?name=John` 요청 시, 다음과 같은 JSON 응답이 반환됩니다:
- `Optional<Member>`는 null이 될 수 있는 객체를 감싸는 컨테이너입니다.
- `Optional`은 값이 존재하면 해당 값을 포함하고, 값이 없으면 비어 있는 상태가 됩니다.
- Test를 실행하면 @Test 메서드들간 순서를 보장해주지 못 하는 듯. 그래서 @AfterEach를 통해 매 테스트 마다 클리어 시켜줘야 쓰레기 값들이 잘못 읽히지 않는다

- @Controller를 붙이면 해당 클래스가 컨테이너에 빈으로써 포함됨
- MemberService 자체는 순수 자바 코드이기에 @Service를 붙여주어야 컨테이너에서 가져다 쓸 수 있게 됨. @Repositroy도 마찬가지임. 그 후 컨테이너를 @Service와 @Repositroy에 연결시켜줄땐 @Autowired 사용함. 이게 *Dependency Injection*임. 
- 자동으로 스프링 빈을 등록하는 스코프는 '같은 패키지 내'이다.
- 인터페이스는 new 안됨
- html 요청이 오면 컨트롤러를 먼저 찾고 없으면 static이다. 즉 컨트롤러의 우선순위가 더 높다
- 파일기반모드인 jdbc:h2:~/testdb는 사용자 홈 디렉에 testdb.mv.db 파일로 디비를 생성하므로 h2를 재시작해도 기존 디비 파일이 유지된다.
- @Test 할 때 db로 실험한 후 @Transactional 붙여주면 롤백하니 깔끔하다 (테스트 케이스에만 해당).
- SpringConfig의 DataSource는 Java JDBC API에서 제공하는 인터페이스이다. 데이터베이스 연결 풀링(Connection Pooling)과 연결 관리(Connection Management)를 간소화하기 위해 설계된 표준 인터페이스입니다. 데이터베이스 연결을 설정하고 관리하는 데 있어 기존의 DriverManager(직접 URL을 통해 연결) 방식보다 더 유연하고 효율적인 대안을 제공합니다. 연결 풀을 사용하면 매번 새 연결을 생성하고 닫는 작업이 필요 없어 성능이 크게 향상됩니다. 
- jdbcTemplateMemberRepository에서 rs는 RowMapper< Member >라는 jdbc template에서 제공하는 인터페이스이다. sql 쿼리 결과를 객체로 매핑할 떄 사용된다. 객체는 ResultSet rs이다. 즉 쿼리 결과를 나타내는 표준 인터페이스이다.
- Member 클래스에 @Entity를 붙여주면 JPA가 관리하는 엔티티가 된다. 
- GenerationType.IDENTITY 주면 디비가 알아서 ID 번호 생성해줌 
- JpaMemberRepository의 EntityManager는 Jpa에 의해 spring boot가 자동으로 생성해줌 (디비와 연결해서). 우린 이걸 injection만 하면 된다. 이 엔티티매니저로 디비와 연결되고 소통함. 즉 Jpa를 쓰려면 EntityManager를 주입 받아야함.
- JpaMemberRepository 하나로 insert query 다 날려서 디비에 집어넣고 멤버에 set id까지 다 해줌. pk기반은 알아서 해줌
```Java
em.createQuery("select m from Member m where m.name = :name", Member.class).setParameter("name", name).getResultList();
```
-  JPQL : 테이블이 아닌 Member라는 객체(Entity)를 대상으로 쿼리를 날리는거임. 그럼 이게 SQL로 번역이 됨. Member as m을 줄여서 그냥 m만 씀. 즉 객체 그 자체를 조회함. pk 기반이 아닌건 이런식으로 JPQL 써야함
- JPA는 모든 변경이 다 트랜젝션 안에서 실행되어야 함으로 MemberService에 @Transaction을 붙여줘야함
- interface가 interface 받을 땐 implements가 아니라 extends. 이때 인터페이스는 다중 상속 가능.
- SpringJPA가 JpaRepository를 extends한 SpringDataJpaMemberRepository를 자동으로 빈으로 생성해서 등록해줌. 우리는 그걸 인젝션해서 쓰기만 하면 됨. 우리가 구현해야했던 MemberRepository의 메소드들(save, findall 등)은 JpaRepository 안에 들어가 보면 여기서 다 지원해줌. 즉 기본적인 CRUD 다 지원됨
- 특히 함수 이름 작성시 룰이 있는데 findBy뒤에 Name을 붙이면 JPQL에서 select m from Member m where m.name=? 등으로 치환시켜서 인식해주는 듯. 그래서 findByNameAndID 등으로도 되는 듯.


