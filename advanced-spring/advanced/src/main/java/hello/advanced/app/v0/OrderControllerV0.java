package hello.advanced.app.v0;

import hello.advanced.trace.TraceId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderControllerV0 {

    private final OrderServiceV0 orderService;

    @GetMapping("/")
    public String index() {
        String str = "abc";
        StringBuilder sb = new StringBuilder("abc");
        StrAppend(str, sb);
        log.info("after : [{}]", str);
        log.info("after : [{}]", sb);

        return "ok";
    }
    public void StrAppend(String str, StringBuilder sb) {
        str += "d";
        sb.append("d");
        log.info("before : [{}]", str);
        log.info("before : [{}]", sb);
    }

    @GetMapping("/v0/request")
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "ok";
    }
}
