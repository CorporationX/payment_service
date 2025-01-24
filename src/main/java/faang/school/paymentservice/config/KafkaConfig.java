package faang.school.paymentservice.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaConfig {
    public static final String PAYMENT_PROMOTION_TOPIC = "payment_promotion";
    public final static String PROMOTION_BOUGHT_TOPIC = "promotion_bought";
    public static final String NOTIFICATION_TOPIC = "notification_topic";
}
