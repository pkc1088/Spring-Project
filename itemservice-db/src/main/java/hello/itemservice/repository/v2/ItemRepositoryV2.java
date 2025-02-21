package hello.itemservice.repository.v2;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

// 간단한 CRUD 기능은 다 JpaRepository 인터페이스에서 처리
public interface ItemRepositoryV2 extends JpaRepository<Item, Long> {
}
