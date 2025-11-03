package Pyeon.Pyeon_Spring_Test;

import Pyeon.Pyeon_Spring_Test.aop.TimeTraceAop;
import Pyeon.Pyeon_Spring_Test.repository.JdbcTemplateMemberRepository;
import Pyeon.Pyeon_Spring_Test.repository.JpaMemberRepository;
import Pyeon.Pyeon_Spring_Test.repository.MemberRepository;
import Pyeon.Pyeon_Spring_Test.service.MemberService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class SpringConfig {

    /*
    private DataSource dataSource;
    private final EntityManager em;

    public SpringConfig(DataSource dataSource, EntityManager em) {
        this.dataSource = dataSource;
        this.em = em;
    }
    */

    private final MemberRepository memberRepository;

    //@Autowired
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository);
    }

    //@Bean 에러남
    //public TimeTraceAop timeTraceAop() { return new TimeTraceAop(); }

     /*
    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        //return new MemoryMemberRepository();
        //return new JdbcTemplateMemberRepository(dataSource);
        //return new JpaMemberRepository(em);
        return null;
    }*/
}
