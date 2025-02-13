package hello.itemservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	/* 기존 컨트롤러의 @InitBinder 를 제거해도 글로벌 설정으로 정상 동작하는 것을 확인할 수 있다
	@Override
	public Validator getValidator() {
		return new ItemValidator();
	}
	*/
}
