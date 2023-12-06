package hello.core.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

public class NetworkClient {
    private String url;

    public NetworkClient() {
        System.out.println("생성자 호출, url = " + url);
        connect();
        call("초기화 연결 메세지");
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //서비스 실행시
    public void connect() {
        System.out.println("connect: " + url);
    }

    public void call(String message) {
        System.out.println("call = " + url + " message = " + message);
    }

    //서비스 종료시
    public void disconnect() {
        System.out.println("close = " + url);
    }

    //인터페이스를 활용한 방법 implements InitializingBean, DisposableBean
    public void afterPropertiesSet() throws Exception {
        System.out.println("NetworkClient.afterPropertiesSet");
        connect();
        call("초기화 연결 메세지");
    }
    //인터페이스를 활용한 방법 implements InitializingBean, DisposableBean
    public void destroy() throws Exception {
        System.out.println("NetworkClient.destroy");
        disconnect();
    }

    //빈 등록 소멸 메서드를 활용한 방법 @Configuration 빈 등록 시 init, destroy 메서드를 지정해주면 됨
    @PostConstruct //어노테이션을 활용한 빈 생성, 소멸 관리
    public void init() {
        System.out.println("NetworkClient.init");
        connect();
        call("초기화 연결 메세지");
    }

    //빈 등록 소멸 메서드를 활용한 방법 @Configuration 빈 등록 시 init, destroy 메서드를 지정해주면 됨
    @PreDestroy //어노테이션을 활용한 빈 생성, 소멸 관리
    public void close() {
        System.out.println("NetworkClient.close");
        disconnect();
    }
}
