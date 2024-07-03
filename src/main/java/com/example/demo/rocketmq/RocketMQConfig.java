package com.example.demo.rocketmq;

public class RocketMQConfig {

    /**
     * 订阅关系:设计一个消费者组订阅一个topic,不建议一个消费组消费多个topic
     *
    订阅关系：一个消费者组订阅一个 Topic 的某一个 Tag，这种记录被称为订阅关系。

    订阅关系一致：同一个消费者组下所有消费者实例所订阅的Topic、Tag必须完全一致。如果订阅关系（消费者组名-Topic-Tag）不一致，会导致消费消息紊乱，甚至消息丢失
    */


    public static final String TOPIC ="rocket";

    /**
     dlq:topic %RETRY%consumerGroup
     */
    public static final String CONSUMER_GROUP ="consumerGroup";

    public static final String TOPIC_DLQ ="%DLQ%consumerGroup";
    /**

     */
    public static final String CONSUMER_GROUP_DLQ ="consumerGroupDLQ";


    public static final String TOPICB ="TopicB";
    public static final String CONSUMER_GROUP_B ="consumerGroupB";
}
