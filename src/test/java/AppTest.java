import com.unfbx.sparkdesk.SparkDeskClient;
import com.unfbx.sparkdesk.constant.SparkDesk;
import com.unfbx.sparkdesk.entity.*;
import com.unfbx.sparkdesk.listener.ChatListener;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class AppTest {
    @Test
    public void test() {
        //构建客户端
        SparkDeskClient sparkDeskClient = SparkDeskClient.builder()
                .host(SparkDesk.SPARK_API_HOST_WS_V2_1)
                .appid("984cc04c")
                .apiKey("c91635a4e3fa65a8140ce405c9e69c13")
                .apiSecret("YzdhOTRjYTZjYjI0MmM0NDUyYjZhOTZh")
                .build();
        //构建请求参数
        InHeader header = InHeader.builder().uid(UUID.randomUUID().toString().substring(0, 10)).appid("984cc04c").build();
        Parameter parameter = Parameter.builder().chat(Chat.builder().domain("generalv2").maxTokens(2048).temperature(0.3).build()).build();
        List<Text> text = new ArrayList<>();
        text.add(Text.builder().role(Text.Role.USER.getName()).content("使用md文档格式写出一个三行三列的表格，表头包含：姓名，性别，爱好。数据随机即可。").build());
        InPayload payload = InPayload.builder().message(Message.builder().text(text).build()).build();
        AIChatRequest aiChatRequest = AIChatRequest.builder().header(header).parameter(parameter).payload(payload).build();

        //发送请求
        sparkDeskClient.chat(new ChatListener(aiChatRequest) {
            //异常回调
            @SneakyThrows
            @Override
            public void onChatError(AIChatResponse aiChatResponse) {
            }
            //输出回调
            @Override
            public void onChatOutput(AIChatResponse aiChatResponse) {
                System.out.println("content: " + aiChatResponse);
            }
            //会话结束回调
            @Override
            public void onChatEnd() {
                System.out.println("当前会话结束了");
            }
            //会话结束 获取token使用信息回调
            @Override
            public void onChatToken(Usage usage) {
                System.out.println("token 信息：" + usage);
            }
        });

        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
