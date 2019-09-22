import com.qianfeng.etl.util.UserAgnetParserUtil;
//测试浏览器
public class UserAgentTest {
    public static void main(String[] args) {
        UserAgnetParserUtil.AgentInfo agentInfo =UserAgnetParserUtil.parserUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
        System.out.println(agentInfo);
    }
}
