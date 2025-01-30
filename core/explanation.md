
- artifact : 프로젝트 빌드명이다
- coretto17되어 있는거 내가 openjdk18로 project 설정했고, gradle도 gradle jvm이 coretto17이였는데 JAVA_HOME으로 변경했음

- 인터페이스와 그것의 구현체는 다른 패키지에 넣는게 좋으나, 간단한거니 그냥 같이 넣음(MemberRepository)
- HashMap도 원래는 동시성 문제가 있을 수 있어서  concurrent HashMap을 써야하긴 함
- MemberServiceImpl이 MemberService 인터페이스를 구현한 클래스고, 얘가 메모리 회원 저장소인 MemoryMemberRepository 객체를 생성하고 호출한다.
- MemberServiceImpl에서 join을 호출하면 MemberRepository라는 인터페이스가 아닌(당연히도) MemoryMemberRepository에 있는 오버라이딩된 save를 호출함
- MemberServiceImpl이 추상화인 MemberRepository와 구체화인 MemoryMemberRepository 모두에 의존한다는 문제점이 있다. DIP를 위반
- 주문과 할인 도메인 개발
    - DiscountPolicy 라는 인터페이스를 만들고
    - 거기에 정액할인정책과 정률할인정책이란 구현체를 만들어서
    - 필요에 따라 바꿔 끼울 수 있다.
- JUnit으로 테스트할 수 있다
    - 이때 sout으로 프린트하는게 아니라
    - Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
    - 을 이용해서 테스트해야 편함
    - 이때 Assertions는 static import를 하면 생략가능함
    - *ctrl+shift+T* 누르면 자동으로 패키지생성후 test 만들어 줌
- JUnit5에는 @DisaplayName으로 한글로 입력할 수 있음
    - 테스트 케이스는 given/when/then으로 작성하는게 좋다.
    - 테스트 코드에서 @BeforeEach 는 각 테스트를 실행하기 전에 호출된다
- Maven/gradle 프로젝트에서 아래의 구조와 같이 main/test에서 시작하는 패키지의 구조가 같은 경우, 패키지정보를 공유하기 때문에 import하지 않아도 참조하여 사용가능합니다.
    - 구조 
        src/
        |-- main/
        |-- java/
        |-- hello/
        |-- [MemberService.java](http://memberservice.java/)
        |-- test/
        |-- java/
        |-- hello/
        |-- [MemberServiceTest.java](http://memberservicetest.java/)
- IDE 편의 기능을 통해서 보더라도, 클래스를 호출하려할 때의 경로가 main/test를 가리지 않음을 확인할 수 있습니다.
- *Build Tools>Gradle에서 Intellij로 둘 다 바꿨음*
    - 한글 깨짐 문제 해결
    - Gradle의 deprecated warning 사라지긴 함
    - 나중에 문제되면 다시 Gradle로 변경하기
- 클래스 의존관계를 분석
    - orderServiceImpl은 DiscountPolicy라는 인터페이스에 의존한다.
    - 그러나 추상(인터페이스) 뿐만 아니라 구체(구현) 클래스에도 의존하고 있다
    - 추상(인터페이스) 의존: DiscountPolicy 
    - 구체(구현) 클래스: FixDiscountPolicy , RateDiscountPolicy
    - 지금 코드는 기능을 확장해서 변경하면, 클라이언트 코드에 영향을 준다! 따라서 OCP를 위반한다
    - 즉 OrderServiceImpl은 DiscountPolicy라는 인터페이스에 의존함과 동시에 Fix/RateDiscountPolicy라는 구현체에도 의존을 하고 있다는 문제가 있음 (실제 코드에서) -> DIP 위반 (추상화 즉 인터페이스에만 의존해야한다는 원칙)
    - 그러한 원인으로, 만약 FixDiscountPolicy를 내부정책에 따라 RateDiscountPolicy로 변경하려고 한다면 OrderServiceImpl의 소스코드도 함께 변경해줘야함
        - private final DiscountPolicy discountPolicy = new RateDiscountPolicy(); 이 형태로
    - 이건 OCP 위반이다
    - 즉 역할과 구현을 구분했으나 DIP/OCP 원칙은 지켜지지 않은 코드임
    - 이걸 인터페이스에만 의존하도록 코드를 수정하려면
        - private DiscountPolicy discountPolicy; 이렇게만 선언하고 (이 자체는 DIP는 지킨거임 대신 NPE 발생함)
        - 이제 누군가가 클라이언트인 OrderServiceImpl 에 DiscountPolicy 의 구현 객체를 대신 생성하고 주입해주어야 한다 
        - 즉 배역이란 인터페이스를 수행하는 배우라는 구현체는 자기 할일만 하고 여러 배역을 캐스팅하는 공연 기획자가 여러 인터페이스를 만들어 주고 주입해주는 방식으로 돌아가야한다.
        - 그 일을 AppConfig에서 해준다
            - 여기서 MemberServiceImpl의 생성자에 메모리저장멤버리포지토리의 경우 new MemoryMemberRepository()를 준다. *생성자 주입*

- AppConfig 같은 함수를 DI 컨테이너라고 한다.
- AppConfig에 설정을 구성한다는 뜻의 @Configuration 을 붙여준다
- @Bean을 붙이면 해당 메서드가 스프링 컨테이너에 등록이된다
- ApplicationContext가 스프링컨테이너라 보면됨 얘가 @Bean (스프링 빈)붙은 애들을 다 관리해줌
    - ApplicationContext applicationContext =  
        new AnnotationConfigApplicationContext(AppConfig.class);
        - Annotation기반으로 관리하기에 저게 붙고 AppConfig를 넣어준다
        - 즉 ApplicationContext가 AppConfig의 정보를 이용해 @Bean 붙은 객체를 스프링 컨테이너에 다 집어넣어 관리해준다
    - getBean("memberService", MemberService.class);
        - 메서드 이름인 memberService를 넣고 MemberService.class 타입을 getBean하면 사용할 수  있게 된다
- 이전에는 개발자가 필요한 객체를 AppConfig 를 사용해서 직접 조회했지만, 이제부터는 스프링 컨테이너를 통 해서 필요한 스프링 빈(객체)를 찾아야 한다. 스프링 빈은 applicationContext.getBean() 메서드를 사용 해서 찾을 수 있다. 기존에는 개발자가 직접 자바코드로 모든 것을 했다면 이제부터는 스프링 컨테이너에 객체를 스프링 빈으로 등록 하고, 스프링 컨테이너에서 스프링 빈을 찾아서 사용하도록 변경되었다.
- ApplicationContext은 스프링 컨테이너이자 인터페이스이고 AnnotationConfigApplicationContext가 구현체이다.
- 스프링 컨테이너는 설정 정보를 참고해서 의존관계를 주입(DI)한다
    - 단순히 자바 코드를 호출하는 것 같지만, 차이가 있다. 이 차이는 뒤에 싱글톤 컨테이너에서 설명한다
- 스프링 빈 조회 (상속관계)
    - 부모타입 조회 시 자식 타입도 함께 조회 됨
- 스프링 컨테이너는 BeanDefnition만 참조하므로 BeanDefinition이 Appconfig.class인지 appConfig.xml인지 뭘로 구현되어있는지는 노상관이라 추상화가 잘 되어있음.
- AppConfig 처럼 자바코드로 Bean을 등록하는 방법을 factory method 방식이라 함

- 스프링없는 순수한 DI 컨테이너인 AppConfig는 클라이언트가 요청을 할 때 마다 객체를 새로 생성한다는 문제를 가짐 
    - 싱글톤 패턴으로 해결 : 해당 객체가 딱 1개만 생성되고, 공유하도록 설계
    - 싱글톤 패턴 : 클래스의 인스턴스가 딱 1개만 생성됨을 보장하는 디자인 패턴이다.
    - private static final SingletonService instance = new ~; 로 스태틱이기에 클래스레벨에서 딱 하나만 존재하게 됨
    - private SingletonService() {} 로 외부에서의 객체 생성을 막아놨기에 딱 하나만 존재할 수 있게 됨. 즉 private으로 new 키워드를 막아놓음
    - instance를 참조하고 싶으면 getInstance()로만 가능함.
    - 스프링은 기본적으로 싱글톤을 지원해주는 듯.
    - 싱글톤 패턴은 많은 문제점을 가지고 있음 (자료 참고)
- 싱글톤 컨테이너
    - 싱글톤 패턴의 문제점을 모두 해결하는 스프링 컨테이너.
    - 지금껏 학습한 스프링 빈이 바로 싱글톤으로 관리되는 빈임
    - springContainer()에서 예전에 만든 memberService의 구현체인MemberServiceImpl을 보면 싱글톤으로 만드려는 행위가 없었지만 컨테이너에서 알아서 관리해줌을 알 수 있다. 
    - 즉 스프링은 memberService 요청을 많은 클라이언트들로 부터 요구 받아도 스프링 DI 컨테이너는 동일한 memberService를 반환 해준다. 따라서 객체를 매번 생성하는게 아니라 하나를 공유해줘서 효율적이게 된다.
    - *이런 이유로 단순 DI 컨테이너인 AppConfig가 아니라 스프링 DI 컨테이너를 이용한다*
- StatefulServiceTest 클래스
    - ac만들때 TestConfig.class를 넣어주는데 static class인 TestConfig 하나만 딱 생성해서 쓰는 용도로 만든거임.
    - TestConfig에는 빈들이있고 ac에서 빈들이 초기화됨
    - StatefulService는 같은 객체이므로 private int price를 statefulService1과 2가 공유해버린다는 문제를 가짐 -> 전역으로 선언하지말고 로컬파라미터로 바꾸면 됨
 - ConfigurationSingletonTest
     - memberRepository는 3번의 new가 호출이되지만 3개 다 같은 객체이다. 
     - 실체 @Bean MemberRepository에 sout으로 로그 찍어보면 3번이 아니라 한 번만 프린트 되는걸 알 수 있음 (자바 코드로는 설명이 불가한 흐름임)
         - @Configuration 덕분에 최초의 빈인지 아닌지로 구분해주는 듯 (CGLIB 기술)
         - 만약 @Configuration을 제거하고 테스트 해보면 기존 자바 코드처럼 모든 new에 대해 중복해서 생성함 (싱글톤이 깨져버린거임)
- @ComponentScan은 @Component라는 어노테이션 붙은 애들을 다 스캔해서 자동으로 컨테이너에 등록해줌.
    - 이때 @Configuration은 수동으로 등록해주는 존재이니 excludeFilters로 제외해준다. (@Configuration도 까보면 @Component 붙어 있음, 실무에선 잘 제외하진 않는 듯)
    - MemberServiceImpl에 @Component 붙여줘도 생성자는 MemberRepository에 의존성이 있음. 그래서 생성자에도 @Autowired라는 어노테이션 붙여줘야함. Fix말고 RateDiscountPolicy에도 당연히 @Component 붙여줘야 함
    - 즉 @Autowired는 의존관계를 자동으로 주입해 줌. 이때 기본적으로 타입을 바탕으로 주입을 해줌
    - @MyIncludeComponent 붙은 클래스는 포함시키고 @MyExcludeComponent 붙은 클래스는 제외시킴. 이때 @MyIn/Ex~는 내가 정의한 거임
- 생성자가 하나일때는 @Autowired를 생략해도 됨
- init(~,~)이란 임의의 함수 만들어서 메서드 주입도 가능하긴 함
- 필드 주입, 수정자 주입(세터, 게터) 이딴거 쓰지말고 그냥 *생성자 주입*만 써라
- 생성자의 또다른 장점은 변수를 final로 선언해줄수 있기에 생성자에서만 초기화를 해줄 수 있다

- 롬복은 @Getter/Setter 사용가능하게 해주며 따로 게터 세터 안만들어도 저 어노테이션 이용해서 바로 setName(), getName() 형태를 쓸 수 있음 (편리함).
- 롬복 라이브러리가 제공하는 *@RequiredArgsConstructor* 기능을 사용하면 final이 붙은 필드를 모아서 생성자를 자동으로 만들어준다. (다음 코드에는 보이지 않지만 실제 호출 가능하다.)
    - ctrl + F12 눌러서 확인해보면 실제로 생성자가 만들어진걸 확인할 수 있다
    - *즉 이 방법으로 @Autowired의 간편함을 대체하면서도 생성자 주입의 장점을 이용할 수 있게 됐다*
- 기존에 RateDiscountPolicy에 @Component가 붙어있기에 FixDiscountPolicy에도 @Component를 붙이게 되면 basicScan에서 빈이 두 개라 충돌이 발생하는 에러가 생김
    - 즉 이름만 다르고 완전히 똑같은 타입의 스프링 빈이 2개 있을 때 해결이 안됨
    - 스프링 빈을 수동등록 해 문제를 해결해도 되지만 의존관계 자동 주입에서 해결하는 방법이 있음
- @Autowired, @Qualifier, @Primary로 해결함 (참고로 이때 빌드는 Gradle로 셋팅해놨음)
    - @Autowired는 기본적으로 타입으로 비교하나 만약 필드명 or 파라미터 이름과 매칭되는 빈이 존재한다면 그걸로 빈 등록 해줌 (같은 타입의 다른 얘들이 있어도). 
    - @Qualifier 는 @Qualifier 를 찾는 용도로만 사용하는게 명확하고 좋다
    - @Primary가 붙어 있으면 @Autowired 시에 여러 빈이 매칭되면 걔가 우선권을 가진다.
    - @Qualifier를 사용자 정의로 만들어서 @MainDiscountPolicy 형태로 붙여주면 컴파일시 에러 잡기 용이함, 코드 추적도 편함

- networkClient()로 호출된 결과물이 스프링 빈으로 등록이 됨
- LifeCycleConfig에는 @Configuartion 달아주고 ac에는 해당 config 넣어줌
- InitializingBean의 @Override한 afterPorpertiesSet()은 의존관계 주입이 끝나면 호출해주겠다는 함수이다
- DisposableBean의 destory()는 disconnect 역할을 담당한다
- 근데 이 방법들은 거의 사용하지 않는다 지나치게 스프링 의존적이라 외부 소스와 연동이 힘든듯
- @Bean(initMethod = "init", destroyMethod = "close") 이 방식으로 대체함
- 그냥 이제는 @PostConstruct, @PreDestroy 쓰면 됨

- AnnotationConfigApplicationContext에 PrototypeBean.class 바로 넣어버리면 PrototypeBean에 굳이 @Component 안 붙여줘도 알아서 인식한다.
- 중요한 점이 있는데, clientBean이 내부에 가지고 있는 프로토타입 빈은 이미 과거에 주입이 끝난 빈이 다. 주입 시점에 스프링 컨테이너에 요청해서 프로토타입 빈이 새로 생성이 된 것이지, 사용 할 때마다 새로 생성되 는 것이 아니다!
    - 즉 생성시점에 prototypeBean은 이미 주입되어서 logic()을 호출할때마다 같은 prototypeBean이 쓰이는거임
- 만약 프로토타입빈을 주입시점에만 새로 생성하는게 아니라, 사용할 때마다 새로 생성해서 사용하길 원한다면
- Provider provider; 이용해서 proverider.get()을 통해서 항상 새로운 프로토타입 빈이 생성되는 것을 확인할 수 있다
    - 프로토타입 빈을 컨테이너에서 대신 찾아주는 딱 DL(Dependency Lookup) 정도의 기능만 제공
- 그러면 프로토타입 빈을 언제 사용할까? 매번 사용할 때 마다 의존관계 주입이 완료된 새로운 객체가 필요하면 사 용하면 된다. 그런데 실무에서 웹 애플리케이션을 개발해보면, 싱글톤 빈으로 대부분의 문제를 해결할 수 있기 때 문에 프로토타입 빈을 직접적으로 사용하는 일은 매우 드물다.

- 'spring-boot-starter-web' 라이브러리 추가하면 스프링부트는 내장 톰켓 서버를 활용해 웹서버와 스프링을 함께 실행시켜서 localhost:8080에 접속된다.
- UUID.randomUUID().toString();로 유니크 ID를 만들면 전세계에 하나만 존재하는 ID를 얻을 수 있다.
- @Scope(value = "request") 를 사용해서 request 스코프로 지정했다. 이제 이 빈은 HTTP 요청 당 하나 씩 생성되고, HTTP 요청이 끝나는 시점에 소멸
    - 고객이 들어올때 최초에 @PostConstruct가 실행되고 스프링에서 나갈때 @PreDestroy가 실행되면서 빈이 소멸됨
- 뷰 화면이 없이 문자를 그냥 바로 반환하려면 @ResponseBody 쓰면 됨. 원래는 고객 요청오면 뷰 컨트롤러 거쳐서 렌더링 후 나가는데 해당 어노테이션 붙여주면 이 문자를 그대로 응답으로 보낼 수 있다.
- HttpServletRequest로 표준 자바 Http 규약 정보를 받을 수 있음.
- MyLogger는 request 스코프임 근데 스프링을 실행시키면 고객 요청이 오기 전이니까 무조건 에러뜸
- Provider로 look up할 수 있도록 수정하면 잘 돌아감
    - ObjectProvider 덕분에 ObjectProvider.getObject() 를 호출하는 시점까지 request scope 빈의 생성(정확하게는 스프링컨테이너에 요청)을 지연할 수 있다
    - MyLogger myLogger = myLoggerProvider.getObject(); 로 시점을 정해주기 때문. 이 시점에 요청이 생성된다. 이때 @PostConstruct가 실행되어서 Http 요청과 uuid를 연결시킴 그 후 myLogger.log로 로그를 찍는거임
    - 핵심은 동시에 여러 요청이 오더라도 요청마다 객체를 각각 따로 관리해준다는 것이다.
- resources/application.properties에 *logging.level.root=INFO* 추가해서  디버깅 정보가 출력되지 않도록 해 CONDITIONS EVALUATION REPORT 숨기도록 했음
- proxyMode를 설정해주면 마치 provider를 쓰는것과 동일한 효과를 준다.
    - private final MyLogger myLogger에는 진짜 myLogger가 아니라 껍데기의 가짜(프록시)를 넣어두고, 실제 그 기능을 호출하는 myLogger.setRequestURL() 등의 순간에 이때 진짜를 찾아서 동작하기 때문










