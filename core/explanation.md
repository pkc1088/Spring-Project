
==sping02==
- SOLID 원칙
    - OCP(Open/closed principle)
        - 확장에는 열려 있으나 변경에는 닫혀 있어야 한다 (다형성으로 역할과 구현을 분리)
        - 즉 기존 코드 수정 없이 기능을 확장할 수 있어야 함.
    - DIP (Dipendency Inversion Principle)
        - 추상화에 의존해야지, 구체화에 의존하면 안된다.
        - 즉 인터페이스, 추상 클래스에 의존해야 함
        - 즉 구체적인 구현이 아니라 추상화된 인터페이스에 의존해야 함
    - SRP : 한 클래스는 하나의 책임만 가져야 한다
    - LSP : 프로그램의 객체는 프로그램의 정확성을 깨뜨리지 않으면서 하위 타입의 인스턴스로 바꿀 수 있어야 한다
    - ISP : 특정 클라이언트를 위한 인터페이스 여러개가 범용 인터페이스 하나보다 낫다
- 누군가가 클라이언트인 OrderServiceImpl 에 DiscountPolicy 의 구현 객체를 대신 생성하고 주입해주어야 OCP, DIP 지켜짐
    - 이걸 AppConfig가 해준다 (*생성자 주입*)
    ```java 
    //AppConfig
    public OrderService orderService() { 
        return new OrderServiceImpl(
                    memberRepository(), discountPolicy()); 
    }
    ...
    public DiscountPolicy discountPolicy() {  
        return new RateDiscountPolicy();  
        // 앞으로 여기서만 FixDiscountPolicy 등으로 바꿔주면 끝
    }
    ```    
    - AppConfig는 애플리케이션의 실제 동작에 필요한 구현 객체를 생성한다.
    - AppConfig는 생성한 객체 인스턴스의 참조(레퍼런스)를 생성자를 통해서 주입(연결)해준다.
    - 이를 통해 구현체인 OrderServiceImpl은 인터페이스인 DiscountPolicy에만 의존하면 된다. 즉 그 인터페이스의 구현체인 Fix/RateDiscountPolicy에 의존하지 않게 된다.
- *DI 컨테이너* : AppConfig 처럼 객체를 생성/관리하면서 의존관계를 연결해주는 것
- *@Configuartion*
    - Bean 설정을 정의하는 클래스임을 나타낸다.
    - 이 프로젝트의 경우 AppConfig에 붙여준다.
    - 모듈별로 분리된 설정을 관리하기 위해 여러 개의 설정 클래스를 사용할 수도 있음. DatabaseConfig라는 클래스, ServiceConfig라는 클래스에 각각 붙여줄 수 있다.
    - 소스코드를 열어보면 @Component 애노테이션이 붙어있다
- *@Bean* : Appconfig 내의 각 메서드에 붙여준다. 스프링 컨테이너에 스프링 빈으로 등록된다는 뜻이다.
- *ApplicationContext*
    ```java
    ApplicationContext applicationContext
    = new AnnotationConfigApplicationContext(AppConfig.class);
    ```
    - 스프링 컨테이너이다. new로 스프링 컨테이너를 생성한다.
    - @Configuration을 포함하여 스프링 애플리케이션의 전체 설정을 로드하고 관리함. 당연히 @Bean 붙은 애들(빈)을 다 관리해줌
    - 기존에는 개발자가 AppConfig 를 사용해서 직접 객체를 생성하고 DI를 했지만, 이제부터는 스프링 컨테이너를 통해서 사용한다
    - *스프링 컨테이너는 @Configuration이 붙은 AppConfig를 구성정보로 사용*한다. 
    - AppConfig 외에 더 넣고 싶으면 쉼표하고 넣으면 됨
    - 구성정보 클래스 및 그 내부의 @Bean이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너의 스프링 빈 저장소에 등록한다. 
    - 이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라 한다. 
    - 이전에는 개발자가 필요한 객체를 AppConfig 를 사용해서 직접 조회했지만, 이제부터는 스프링 컨테이너를 통해서 필요한 스프링 빈(객체)를 찾아야 한다. 
    - 즉 생성자를 호출하면서 의존관계 주입도 한번에 처리한다.
    - 스프링 빈은 applicationContext.getBean() 메서드를 사용 해서 찾을 수 있다. 기존에는 개발자가 직접 자바코드로 모든 것을 했다면 이제부터는 스프링 컨테이너에 객체를 스프링 빈으로 등록 하고, 스프링 컨테이너에서 스프링 빈을 찾아서 사용하도록 변경되었다.
- BeanDefinition
    - AppConfig.class/.xml/.xxx 등 다양한 형식을 지원해준다. 스프링 컨테이너는 BeanDefintion의 빈 설정 메타정보만 참고하기에 참고하는 내용이 자바든 xml이든 노상관이다
    - AppConfig 처럼 자바코드로 Bean을 등록하는 방법을 factory method 방식이라 함
- *Singleton*
    - 정의 : 클래스의 인스턴스가 딱 1개만 생성됨을 보장하는 디자인 패턴이다.
    - 스프링 없는 순수한 DI 컨테이너인 AppConfig는 요청을 할 때 마다 객체를 새로 생성함
    - 해당 객체가 딱 1개만 생성되고, 공유하도록 설계되도록 싱글톤 패턴으로 해결한다.
        - private 생성자를 사용해서 외부에서 임의로 new 키워드를 사용하지 못하도록 막아야 한다
        - private static final SingletonService instance = new SingletonService(); 로 static으로 선언해서 클래스레벨에서 딱 하나만 존재하게 만듦.
        - private SingletonService() {} 로 외부에서의 객체 생성을 막아놨기에 딱 하나만 존재할 수 있게 됨. 즉 private으로 new 키워드를 막아놓음
        - 이 객체 인스턴스가 필요하면 오직 public의 getInstance()로만 조회 가능함.
        - 이 메서드를 호 출하면 항상 같은 인스턴스를 반환함
    - 싱글톤 패턴의 문제점
        - 싱글톤 패턴을 구현하는 코드 자체가 많이 들어간다. 
        - 의존관계상 클라이언트가 구체 클래스에 의존한다. 
        - DIP를 위반한다. 클라이언트가 구체 클래스에 의존해서 OCP 원칙을 위반할 가능성이 높다. 
        - 테스트하기 어렵다. 
        - 내부 속성을 변경하거나 초기화 하기 어렵다. private 생성자로 자식 클래스를 만들기 어렵다. 
        - 결론적으로 유연성이 떨어진다. 안티패턴으로 불리기도 한다.
    - 스프링 컨테이너는 싱글톤 패턴의 문제점을 해결하면서, 객체 인스턴스를 싱글톤(1개만 생성)으로 관리한다. 지금까지 우리가 학습한 스프링 빈이 바로 싱글톤으로 관리되는 빈이다
        - 스프링 컨테이너는 싱글턴 패턴을 적용하지 않아도, 객체 인스턴스를 싱글톤으로 관리한다.
        - 스프링 컨테이너는 싱글톤 컨테이너 역할을 한다. 이렇게 싱글톤 객체를 생성하고 관리하는 기능을 싱글톤 레지스 트리라 한다
        - 스프링 컨테이너의 이런 기능 덕분에 싱글턴 패턴의 모든 단점을 해결하면서 객체를 싱글톤으로 유지할 수 있다. 
            - 싱글톤 패턴을 위한 지저분한 코드가 들어가지 않아도 된다. 
            - DIP, OCP, 테스트, private 생성자로 부터 자유롭게 싱글톤을 사용할 수 있다.
    - 스프링 컨테이너 덕분에 고객의 요청이 올 때 마다 객체를 생성하는 것이 아니라, *이미 만들어진 객체를 공유해서 효율적으로 재사용*할 수 있다
    - 하나의 같은 객체 인스턴스를 공유하기 때문에 싱글톤 객체는 상태를 유지(stateful)하 게 설계하면 안된다. *무상태(stateless)로 설계*해야 한다
        - 특정 클라이언트에 의존적인 필드가 있으면 안된다. 
        - 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다
        - 가급적 읽기만 가능해야 한다. 
        - 필드 대신에 자바에서 공유되지 않는, 지역변수, 파라미터, ThreadLocal 등을 사용해야 한다
        - 반례로 'StatefulServiceTest 클래스' 좋은 예시임
- CGLIB
    - CGLIB라는 바이트코드 조작 라이브러리를 사용해서 AppConfig 클래스를 상속받은 임의의 다른 클래스를 만들고, 그 클래스를 스프링 빈으로 등록한다
    - 그 임의의 다른 클래스가 바로 싱글톤이 보장되도록 해준다. 아마도 다음과 같이 바이트 코드를 조작해서 작성되어 있을 것이다.(실제로는 CGLIB의 내부 기술을 사용하는데 매우 복잡하다.)
        - AppConfig <- AppConfig@CGLIB의 꼴이다.
    - 그래서 memberRepository()를 여러번 new로 생성해도 같은 객체를 가리킨다.
    - 즉 AppConfig는 @Configuration을 붙여서 바이트코드를 조작하는 CGLIB 기술을 사용해서 싱글톤을 보장한다. 
        - 만약 이 어노테이션을 안 붙이면 new로 생성하면 각각 다른 인스턴스가 생성된다.
 - *@ComponentScan*
    - @Bean을 일일이 매번 등록하는건 비효율적이다
    - 컴포넌트 스캔은 이름 그대로 @Component 애노테이션이 붙은 클래스를 스캔해서 스프링 빈으로 등록한다. 
        - 이때 스프링 빈의 기본 이름은 클래스명을 사용하되 맨 앞글자만 소문자를 사용
        - 빈 이름 직접 지명하려면 @Component("memberService2") 이런식으로 이름을 부여하면 됨
    - 스프링은 설정 정보가 없어도 자동으로 스프링 빈을 등록하는 컴포넌트 스캔의 기능을 제공한다. 이제 AutoAppConfig클래스를 @Configuration으로서 사용한다.
    - 컴포넌트 스캔하면 @Configuration이 붙은 모든 설정 정보들도 자동으로 등록됨 (중복 주의)
        - @Configuration에도 내부에 @Componenet 붙어있기 떄문
        - excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
        - 이걸로 @Configuration이 붙은 다른 클래스들을 제외하도록 설정했음
    - MemoryMemberRepository, RateDiscountPolicy와 같은 구현체들에 @Component 를 붙여주자. 
    - 참고로 스프링 부트를 사용하면 스프링 부트의 대표 시작 정보인 *@SpringBootApplication*를 이 프로젝트 시작 루트 위치에 두는 것이 관례이다. (그리고 이 설정안에 바로 @ComponentScan 이 들어있다!)
    - 컴포넌트 스캔이 포함하는 대상들
        - @Component : 컴포넌트 스캔에서 사용 
        - @Controller : 스프링 MVC 컨트롤러에서 사용 
        - @Service : 스프링 비즈니스 로직에서 사용 
        - @Repository : 스프링 데이터 접근 계층에서 사용 
        - @Configuration : 스프링 설정 정보에서 사용
        - 왜냐하면 얘네 모두 내부에 @Component 포함하기 떄문임
    - 최근 스프링 부트에서는 수동 빈 등록과 자동 빈 등록이 충돌나면 오류가 발생하도록 함
- *@Autowired*
    - 의존관계를 자동으로 주입해준다.
    - 생성자에 붙여주는 방식을 통상 사용한다. (생성자에서 여러 의존관계도 한번에 주입받을 수 있다)
    - 생성자에 @Autowired 지정하면 스프링 컨테이너가 자동으로 해당 스프링 빈을 찾아서 주입한다
    - 생성자가 딱 1개만 있으면 @Autowired를 생략해도 자동 주입 된다
    - 필드 주입, 메서드 주입, 수정자 주입(세터, 게터) 이딴거 쓰지말고 그냥 생성자 주입만 써라
    - 오직 생성자 주입 방식만 final 키워드를 사용할 수 있다.
    - 타입(Type)으로 조회한다. 즉 ac.getBean(DiscountPolicy.class)이런 느낌이다. 근데 DiscountPolicy 하위 타입인 Fix와 Rate 둘 다 스프링 빈으로 선언(@Component 붙여)하면 에러 발생
        - NoUniqueBeanDefinitionException 발생한다.
        - 조회 빈이 2개 이상일때 문제 발생 -> @Qualifier, @Primary로 해결한다.
- *@RequiredArgsConstructor*
    - 롬복이 제공한다.
    - final이 붙은 필드를 모아서 생성자를 자동으로 만들어준다.
    - OrderServiceImpl 클래스에 해당 어노테이션 붙여주면 굳이 @Autowired+생성자 안 만들어도 된다.
- 조회 빈이 2개 이상일 때 해결 방법
    - @Autowired 필드 명 매칭
        - @Autowired 는 타입 매칭을 시도하고, 이때 여러 빈이 있으면 필드 이름 혹은 파라미터 이름 중 매칭되는 얘 있으면 그걸 빈 이름을 추가 매칭한다. 
    - *@Qualifier*
        - 추가 구분자를 붙여주는 방법이다. 주입시 추가적인 방법을 제공하는 것이지 빈 이름을 변경하는 것은 아니다
        - @Qualifier("mainDiscountPolicy")를 RateDiscountPolicy클래스에 붙여준다, @Qualifier("fixDiscountPolicy")를 Fix클래스에 붙여준다.
        - OrderServiceImpl의 생성자 주입시 DiscountPolicy discountPolicy라는 파라미터 앞에 @Qualifier("mainDiscountPolicy")를 붙여준다. 그러면 해당 어노테이션과 매칭되는 Rate클래스가 등록된다
    - *@Primary*
        - @Autowired 시에 여러 빈이 매칭되면 @Primary 가 우선권을 가진다. Rate나 Fix 둘 중 하나에 @Primary 붙여주면 걔로 주입한다
        - 이걸 자주 사용하는 듯
        - Qualifier의 우선순위가 Primary 보다 높다.
    - *annotation 직접 만들기*
        - @Qualifier("mainDiscountPolicy") 이렇게 문자를 적으면 컴파일시 타입 체크가 안된다. 다음과 같은 애노테이션을 만들어서 문제를 해결할 수 있다
        - MainDiscountPolicy라는 인터페이스를 만들고 @Qualifier("mainDiscountPolicy")라고 달아주고 추가적으로 어노테이션 몇개 더 달아주면 
        - @MainDiscountPolicy라는 나만의 어노테이션을 쓸 수 있게 된다.
        - 이 어노테이션을 Fix or Rate에 달아주면 된다
- 애노테이션에는 상속이라는 개념이 없다. 이렇게 여러 애노테이션을 모아서 사용하는 기능은 스프링이 지원해주는 기능이다.
- 개발자 입장에서 스프링 빈을 하나 등록할 때 @Component 만 넣어주면 끝나는 일을 @Configuration 설정 정보에 가서 @Bean 을 적고, 객체를 생성하고, 주입할 대상을 일일이 적어주는 과정은 상당히 번거롭다. 또 관리할 빈이 많아서 설정 정보가 커지면 설정 정보를 관리하는 것 자체가 부담이 된다. 그리고 결정적으로 자동 빈 등록을 사용해도 OCP, DIP를 지킬 수 있다
    - *자동 빈 등록을 사용하자*
    - 애플리케이션에 광범위하게 영향을 미치는 기술 지원 객체는 수동 빈으로 등록해서 딱! 설정 정보에 바로 나타나게 하는 것이 유지보수 하기 좋다.
```java
@Configuration 
public class DiscountPolicyConfig { 
    @Bean 
    public DiscountPolicy rateDiscountPolicy() { 
        return new RateDiscountPolicy(); 
    } 
    @Bean 
    public DiscountPolicy fixDiscountPolicy() { 
        return new FixDiscountPolicy(); 
    } 
}
```
- 이 설정 정보만 봐도 한눈에 빈의 이름은 물론이고, 어떤 빈들이 주입될지 파악할 수 있다. 그래도 빈 자동 등록을 사용 하고 싶으면 파악하기 좋게 DiscountPolicy 의 구현 빈들만 따로 모아서 특정 패키지에 모아두자
- 정리
    - 편리한 자동 기능을 기본으로 사용하자.
    - 직접 등록하는 기술 지원 객체는 수동 등록.
    - 다형성을 적극 활용하는 비즈니스 로직은 수동 등록을 고민해보자.
- 지금까지 우리는 스프링 빈이 스프링 컨테이너의 시작과 함께 생성되어서 스프링 컨테이너가 종료될 때까지 유지된다 고 학습했다. 이것은 스프링 빈이 기본적으로 싱글톤 스코프로 생성되기 때문이다.
- *@PostConstruct*
    - 빈(Bean)이 초기화되고 난 후 실행되는 메서드에 적용되는 애노테이션입니다.
    - 빈이 생성된 후, 의존성 주입이 완료된 뒤, 초기화 작업을 실행하기 위해 사용됩니다.
    - 이 메서드는 생성자나 @Autowired된 메서드와 같은 의존성 주입이 모두 완료된 후 호출됩니다.
- *@PreDestroy*
    - @PreDestroy는 빈(Bean) 소멸되기 전에 실행되는 메서드에 적용됩니다.
    - 빈이 종료되기 직전에 정리 작업(자원 반납 등)을 실행하는 데 사용됩니다.
- *Bean Scope*
    - 빈이 존재할 수 있는 범위를 뜻한다.
    - 종류
        - 싱글톤
            - 기본 스코프, 스프링 컨테이너의 시작과 종료까지 유지되는 가장 넓은 범위의 스코프이다.
        - Prototype
            - 스프링 컨테이너는 프로토타입 빈의 생성과 의존관계 주입까지만 관여하고 더는 관리하지 않는 매우 짧은 범위의 스코프이다
            - @Scope("prototype")
        - 웹 관련 스코프
            - request: 웹 요청이 들어오고 나갈때 까지 유지되는 스코프이다.
            - session: 웹 세션이 생성되고 종료될 때 까지 유지되는 스코프이다.
            - application: 웹의 서블릿 컨텍스트와 같은 범위로 유지되는 스코프이다
- *Prototype Scope*
    - 싱글톤 스코프의 빈을 조회하면 스프링 컨테이너는 항상 같은 인스턴스의 스프링 빈을 반환한다.
        - *싱글톤 빈은 스프링 컨테이너 생성 시점에 생성됨*(= 초기화 메서드가 실행 됨) 떄문이다.
    - 반면에 프로토타입 스코프를 스프링 컨테이너에 조회하면 스프링 컨테이너는 항상 새로운 인스턴스를 생성해서 반환한다.
        - *PrototypeBean은 실제 빈을 조회할 때 생성 및 초기화 됨*
    - 프로토타입 빈 요청 흐름
        1. 프로토타입 스코프의 빈을 스프링 컨테이너에 요청한다.
        2. 스프링 컨테이너는 이 시점에 프로토타입 빈을 생성하고, 필요한 의존관계를 주입한다
        3. 스프링 컨테이너는 생성한 프로토타입 빈을 클라이언트에 반환한다.
        4. 이후에 스프링 컨테이너에 같은 요청이 오면 항상 새로운 프로토타입 빈을 생성해서 반환한다.
    - 핵심은 스프링 컨테이너는 프로토타입 빈을 생성하고, 의존관계 주입, 초기화까지만 처리한다는 것.
    - 클라이언트에 빈을 반환한 이후 스프링 컨테이너는 생성된 프로토타입 빈을 관리하지 않는다. 프로토타입 빈을 관리할 책임은 프로토타입 빈을 받은 클라이언트에 있다. 그래서 @PreDestroy 같은 종료 메서드가 호출되지 않는다.
- clientBean이라는 *싱글톤 빈이 의존관계 주입을 통해서 프로토타입 빈을 주입받아서 사용하는 예* 'SingletonWithPrototypeTest1 Class'
    - clientBean이 내부에 가지고 있는 프로토타입 빈은 이미 과거에 주입이 끝난 빈이 다. 주입 시점에 스프링 컨테이너에 요청해서 프로토타입 빈이 새로 생성이 된 것이지, 사용 할 때마다 새로 생성되는 것이 아니다
    - 클라A가 count를 증가시키면 0->1
    - 클라B가 count를 증가시키면 1->2가 됨
    - 정확한 흐름
        - 싱글톤인 ClientBean이 스프링에 의해 등록되고 그 생성자에서 PrototypeBean을 호출한다. 이때 프로토타입빈이 요청되는거다. 그러면 스프링컨테이너가 프로토타입빈을 만들어서 던져준다. 그게 private final PrototypeBean prototypeBean; 에 어사인되는거고 그래서 생성시점에 이미 주입이 되어 있는거임. 그래서 클라A가 최초에 요구를 해도 이미 주입 끝난 그 프로토타입빈이 주어지는거고 클라B가 요구해도 마찬가지임
        - 어찌보면 이 흐름이 당연한거임 private final PrototypeBean prototypeBean; 이걸 공유하니까
    - 'PrototypeTest class'와 차이가 있음.
- *Provider*
    - 싱글톤 빈과 프로토타입 빈을 함께 사용할 때, 어떻게 하면 사용할 때 마다 항상 새로운 프로토타입 빈을 생성할 수 있을까의 해결법
    - 물론 ClientBean clientBean1 = ac.getBean(ClientBean.class); 후에 clientBean2에 대해 새로이 호출하면 해결할 수는 있다
        - 이런식으로 의존관계를 외부에서 주입(DI) 받는게 아니라 스프링 컨테이너에 직접 필요한 의존관계를 찾는 것을 Dependency Lookup (DL) 의존관계 조회(탐색) 이라한다.
        - 그런데 이렇게 스프링의 애플리케이션 컨텍스트 전체를 주입받게 되면, 스프링 컨테이너에 종속적인 코드가 되고, 단위 테스트도 어려워진다.
        - 지금 필요한 기능은 지정한 프로토타입 빈을 컨테이너에서 대신 찾아주는 딱! DL 정도의 기능만 제공하는 무언가 가 있으면 된다.
    - *ObjectProvider*
        - getObject()를 호출하면 그때서야 스프링 컨테이너에서 프로토타입 빈을 찾아서 반환해줌 (DL).
        - 이렇게 함으로서 우리가 어플리케이션 컨텍스트한테 직접 찾는게 아니면서도 getObject가 호출될때 반환이 됨으로 프로토타입 빈을 클라들에게 개별적으로 줄 수 있게 됨
    - Provider는 .get()으로 위 내용을 구현함. (자바 표준임)
- 그러면 프로토타입 빈을 언제 사용할까? 매번 사용할 때 마다 의존관계 주입이 완료된 새로운 객체가 필요하면 사 용하면 된다. 그런데 실무에서 웹 애플리케이션을 개발해보면, 싱글톤 빈으로 대부분의 문제를 해결할 수 있기 때 문에 프로토타입 빈을 직접적으로 사용하는 일은 매우 드물다.
- *Web Scope*
    - 종류
        - request: HTTP 요청 하나가 들어오고 나갈 때 까지 유지되는 스코프, 각각의 HTTP 요청마다 별도의 빈 인스턴스가 생성되고, 관리된다.
        - session: HTTP Session과 동일한 생명주기를 가지는 스코프
        - application: 서블릿 컨텍스트( ServletContext )와 동일한 생명주기를 가지는 스코프
        - websocket: 웹 소켓과 동일한 생명주기를 가지는 스코프
    - 스프링 애플리케이션을 실행하는 시점에 싱글톤 빈은 생성해서 주입이 가능하지만, request 스코프 빈은 아직 생성되지 않는다. 이 빈은 실제 고객의 요청이 와야 생성할 수 있다
    - 해결법
        1. Provider 방법
            - ObjectProvider 덕분에 ObjectProvider.getObject() 를 호출하는 시점까지 request scope 빈의 생성을 지연할 수 있다.
            - ObjectProvider.getObject() 를 호출하시는 시점에는 HTTP 요청이 진행중이므로 request scope 빈 의 생성이 정상 처리된다
        2. 프록시 방법
            - MyLogger 클래스에 proxyMode = ScopedProxyMode.TARGET_CLASS 붙여준다.
            - MyLogger의 가짜 프록시 클래스를 만들어두고 HTTP request와 상관 없이 가짜 프록시 클래스를 다른 빈에 미리 주입해 둘 수 있다
            - CGLIB라는 라이브러리로 내 클래스를 상속 받은 가짜 프록시 객체를 만들어서 주입한다.
            - 그래서 의존관계 주입도 이 가짜 프록시 객체가 주입된다
            - 흐름
                - Spring이 CGLIB을 이용하여 가짜 프록시 객체를 생성
                - 클라이언트가 빈을 호출하면 프록시 객체가 먼저 응답.
                    - myLogger.getClass()
                - 이후 프록시 객체의 아무 메서드라도 호출되면 내부적으로 진짜 객체(원본 빈)가 생성되고, 해당 메서드 실행이 위임됩니다. (프록시 객체가 내부적으로 실제 빈을 생성하여 요청을 위임)
                    - myLogger.setRequestURL(requestURL);
                    - myLogger.log("controller test");


----
----

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










