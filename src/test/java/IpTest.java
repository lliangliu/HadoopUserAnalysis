import com.qianfeng.etl.util.IpPaerserUtil;
public class IpTest {

    public static void main(String[] args) {
       // System.out.println(IpPaerserUtil.ipParser("112.114.108.135"));
       System.out.println(
                IpPaerserUtil.ipParser2("http://ip.taobao.com/service/getIpInfo.php?ip=112.114.108.135",
                        "utf-8"));

    }
}
