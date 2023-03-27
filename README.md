## 消息队列篇（一）

### 1、消息队列是什么？有什么作用？
<hr>

<font color="orange"><b>什么是消息队列</b></font>

消息队列（Message Queue）简称为 MQ，是一种可以应用于分布式系统中的中间件。

消息是信息的载体，在软件系统中，消息就是服务与服务之间传递的数据；而队列则是一种先进先出（FIFO）的数据结构。我们将负责发送消息的一方称为生产者（Producer），而负责消费处理消息的一方称为消费者（Consumer）。由于队列这种数据结构的特性，消息也会按照先进先出的顺序被消费者依次消费。

![](https://files.mdnice.com/user/19026/e21ea1de-92b6-4274-a132-2d3564c76a6b.png)

<font color="orange"><b>消息队列有什么作用？</b></font>

接下来，我们就从一个外卖点餐系统入手，学习一下为什么要使用消息队列，以及消息队列究竟有什么作用。

该外卖点餐系统由以下四种微服务构成：

- 订单微服务
- 商家微服务
- 骑手微服务
- 结算微服务

其业务流程图如下所示：

![](https://files.mdnice.com/user/19026/b15d20e4-aa5d-4ed6-8319-15be81a023b2.png)

如我们所见，该系统各种微服务之间为同步调用，同步直接调用会带来以下几种问题：

- 业务调用链过长，用户需要长时间等待
- 如果部分组件出现故障，则会使整个业务发生瘫痪
- 业务高峰期没有缓冲，假设下单高峰，订单，商家，骑手微服务处理的速度比结算微服务快很多，那么结算微服务就会堆积大量待处理的请求，从而致使系统发生瘫痪

而这个时候，我们就可以使用消息队列来解决这些问题。

来看一下使用消息队列后的业务流程图：

![](https://files.mdnice.com/user/19026/c6059158-1049-4387-89ff-0d726b149449.png)

首先，我们的系统从同步调用改进为异步调用，图中虚线隔开的部分表示为异步调用。从流程图中可以直观地看出，由于异步，用户不再需要长时间的等待了，在用户下订单后，异步线程 a 会立刻响应用户订单处理中，异步线程 b 则会开始执行业务逻辑。

在使用消息队列后，四种微服务会调用消息中间件来进行消息的收发，而不再与彼此相互耦合：

![](https://files.mdnice.com/user/19026/fa5925fe-3d06-4c36-a5d0-bcf225692937.png)

这样做的好处是，如果某一个微服务宕机，那么消息还是会保存在消息队列中，等到该微服务重启后，仍然能继续处理消息。

并且，业务高峰期没有缓冲这一问题也可以得到解决。业务高峰期产生的消息会被存储到消息队列中，然后我们根据各个微服务的能力，慢慢去消费这些消息，这样就可以避免某个微服务因堆积了大量请求而瘫痪的问题。

通过这样一个示例，我们可以总结出，为什么要使用消息队列，或者说消息队列的作用是什么？

用简单的三句话来概括：

1. 异步处理
2. 系统解耦
3. 削峰限流

那么，使用消息队列会带来哪些问题呢？

1. 系统可用性降低
2. 系统复杂性变高
3. 一致性问题

如果 MQ 挂掉，就会导致系统发生崩溃，所以系统的可用性降低了；我们引入了 MQ 以后，需要考虑消息是否有被重复消费、丢失等问题，所以提升了系统的复杂性；MQ 可以实现异步调用，异步调用虽然提升了系统的响应速度，但是也会带来一致性问题，假如生产者发送消息后，由于某些问题导致业务逻辑执行出错，从而发生事务回滚，消费者没有正确地消费消息，这便会产生一致性问题。

<font color="orange"><b>主流的消息中间件有哪些？</b></font>

当前主流的消息中间件有：ActiveMQ、RabbitMQ、RocketMQ、Kafka、Apollo 等，不过应用最为广泛的还是 RabbitMQ、RocketMQ 以及 Kafka。这三种消息中间件也是各有各的优势：

<font color="blue">RabbitMQ</font>

优点：

- 基于 Erlang 开发，支持高并发
- 高可靠性，支持发送确认，投递确认等特性
- 高可用性，支持镜像队列
- ...

缺点：

- Erlang 语言较为小众，不利于二次开发
- 代理架构下，中央节点增加了延迟，影响性能
- 使用 AMQP 协议，使用起来有一定学习成本
- ...

<font color="blue">RocketMQ</font>

优点：

- 基于 Java，方便二次开发
- 单机吞吐量为十万级，比 RabbitMQ 还高出一个量级
- 高可用性，采用分布式架构
- 高可靠性，经过参数优化配置，消息可做到 0 丢失
- ...

缺点：

- 支持的客户端语言不多，较为成熟的有 Java，C++ 以及 Go
- 没有 Web 管理界面，仅提供一个 CLI 
- ...

<font color="blue">Kafka</font>

优点：

- 原生的分布式系统
- 使用零拷贝技术，吞吐量高
- 快速持久化；可以在 O(1) 的系统开销下进行消息持久化
- 支持数据批量发送和拉取
- ... 

缺点：

- 单机超过 64 个队列/分区时，性能会出现明显劣化
- 使用短轮询方式，实时性取决于轮询间隔时间
- 消费失败不支持重试
- ...


那么该如何选择这些消息中间件呢？

Kafka 一般用于追求高吞吐量的业务， 它非常适合配合大数据类的系统进行实时数据计算，日志采集的场景；RocketMQ 则可用于追求高可靠性的场景中，譬如电商领域，在双十一这种会发生大量交易涌入，需要进行业务的削峰限流，并且对可靠性要求很高时，我们可以选择使用 RocketMQ；RabbitMQ 比起 Kafka 与 RocketMQ 可以说是“麻雀虽小，五脏俱全”，它比较适合数据量没有那么大，但要求功能完备的公司，虽然 RabbitMQ 基于 erlang 开发，但是 RabbitMQ 的社区活跃度很高，更新维护速度也快。

我们的 Java 面试八股文之消息队列篇便会以 RabbitMQ 这个中间件为主，进行面试题的分析与讲解～

### 2、什么是 AMQP 协议？请描述 Direct，Fanout，Topic 三种 Exchange 的区别？
<hr>

<font color="orange"><b>什么是 AMQP 协议？</b></font>

![](https://files.mdnice.com/user/19026/be6248dd-b129-49b8-8c03-0125d63d4c52.png)

AMQP(Advanced Message Queuing Protocol) 是一套为面向消息中间件而设计的协议，基于此协议，客户端和消息中间件之间可以进行通讯。

我们熟知的消息中间件：RabbitMQ 则是 AMQP 协议的一个实现；而 AMQP 协议则规定了 RabbitMQ 对外的接口规范。

AMQP 模型图如下所示：

![](https://files.mdnice.com/user/19026/83a0f073-cfd0-42d6-8146-844801748367.png)

关于模型图中的名词解释：

<font color="blue">1. Publisher 与 Consumer</font>

Publisher，也可以叫做 Producer；它是消息的生产者，也是消息的发送者。

Consumer 则是消息的消费者。

Publisher 会将消息发送给交换机（Exchange），交换机会根据消息的路由键（Routing Key）以及交换机与队列的绑定关系（Binding）将消息路由转发到相应的队列（Queue）中，并由消费者（Counsumer）进行消费。

<font color="blue">2. Routing Key</font>

Routing Key 即路由键，生产者将消息发送给交换机时，会指定一个 Routing Key，Routing Key 决定消息去往哪里。通俗解释的话，如果我们将 Publisher 形象地比作快递发件方，消息比作快递，那么 Routing Key 就相当于快递邮寄的地址。

<font color="blue">3. Message Broker 与 Virtual Host </font>

Message Broker 是用于接收和分发消息的应用；譬如，RabbitMQ 就是一个 Message Broker。

Virtual Host 则是虚拟的 Broker，用于将内部多个单元划分隔开。

<font color="blue">4. Connection 与 Channel </font>

Connection 是 Publisher/Consumer 与 Broker 之间的 TCP 连接。

Channel 是 Connection 内部建立的逻辑连接，通常每个线程创建一个单独的 Channel。

<font color="blue">5. Exchange </font>

Exchange 即：交换机；它是 AMQP 协议中最为重要的组件，并承担着最核心的功能——路由转发；如果沿用上面的例子来说明的话，Exchange 则相当于快递的分拨中心。Publisher 将消息发送至 Exchange 交换机，Exchange 交换机通过 Routing Key 和 Queue 与 Exchange 的绑定关系（Binding），将消息路由到对应的队列中。

<font color="blue">6. Binding </font>

Binding 是 Exchange 与 Queue 之间的虚拟连接，是消息分发的依据。在绑定多个 Queue 到同一个 Exchange 时，这些 Binding 允许使用相同的 Binding Key。

<font color="blue">7. Queue </font>

Queue 即队列，消息最终会被消费者从队列中取走并消费。

<font color="orange"><b>请描述 Direct，Fanout，Topic 三种 Exchange 模式的区别？</b></font>

Exchange 是 AMQP 协议与 RabbitMQ 的核心组件，我们在上文提到了 Exchange 的功能就是根据 Routing Key 与绑定关系（Binding）将消息路由发送至相应的队列中。

最常用的三种 Exchange 模式为：

- Direct
- Fanout 
- Topic

接下来，我们就一起看一下这三种 Exchange 的区别。

<font color="blue">1. Direct Exchange</font>

当消息携带的 Routing Key 和交换机与队列的 Binding Key 一致时，Direct Exchange 则将消息分发到对应的队列中。

![](https://files.mdnice.com/user/19026/cea69cf5-4e0e-4e92-8d0d-9382999b83bd.gif)

如上面的动图所示：Exchange 的类型为 Direct，生产者发送的消息携带的 Routing Key 为 “orange”，消息只会流向 Binding Key 同样为 “orange” 的队列 “q1”。

题外话 🌺：

我使用的工具为 RabbitMQ Simulator，它是一个可以模拟 RabbitMQ 消息队列发送消息的在线工具。RabbitMQ Simulator 可以构建出消息发送拓扑图，进而帮助我们迅速理解不同的 Exchange 模型，以及整个 AMQP 模型。

链接 🔗：http://tryrabbitmq.com/

同时也向大家强烈推荐一个视频转 GIF 的在线免费工具 ezgif。

链接 🔗：https://ezgif.com/

<font color="blue">2. Fanout Exchange</font>

fanout 翻译为扇形展开，顾名思义，Fanout Exchange 可以和任意的多个队列绑定起来，无论绑定在消息上的 Routing Key 是什么，当消息发送至 Fanout Exchange 时，该消息则会被拷贝并路由到所有绑定到该交换机上的队列中，这一种方式也就是我们俗称的广播方式，非常容易理解。

![](https://files.mdnice.com/user/19026/2d482528-9cb0-46de-8f67-e1a934daf8d8.gif)

<font color="blue">3. Topic Exchange</font>

Topic Exchange 是应用最为灵活的一种 Exchange 模式，它会根据 Routing Key 以及通配规则，将消息路由发送到匹配的 Queue 中。

通配规则为：

1. 全匹配，全匹配和 Direct Exchange 相同
2. Binding Key 中，`#` 表示可以匹配任意个数的单词
3. Binding Key 中，`*` 表示可以匹配任意一个单词

譬如下面的动图所示，如果一条消息携带的 Routing Key 为 `kim.orange.fly`，那么这条消息将被发送到 “q1” 队列中，因为 “q1” 的 Binding Key 为 `*.orange.*`，根据通配规则，“q1” 的 Binding Key 与 Routing Key 匹配；如果一条消息携带的 Routing Key 为 `lazy.kim.boy`，那么这条消息将被发送到 “q3” 队列中，因为 “q3” 的 Binding Key 为 `lazy.#`，根据通配规则，“q3” 的 Binding Key 与 Routing Key 匹配。

![](https://files.mdnice.com/user/19026/c44fd086-3f31-4dfa-b3d4-3b8ab56549d2.gif)



### 3、RabbitMQ 如何保证消息的可靠性？
<hr>

RabbitMQ 该如何保证一条消息的可靠性呢？

有以下几点：

- AMQP 事务与发送端的 Confirm 机制保证了生产者的消息是否有成功发送到 RabbitMQ 服务器
- RabbitMQ 的消息返回机制保证了消息是否可以被正确路由
- 消费端的手动 Confirm 机制保证消息是否从 RabbitMQ 队列成功发送至消费端，并被消费端消费
- RabbitMQ 的消费端限流机制限制了消息推送速度，保证了消息接收端服务的稳定
- 消息 TTL 机制保证了 RabbitMQ 服务器不会有大量消息堆积而导致其崩溃
- 死信队列保证了被 RabbitMQ 丢弃的消息可以被收集，以提供运维人员分析

<font color="orange"><b>AMQP 事务与发送端的 Confirm 机制</b></font>

对于消息的发送方，即生产者来说，需要知道一条消息在被发送后，是否有正确到达 Broker 代理服务器，RabbitMQ 有两种方式可以解决这一问题：

1. AMQP 事务
2. 发送端的 Confirm 机制

<font color="blue">1. AMQP 事务</font>

AMQP 协议自身提供了一种保证消息投递成功的事务模式，通过信道 Channel，我们可以开启事务，提交事务，当发生异常时，可以回滚事务：

- `channel.txSelect()`：开启事务
- `channel.txCommit()`：提交事务
- `channel.txRollback()`：回滚事务

来看下示例代码：
```java
@Slf4j
public class RabbitTx {

    public static final String QUEUE = "queue.test";

    public static final String ROUTING_KEY = "key.test";

    public static final String EXCHANGE = "exchange.test";

    public static void sendMsg() throws Exception {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        // 声明 Exchange
        channel.exchangeDeclare(
                EXCHANGE, BuiltinExchangeType.DIRECT,
                true,
                false,
                null
        );

        // 声明 Queue
        channel.queueDeclare(
                QUEUE,
                true,
                false,
                false,
                null
        );
        // Queue 与 Exchange 绑定
        channel.queueBind(
                QUEUE,
                EXCHANGE,
                ROUTING_KEY
        );

        try {
            // 开启事务
            channel.txSelect();
            // 发送消息
            String message = "hello world";
            channel.basicPublish(
                    EXCHANGE,
                    ROUTING_KEY,
                    null,
                    message.getBytes());
            // 提交事务
            channel.txCommit();

        } catch (Exception e) {
            log.error(e.getMessage());
            // 发生异常，回滚
            channel.txRollback();
        } finally {
            channel.close();
            connection.close();
        }
    }

    public static void main(String[] args) throws Exception {
        sendMsg();
    }
}
```
运行程序，使用 WireShark 抓包的结果如下所示：

![](https://files.mdnice.com/user/19026/67e037c8-fd02-4d8f-b893-9a59e2fae048.png)

通过结果显示，我们在发送消息的前后多出了开启事务与提交事务的步骤。

假如在消息发送之后，事务提交之前，程序发生了异常导致事务回滚，队列还能收到消息么？

目前队列 `queue.test` 中没有任何消息。 

![](https://files.mdnice.com/user/19026/a9ba14df-1801-4d71-867f-c173438c3b1f.png)

我们在 `channel.basicPublish()` 与 `channel.txCommit()` 之间写了一段会抛出异常的代码
```java
try {
    // 开启事务
    channel.txSelect();
    // 发送消息
    String message = "hello world";
    channel.basicPublish(
            EXCHANGE,
            ROUTING_KEY,
            null,
            message.getBytes());
    // 抛出异常
    Integer.valueOf("abc");
    // 提交事务
    channel.txCommit();
            
} catch (Exception e) {
    log.error(e.getMessage());
    // 发生异常，回滚
    channel.txRollback();
} finally {
    channel.close();
    connection.close();
}
```
运行程序，使用 WireShark 抓包的结果如下所示：

![](https://files.mdnice.com/user/19026/d6cb9be1-b6c7-4b1e-9b11-bfb9ec4af950.png)

结果显示，我们在发送消息后由于程序抛出异常，所以消息并没有被提交，而是发生了事务回滚，此时队列 `queue.test` 中仍然显示收到的消息数为 0：

![](https://files.mdnice.com/user/19026/5abbddbc-ddd0-49d5-9db9-7e3340cb4cb1.png)

通过上面的例子，我们知道，AMQP 事务模型可以解决判断生产者的消息是否有成功发送到 Broker 的问题。事务提交成功则意味着消息被 Broker 成功接收；而一旦发生事务回滚，则意味着消息发送至 Broker 失败，进而，发送方可以做出相应的处理措施，对消息进行重发。

不过 AMQP 事务的性能非常差，这种方式我们需要了解，但并不推荐使用。

<font color="blue">2. 发送端的 Confirm 机制</font>

发送端有三种 Confirm 机制，来确认消息是否成功发送到 RabbitMQ，这三种确认机制为：

- 单条同步确认
- 多条同步确认
- 异步确认

<font color="purple">2.1 单条同步确认</font>

单条同步确认模式为：消息发送端每发送一条消息至 RabbitMQ 成功，服务端就会回传给发送端一条同步确认（ACK），如果服务端超时未返回则说明消息发送失败。

关键代码为：

- `channel.confirmSelect()`：开启 Confirm
- `channel.waitForConfirms()`：等待服务端返回 ACK

来看下示例代码：
```java
@Slf4j
public class SingleConfirm {

    public static final String QUEUE = "queue.test";

    public static final String ROUTING_KEY = "key.test";

    public static final String EXCHANGE = "exchange.test";

    public static void sendMsg() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            // 声明 Exchange
            channel.exchangeDeclare(
                    EXCHANGE, BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            // 声明 Queue
            channel.queueDeclare(
                    QUEUE,
                    true,
                    false,
                    false,
                    null
            );
            // Queue 与 Exchange 绑定
            channel.queueBind(
                    QUEUE,
                    EXCHANGE,
                    ROUTING_KEY
            );
            // 开启 Confirm
            channel.confirmSelect();
            for (int i = 0; i < 3; i++) {
                String message = "第" + i + "条消息";
                channel.basicPublish(
                        EXCHANGE,
                        ROUTING_KEY,
                        null,
                        message.getBytes());
                // 等待同步确认
                if (channel.waitForConfirms()) {
                    log.info("消息发送成功");
                } else {
                    log.error("消息发送失败");
                    // TODO message resend
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        sendMsg();
    }
}
```
代码十分简单，程序内，我们循环三次，让生产者发消息，每发一条消息都等待 Broker 的同步确认。

运行程序，使用 WireShark 抓包的结果如下所示：

![](https://files.mdnice.com/user/19026/a59b728d-f61e-4270-a811-55ecd7c3cff6.png)

我们可以看到，生产者每发送一条消息，都会收到 Broker 回传的同步确认（Basic.Ack），如果没有收到，则意味着消息发送失败，生产者便不会继续发送下一条消息。

<font color="purple">2.2 多条同步确认</font>

多条同步确认模式与单条同步确认模式的不同点在于：单条同步确认是生产者每发一条消息，就要确认一次，收到 ACK 后再发送下一条；而多条同步确认模式是批量发送消息，然后再进行确认。其使用方法与单条同步确认无任何区别。

这样做的优点是，多条同步确认相比于单条同步确认提升了处理消息的效率；缺点也很明显，一旦 `channel.waitForConfirms()` 方法返回了 `false`，那么生产者就需要将这一批次的消息全部进行重发，不仅效率没有提升，反而降低了系统的性能。

来看下示例代码：
```java
@Slf4j
public class BatchConfirm {

    public static final String QUEUE = "queue.test";

    public static final String ROUTING_KEY = "key.test";

    public static final String EXCHANGE = "exchange.test";

    public static void sendMsg() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            // 声明 Exchange
            channel.exchangeDeclare(
                    EXCHANGE, BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            // 声明 Queue
            channel.queueDeclare(
                    QUEUE,
                    true,
                    false,
                    false,
                    null
            );
            // Queue 与 Exchange 绑定
            channel.queueBind(
                    QUEUE,
                    EXCHANGE,
                    ROUTING_KEY
            );
            // 开启 Confirm
            channel.confirmSelect();
            for (int i = 0; i < 3; i++) {
                String message = "第" + i + "条消息";
                channel.basicPublish(
                        EXCHANGE,
                        ROUTING_KEY,
                        null,
                        message.getBytes());
            }
            // 等待同步确认
            if (channel.waitForConfirms()) {
                log.info("消息发送成功");
            } else {
                log.error("消息发送失败");
                // TODO message resend
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        sendMsg();
    }
}
```
运行程序，使用 WireShark 抓包的结果如下所示：

![](https://files.mdnice.com/user/19026/4ec79e96-c051-4880-97e7-c81a30328ab1.png)

可以看到，生产者连续发送了三条消息，待消息全部发送完毕才会收到 Broker 回传的同步确认（Basic.Ack）。

<font color="purple">2.3 异步确认</font>

异步确认模式与同步确认的不同点在于，异步确认模式的发消息与确认是相互独立的事件，对于同步确认来说，无论是单条同步还是多条同步，消息发送者都需要发送消息，并等待确认后，才能发送下一条或下一批次的消息；而异步就不同了，发送消息与确认消息两个事件完全分离，发送消息的线程只管发消息，而异步线程则负责确认，判断消息发送成功还是失败。

来看下示例代码：
```java
@Slf4j
public class AsyncConfirm {
    public static final String QUEUE = "queue.test";

    public static final String ROUTING_KEY = "key.test";

    public static final String EXCHANGE = "exchange.test";

    public static void sendMsg() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            // 声明 Exchange
            channel.exchangeDeclare(
                    EXCHANGE, BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            // 声明 Queue
            channel.queueDeclare(
                    QUEUE,
                    true,
                    false,
                    false,
                    null
            );
            // Queue 与 Exchange 绑定
            channel.queueBind(
                    QUEUE,
                    EXCHANGE,
                    ROUTING_KEY
            );
            
            // 开启 Confirm
            channel.confirmSelect();

            ConfirmListener confirmListener = new ConfirmListener() {

                // 确认成功，将调用 handleAck
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    // deliveryTag 为发送端的消息序号
                    // multiple 为 true 时，说明确认的是多条消息；为 false 时，说明确认的是单条消息
                    log.info("Ack, deliveryTag:{},multiple:{}", deliveryTag, multiple);
                }

                // 确认失败，将调用 handleNack
                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    log.info("Nack, deliveryTag:{},multiple:{}", deliveryTag, multiple);
                }
            };

            channel.addConfirmListener(confirmListener);

            for (int i = 0; i < 10; i++) {
                String message = "第" + i + "条消息";
                channel.basicPublish(
                        EXCHANGE,
                        ROUTING_KEY,
                        null,
                        message.getBytes());
            }

            Thread.sleep(10000);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        sendMsg();
    }
}
```
执行程序，输出结果为：
```text
[AMQP Connection 127.0.0.1:5672] INFO producter.AsyncConfirm - Ack, deliveryTag:5,multiple:true
[AMQP Connection 127.0.0.1:5672] INFO producter.AsyncConfirm - Ack, deliveryTag:9,multiple:true
[AMQP Connection 127.0.0.1:5672] INFO producter.AsyncConfirm - Ack, deliveryTag:10,multiple:false
```
从程序的输出结果来看，我们可以知道，生产者将这十条消息分成三个批次发出且成功收到了三次确认，前两个批次为一次性发送多条消息，最后一个批次单独发送了一条消息（multiple 为 fasle）。

使用 WireShark 抓包的结果如下所示：

![](https://files.mdnice.com/user/19026/abcabc2f-2923-4bb0-b09f-47b13ab3238b.png)

看过代码后，我们先来思考下异步确认模式的缺点。

假如消息发送失败，要如何处理？我们知道发送消息和消息确认是两个不同的线程，假如某一条消息发送失败，那么在消息确认的异步线程中的 `handleNack()` 方法里，我们就要对发送失败的消息进行重发。可是，异步线程如何才能知道发送失败的消息的具体内容是什么呢？

这里面我们就需要采用一些可以让线程之间进行通信的方法，譬如发送消息的时候，我们可以将消息的 deliveryTag 和消息体存入到数据库中，这样在负责消息确认的异步线程中，我们就可以从数据库里查到这条消息的具体内容了。

不过，这样的操作无疑增加了系统的复杂性，而这便是异步确认模式不太常用的原因，也是它的缺点所在。

异步确认模式的优点就不用多说了，首先它的性能必然要比单条同步确认要好，也是远远高于 AMQP 事务的，其次它对失败消息的重发效率也比多条同步确认高。

<font color="blue">3. 如何保证消息发送成功</font>

通过上文的讲解，我们已经知道这个问题的答案了，如何保证生产者的消息可以到达 RabbitMQ 服务器或者说是 RabbitMQ Broker 呢？

有两种方法：

- 第一种是通过 AMQP 事务
- 第二种是通过 RabbitMQ 消息发送端的 Confirm 模式（单条，多条，异步）

推荐使用单条同步确认，虽然它的性能不是最优的，但是其原理简单，不易出错。

<font color="orange"><b>消息返回机制</b></font>

当一条消息发送到 Broker 后，Exchange 交换机会根据这条消息携带的 Routing Key，和 Queue 与 Exchange 的绑定关系（Binding），将消息路由到正确的队列中。如果这条消息的 Routing Key 不能匹配任何与该 Exchange 绑定的队列的 Binding Key，将无法去往任何一个队列，我们则称这条消息为一条不可达的消息。

消息返回机制正是一种维系不可达的消息与生产者关系的保障策略，通俗解释的话：消息返回机制就是一种监听机制，它监听生产者发送到 Broker 的消息是否可达，如果消息不可达，就会返回一个信号通知消息发送端，消息发送端便可以做出相应的处理；反之，如果消息被正确路由，则不会返回任何信号。

开启消息返回机制的关键代码：

```java
// 开启监听
// replyCode：类似于 HTTP 返回码，用于表示消息路由结果的字码
// replyText：返回信息
channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
    log.info("Message Return");
    // TODO:说明消息不可达，可执行对应的操作，譬如告警，发邮件通知运维人员等等
});
// 发送消息
channel.basicPublish(
    "exchange",
    "routingKey",
    true,
    null,
    message.getBytes()
);
```
首先，我们要为 channel 添加一个 `ReturnListener`，即：消息返回监听器，当消息不可达时，程序便会回调监听器的 `handleReturn` 方法（异步回调），在该方法中，我们可以获取到消息路由结果的字码，返回信息，Exchange 名称，Routing Key 等内容；然后我们需要在 `basicPublish` 方法中，将 Mandtory 对应的参数项设置为 true，Mandatory 设置为 false 时，RabbitMQ 将直接丢弃无法被路由的不可达消息；而 Mandatory 设置为 true 时，RabbitMQ 便会处理不可达的消息。

示例代码：
```java
@Slf4j
public class ReturnListenerTest {

    public static final String QUEUE = "queue.test";

    public static final String ROUTING_KEY = "key.test";

    public static final String WRONG_ROUTING_KEY = "key.wrong";

    public static final String EXCHANGE = "exchange.test";

    public static void sendMsg() {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            // 声明 Exchange
            channel.exchangeDeclare(
                    EXCHANGE, BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            // 声明 Queue
            channel.queueDeclare(
                    QUEUE,
                    true,
                    false,
                    false,
                    null
            );
            // Queue 与 Exchange 绑定
            channel.queueBind(
                    QUEUE,
                    EXCHANGE,
                    ROUTING_KEY
            );

            String message = "send message";

            // 开启监听
            // replyCode：类似于 HTTP 返回码，用于表示消息路由结果的字码
            // replyText：返回信息
            channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
                log.info("-------- Message Return --------");
                log.info("replyCode:{}", replyCode);
                log.info("replyText:{}", replyText);
                log.info("exchange:{},routingKey:{}", exchange, routingKey);
                // TODO:说明消息不可达
            });

            // 发送消息
            channel.basicPublish(
                    EXCHANGE,
                    WRONG_ROUTING_KEY,
                    true,
                    null,
                    message.getBytes());

            Thread.sleep(10000);


        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        sendMsg();
    }
}
```
在示例程序中，消息携带的 Routing Key 为我们定义的 `WRONG_ROUTING_KEY`，Exchange 与队列绑定的 Binding Key 为我们定义的 `ROUTING_KEY`，同时我们使用的 Exchange 模式为 Direct，所以生产者发送的消息将无法被路由。运行程序，输出结果为：
```text
[AMQP Connection 127.0.0.1:5672] INFO producter.ReturnListenerTest - -------- Message Return --------
[AMQP Connection 127.0.0.1:5672] INFO producter.ReturnListenerTest - replyCode:312
[AMQP Connection 127.0.0.1:5672] INFO producter.ReturnListenerTest - replyText:NO_ROUTE
[AMQP Connection 127.0.0.1:5672] INFO producter.ReturnListenerTest - exchange:exchange.test,routingKey:key.wrong
```
RabbitMQ 的消息返回机制确保了消息可以被正确路由，如果出现路由异常，消息不会丢失。

<font color="orange"><b>消费端的确认机制</b></font>

通过上文，我们知道了，发送端的 Confirm 机制保证了消息是否从发送端成功发送到 RabbitMQ，那么我们自然也会想到，对于 RabbitMQ 与消费端之间，也必然存在一种机制，来确保消息是否从 RabbitMQ 队列成功发送至消费端，并被消费端消费。而这种机制就是消费端的 Confirm 机制。

默认情况下，消费端接收消息时，会自动回传给 RabbitMQ 一条 ACK，来通知 RabbitMQ，“我已经收到你的消息了”，在代码中的体现就是将消费者的 `basicConsume` 方法中的 `autoAck` 设置为 `true`:
```java
channel.basicConsume(QUEUE, true, deliverCallback, consumerTag -> {
});

DeliverCallback deliverCallback = ((consumerTag, message) -> {
    // do somthing ...
});
```
不过，将 `autoAck` 设置为 `true` 的这种做法却是不被推荐的。

<font color="blue">1. 将 autoAck 设置为 true 会有什么问题？</font>

我们要知道的是，RabbitMQ 在收到消费端发来的 ACK 后，会将消息从内存中移除。

那么试想这样一种情况，RabbitMQ 队列收到了消费端发送的 ACK 后，将消息从队列中移除，而在此时，消费端发生宕机，因为宕机，消费端没有正确处理这条消息，这样便会引起消息的丢失！

取而代之的做法就是，使用手动 ACK。

<font color="blue">2. 消费端的手动确认机制</font>

为了避免自动 ACK 带来的消息丢失问题，我们可以使用消费端的手动 ACK 机制。

当我们设置了手动 ACK 后， 消费端收到消息后不会自动“签收”，而是在我们的业务代码中显示地去进行“签收”；RabbitMQ 会等待消费者显式地回传确认信号，这样做的好处是，消费者将会有足够的时间去处理消息，等到消费者把这条消息“真正地”消费后，才去回传 ACK。大家可以去体会下“真正地”消费这句话的意思，为什么自动 ACK，不代表消费者真正地消费了消息呢？其原因在于消费端的 Confirm 机制本身就是异步的，有可能 RabbitMQ 已经收到了 ACK，但是消费端的业务逻辑还没有处理完毕。

在代码中的体现则是将消费者的 `basicConsume` 方法中的 `autoAck` 设置为 `false`；并且我们要在 `deliverCallback` 中，指定消费者的操作:
```java
channel.basicConsume(QUEUE, false, deliverCallback, consumerTag -> {
});

DeliverCallback deliverCallback = ((consumerTag, message) -> {
    // do somthing ...
    channel.basicAck(tag,multiple);
});
```

消费者可以进行的四种基本操作有：

- `basicAck`
- `basicRecover`
- `basicReject`
- `basicNack`

`basicAck` 为确认消息，方法中的第二个参数 `multiple` 为 `true` 时，表示批量确认操作，为 `false` 时，表示对单个消息进行确认操作；`basicRecover` 表示是否使消息重新恢复至队列中，`true` 表示重新发回到队列，并尽可能地将之前 recover 的消息投递给其他的消费者，`false` 则会使消息重新投递给自己；`basicReject` 为拒收消息，方法中的第二个参数 `requeue` 为 `true` 时，表示是否将拒收的消息重回队列，为 `false` 时，表示丢弃或将消息发送至死信队列；`basicNack` 为批量拒绝，方法参数中的 `multiple` 设置为 `true` 时，可以对消息进行批量拒绝。

接下来，我们来看一个示例程序：
```java
@Slf4j
public class ConsumerConfirm {

    public static final String QUEUE = "queue.test";

    public static final String ROUTING_KEY = "key.test";

    public static final String EXCHANGE = "exchange.test";

    public Channel channel;

    public static void main(String[] args) {
        ConsumerConfirm test = new ConsumerConfirm();
        test.doTest();
    }

    public void doTest() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            this.channel = channel;

            // 声明 Exchange
            channel.exchangeDeclare(
                    EXCHANGE, BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            // 声明 Queue
            channel.queueDeclare(
                    QUEUE,
                    true,
                    false,
                    false,
                    null
            );
            // Queue 与 Exchange 绑定
            channel.queueBind(
                    QUEUE,
                    EXCHANGE,
                    ROUTING_KEY
            );

            String message = "a message";

            // 生产者发送消息
            log.info("----- send message -----");
            log.info("message : {}", message);

            channel.basicPublish(
                    EXCHANGE,
                    ROUTING_KEY,
                    null,
                    message.getBytes());

            // 消费者消费消息
            channel.basicConsume(QUEUE, false, deliverCallback, consumerTag -> {
            });

            Thread.sleep(10000);

        } catch (Exception e) {
            log.error("error message : {}", e.getMessage());
        }
    }

    DeliverCallback deliverCallback = ((consumerTag, message) -> {
        try {
            String msgBody = new String(message.getBody());

            log.info("----- receive message -----");
            log.info("message : {}", msgBody);
            // TODO: 实现消费消息的业务逻辑
            // 消费端手动确认
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    });
}
```
在`channel.basicPublish()`，`channel.basicAck()` 这两处打上断点；这一次，我们将会使用 DEBUG 模式来运行程序，进行分析。

运行程序之前，我们的队列为空。

![](https://files.mdnice.com/user/19026/94f6e42e-027d-49c8-9cf1-b198e1cd2e82.png)

Ready 表示待消费的消息数量，即：还未发送给消费者的消息数量；Unacked 表示待应答的消息数量，即：消息已经发送给了消费者，但消费者还未返回 ACK；Total 则是二者的加和。

当程序执行完`channel.basicPublish()`方法后：

![](https://files.mdnice.com/user/19026/9348f779-ded8-48b4-8f7a-f868953988a4.png)

我们看到队列中已经存在了一条待处理的消息。

程序执行到 `channel.basicAck()` 方法前时：

![](https://files.mdnice.com/user/19026/c8da297f-dac2-40b5-b7e0-0ca95319178a.png)

我们看到，消息已经发送给消费者了，但是该消息处于一个等待消费者确认的状态。

执行完 `channel.basicAck()` 方法后，队列中就没有待处理的消息了。

通过本小节的学习，想必大家对消费端的确认机制已经有了一个良好的认识。示例中，我只给出了 `basicAck` 的代码及断点流程分析，大家可以自行尝试，体会一下 `basicReject`，`basicRecover`，`basicNack` 这些操作分别都是怎样的～

<font color="orange"><b>消费端限流，TTL，死信队列</b></font>

<font color="blue">1. 消费端限流机制</font>

为什么要对消费端做限流处理？

试想这样一种场景：在业务高峰期时，由于消息发送端与消息接收端性能的不一致（发送端推送消息的速度远大于消费端处理消息的速度），导致大量的消息被一次性推送给消息接收端，从而造成消息接收端服务发生崩溃。消息接收端服务宕机下线后，期间消息队列积压了大量的消息。那么当这个微服务重新上线后，又一次性收到了大量的消息，导致继续崩溃... 

针对以上问题，RabbitMQ 提供了一种 QoS（Quality of Service）即：服务质量保障功能。它的原理是在**非自动确认消息开启的前提下**，当消费端有一定数量的消息未被 ACK 确认时，RabbitMQ 将不会给消费端推送新的消息。

我们在消费端的代码中只需开启非自动确认，并使用 `channel.basicQos()` 方法即可开启 QoS 功能。

```java
void basicQos(int prefetchSize, int prefetchCount, boolean global) throws IOException;
void basicQos(int prefetchCount, boolean global) throws IOException;
void basicQos(int prefetchCount) throws IOException;
```
`basicQos` 有三种重载方法，这些参数的含义为：

- `prefetchCount`：表示针对一个消费端最多可以推送多少未 ACK 确认消息
- `global`：当其值设置为`true`时，会针对整个消费端进行限流，当值为`false`时，仅针对当前的 channel 进行限流
- `prefetchSize`：单条消息大小限制，一般设置为 0，代表不限制

需要注意的是：带有 `prefetchSize` 与 `global` 这两个参数项的方法， RabbitMQ 暂时没有实现（仅在 AMQP 协议中定义了接口）。 

接下来，我们来看一个示例程序：
```java
@Slf4j
public class QoS {

    public static final String QUEUE = "queue.qos.test";

    public static final String ROUTING_KEY = "key.qos.test";

    public static final String EXCHANGE = "exchange.qos.test";

    public Channel channel;

    public static void main(String[] args) {
        QoS test = new QoS();
        test.doTest();
    }

    public void doTest() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            this.channel = channel;

            // 声明 Exchange
            channel.exchangeDeclare(
                    EXCHANGE, BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            // 声明 Queue
            channel.queueDeclare(
                    QUEUE,
                    true,
                    false,
                    false,
                    null
            );
            // Queue 与 Exchange 绑定
            channel.queueBind(
                    QUEUE,
                    EXCHANGE,
                    ROUTING_KEY
            );

            String message = "a message";

            // 生产者发送消息
            log.info("----- send message -----");
            for (int i = 0; i < 10; i++) {
                channel.basicPublish(
                        EXCHANGE,
                        ROUTING_KEY,
                        null,
                        message.getBytes());
            }
            
            // 消费者消费消息
            channel.basicConsume(QUEUE, false, deliverCallback, consumerTag -> {
            });

            Thread.sleep(100000);

        } catch (Exception e) {
            log.error("error message : {}", e.getMessage());
        }
    }

    DeliverCallback deliverCallback = ((consumerTag, message) -> {
        try {
            String msgBody = new String(message.getBody());

            log.info("----- receive message -----");
            log.info("message : {}", msgBody);
            Thread.sleep(5000);
            // 消费端手动确认
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    });
}
```

为了模拟消费者消费消息的速度远远落后于生产者推送消息的速度,我在 `deliverCallback` 中，使用了 `Thread.sleep(5000)`  。

在未开启消费端限流 QoS 功能时，运行程序：

![](https://files.mdnice.com/user/19026/12598561-dd12-46e9-bfdd-3beffff375af.png)

在监控台中，我们可以看到，生产者发送到队列的十条消息，已经全部由 RabbitMQ 一次性地发送给消费端了（Unacked 表示待应答的消息数量，即：消息已经发送给了消费者，但消费者还未返回 ACK）。

我们在代码中添加 QoS 功能开启：
```java
// ... ...
// 消费端限流机制 QoS
channel.basicQos(2);
// 消费者消费消息
channel.basicConsume(QUEUE, false, deliverCallback, consumerTag -> {
});
// ... ...
```
`basicQos(2)` 表示消费端最多可以处理 2 条未 ACK 确认的消息。

重新运行程序：

![](https://files.mdnice.com/user/19026/cde9c5b6-a121-4faf-a564-1f8ed9ab7882.png)

我们看到，正如我们所想，消费者最多可以处理 2 条消息，而其余的消息仍然在队列中未发送给消费者（Ready 表示待消费的消息数量，即：还未发送给消费者的消息数量）。

QoS 的重要意义不仅仅在于可以保护消费端，同时它也有利于消费端的横向扩展，使得消费端可以分布式处理堆积的消息。

假如我们没有设置 QoS 功能，仅启动了一个消费者，那么堆积的消息仍然已经全部发送给了这个消费者（消息状态为 Unacked）。就算我想启动更多的消费者去处理这些堆积的消息也无济于事。

反之，如果我们开启了 QoS 功能，堆积的消息仍然在队列中处于 Ready 的状态，这时，我们就可以横向扩展更多的消费者帮忙处理这些消息了～

<font color="blue">2. 消息过期机制</font>

在默认的情况下，当消息进入到队列后，会永久存在，直到被消费。但是这样一来，就会造成 RabbitMQ 产生大量的消息堆积，这给 RabbitMQ 自身造成了很大的压力。为了解决这个问题，RabbitMQ 支持对发送的消息设置过期时间（我们简称：消息 TTL），以及对整个队列设置消息的过期时间（我们简称：队列 TTL）。TTL 的机制是当消息过期时，如果有设置死信队列，那么这条消息会被转发到专门接收死信的队列中，如果没有设置死信队列，那么消息会被队列永久移除。

<font color="purple">2.1 消息 TTL</font>

用法：
```java
// 设置单条消息 TTL 为 1 min
AMQP.BasicProperties properties = new AMQP.BasicProperties()
        .builder()
        .expiration("60000")
        .build();


channel.basicPublish(
        EXCHANGE,
        ROUNTING_KEY,
        properties,
        message.getBytes()
);
```
如代码所见，我们只需要在 `channel.basicPublish` 方法中，对 `properties` 参数设置 `expiration` 属性即可，单位为毫秒，示例代码中，我们为发送的消息设置的 TTL 为 1 分钟。



<font color="purple">2.2 队列 TTL</font>

用法：
```java
Map<String, Object> args = new HashMap<>(16);
args.put("x-message-ttl", 60000);
// x-expire 为队列的存活时间，如果在一定的时间内，队列没有接收到消息，队列会被删除；不要加入这样一个参数
// args.put("x-expire",60000);
// 声明队列 Queue
channel.queueDeclare(
        QUEUE,
        true,
        false,
        false,
        args
);
```
如代码所见，我们可以在声明队列的 `channel.queueDeclare` 方法中，设置 `args` 参数，`args` 是一个 `Map`，我们为其加入 `x-message-ttl` 这个键，对应的值就是规定队列中的消息的 TTL，单位为毫秒。

需要注意的是，`args` 还有一个 `x-expire` 的键，
它特别容易和 `x-message-ttl` 搞混。`x-expire` 是为队列设置一个存活时间，如果在这个时间内，队列没有接收到任何消息，那么整个队列会自动删除。我们在开发代码中尽量不要直接删除队列，这不是一个值得提倡的做法。

还有一个问题是，如果我们既给消息设置了 TTL，也给队列设置了 TTL，究竟哪个会起作用呢？

答案很简单，自然是按照最短的时间来啦～ 木桶效应，哪个先到时间，哪个起作用。


<font color="blue">3. 死信队列</font>

我先来解释一下，什么叫做死信（Dead Message）。

当一条消息在队列中出现以下三种情况时，就会被队列标记成一条死信：

1. 消息被拒绝（reject/nack），并且 `requeue = false`
2. 消息过期
3. 队列达到了最大长度

当一条消息成为死信后，就会被队列直接丢弃。但是，运维人员并不希望这些消息被直接丢弃，而是希望收集这些消息，找到它们被丢弃的原因。而死信队列便是做这样一件事的～

RabbitMQ 支持为队列配置死信队列，当一条消息在队列中变成死信后，队列不会将其直接删除，而是将这条死信重新推送到死信交换机（Dead-Letter Exchange）上，死信交换机和普通的交换机没有任何区别，只不过它的作用是用来专门处理死信的。死信交换机会将死信投递到与其绑定的队列中，这个队列就是死信队列（Dead-Letter Queue），当然死信队列和普通的队列没有任何区别，只不过它的作用是用来专门接收死信交换机路由传递的死信而已。


![](https://files.mdnice.com/user/19026/366aa8b1-912c-43ce-8f86-a5f105c7b850.png)


死信队列的设置方法：

1. 设置转发与接收死信的死信交换机和死信队列
    - Exchange：`channel.exchangeDeclare(DL-exchangeName)`
    - Queue：`channel.queueDeclare(DL-queueName)`
    - RoutingKey：`#`；如果我们使用 Topic Exchange，当 RoutingKey 设置为 `#` 时，死信队列则可以接收任何消息
2. 在需要设置死信的队列中加入参数
    - `x-dead-letter-exchange = DL-exchangeName`

示例代码：
```java
@Slf4j
public class TestDeadLetter {

    public static final String QUEUE = "queue.letter.test";

    public static final String ROUTING_KEY = "key.letter.test";

    public static final String EXCHANGE = "exchange.letter.test";

    public static final String DEAD_EXCHANGE = "exchange.dlx";

    public static final String DEAD_QUEUE = "queue.dlx";

    public Channel channel;

    public static void main(String[] args) {
        TestDeadLetter test = new TestDeadLetter();
        test.doTest();
    }

    public void doTest() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            this.channel = channel;

            // 声明死信交换机
            channel.exchangeDeclare(
                    DEAD_EXCHANGE,
                    BuiltinExchangeType.TOPIC,
                    true,
                    false,
                    null
            );

            // 声明死信队列
            channel.queueDeclare(
                    DEAD_QUEUE,
                    true,
                    false,
                    false,
                    null
            );

            // 将死心队列和死信交换机进行绑定
            channel.queueBind(
                    DEAD_QUEUE,
                    DEAD_EXCHANGE,
                    "#"
            );

            // 声明 Exchange
            channel.exchangeDeclare(
                    EXCHANGE, BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null
            );

            Map<String, Object> args = new HashMap<>(16);
            // 设置队列 TTL 为 10 s
            args.put("x-message-ttl", 10000);
            // 绑定在死信交换机
            args.put("x-dead-letter-exchange", DEAD_EXCHANGE);
            // 声明 Queue
            channel.queueDeclare(
                    QUEUE,
                    true,
                    false,
                    false,
                    args
            );
            // Queue 与 Exchange 绑定
            channel.queueBind(
                    QUEUE,
                    EXCHANGE,
                    ROUTING_KEY
            );

            String message = "a message";

            // 生产者发送消息
            log.info("----- send message -----");

            channel.basicPublish(
                    EXCHANGE,
                    ROUTING_KEY,
                    null,
                    message.getBytes());

            Thread.sleep(15000);

            int messageCountInLetterTestQueue = channel.queueDeclarePassive(QUEUE).getMessageCount();
            log.info("queue.letter.test 队列中的消息数为：{}", messageCountInLetterTestQueue);

            int messageCountInDeadQueue = channel.queueDeclarePassive(DEAD_QUEUE).getMessageCount();
            log.info("queue.dlx 队列中的消息数为：{}", messageCountInDeadQueue);
            
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error message : {}", e.getMessage());
        }
    }
}
```
在示例程序中，我们设置消息在队列的存活时间为 10 秒，在消息发送后，没有消费者去消费该消息，那么这条消息将会被标记为死信，转发到死信交换机并路由转发到死信队列。

程序运行后，输出如下：
```text
[main] INFO mq.TestDeadLetter - ----- send message -----
[main] INFO mq.TestDeadLetter - queue.letter.test 队列中的消息数为：0
[main] INFO mq.TestDeadLetter - queue.dlx 队列中的消息数为：1
```
通过管控台，我们也可以看到，死信队列中已经存入了一条死信：

![](https://files.mdnice.com/user/19026/8ece4e05-b266-47bc-a30d-3abffbd4c0a4.png)

## 消息队列篇（二）

### 1. 什么是 Spring-AMQP？
<hr>

Spring-AMQP 是 Spring 对 AMQP 协议的封装与扩展，它将 Spring 的核心概念应用于基于 AMQP 的消息传递解决方案中，使得开发者可以通过 Spring-AMQP 更简单方便地完成声明组件（队列，交换机等），收发消息等工作。

Spring-AMQP 是一个抽象层，不依赖于特定的 AMQP Broker 的实现，这样做的好处在于，可以使用户只针对抽象层来进行开发，而不用关心底层具体的实现是什么。

本篇文章内容基于 `spring-boot-starter-amqp:2.7.5` 创作。

### 2. RabbitAdmin
<hr>

<font color="orange"><b>RabbitAdmin 是什么？</b></font>

RabbitAdmin 是 Spring-AMQP 中的核心组件。顾名思义，RabbitAdmin 是用来管理 RabbitMQ 的，其主要功能包括：

- `declareExchange`：创建交换机
- `deleteExchange`：删除交换机
- `declareQueue`：创建队列
- `deleteQueue`：删除队列
- `purgeQueue`：清空队列
- `declareBinding`：创建绑定关系
- `removeBinding`：删除绑定关系
- ... ...

来看一个 🌰：

*Producer*

```java
@Service
@Slf4j
public class Producer {

    final String QUEUE = "queue.test";
    final String EXCHANGE = "exchange.test";
    final String ROUTING_KEY = "key.test";


    public void initRabbit() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 声明 Exchange
        Exchange exchange = new DirectExchange(EXCHANGE, false, false);
        // 声明 Queue
        Queue queue = new Queue(QUEUE, false);
        // 声明 Binding
        Binding binding = new Binding(
                QUEUE,
                Binding.DestinationType.QUEUE,
                EXCHANGE,
                ROUTING_KEY,
                null
        );
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
    }
}
```
*TestController*

```java
@RestController
public class TestController {

    @Autowired
    private Producer producer;

    @GetMapping("/test")
    public String test() {
        producer.initRabbit();
        return "success";
    }
}
```

启动 Spring Boot 项目，调用接口，我们可以在 RabbitMQ 管控台看到 Exchange，Queue，Binding 声明及创建成功：

![](https://files.mdnice.com/user/19026/1235d0c0-3324-4d94-964f-799ce7705e34.png)

除了手动调用 RabbitAdmin 方法这种方式以外，我们还可以通过 Spring Boot Config 声明式地完成队列，交换机，绑定关系的创建。

Spring-AMQP 充分地发挥了 Spring Boot 的 Convention Over Configuration ，即：约定优于配置的特性。我们可以通过 Spring Boot Config 将 RabbitAdmin 交给 Spring 管理，并声明式地将队列，交换机，绑定关系注册为 Bean，Spring Boot 会为我们自动完成这些组件的创建：

```java
@Configuration
@Slf4j
public class RabbitConfig {

    final String QUEUE = "queue.test";
    final String EXCHANGE = "exchange.test";
    final String ROUTING_KEY = "key.test";

    /**
     * 声明队列 queue.test
     *
     * @return
     */
    @Bean
    public Queue testQueue() {
        return new Queue(QUEUE);
    }


    /**
     * 声明交换机 exchange.test
     *
     * @return
     */
    @Bean
    public Exchange testExchange() {
        return new DirectExchange(EXCHANGE);
    }

    /**
     * 声明绑定关系
     *
     * @return
     */
    @Bean
    public Binding testBinding() {
        return new Binding(QUEUE,
                Binding.DestinationType.QUEUE,
                EXCHANGE,
                ROUTING_KEY,
                null);
    }


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.createConnection();
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
}
```
如上面的代码所示，我们将 RabbitAdmin 注册为一个 Bean，交给 Spring 管理；并将 Exchange，Queue，Binding 都声明为了 Bean。

我们从 RabbitMQ 管控台中将队列，交换机删除后，启动 Spring Boot 项目。

项目启动完成，回到 RabbitMQ 管控台，我们可以发现，Spring Boot “神奇地”创建了我们在 Spring Boot Config 中声明的交换机，队列，以及绑定关系：

![](https://files.mdnice.com/user/19026/79477f96-5f7e-41cd-ad77-cfdd8399f2e0.png)

<font color="orange"><b>Spring AMQP 是如何做到通过 Spring Boot Config 声明式创建 Exchang，Queue，Binding 的？</b></font>

查看源代码，我们可以看到 `RabbitAdmin` 实现了多个接口，其中便有 `ApplicationContextAware` 与 `InitializingBean` 接口。

![](https://files.mdnice.com/user/19026/7a127485-094b-464d-b73b-85839d5df166.png)

`ApplicationContextAware` 接口的作用就是获取应用上下文资源，实现了该接口的 Bean 便可以拿到 Spring 容器（ApplicationContext）。

而 `InitializingBean` 则是一个 Bean 的生命周期接口，对应一个 Bean 的初始化阶段。

在往期文章《Java 面试八股文之框架篇（二）》中，我向大家介绍了 Spring Bean 的完整生命周期，其中便有关于 `InitializingBean` 接口的解读。 

![](https://files.mdnice.com/user/19026/20d7b434-a511-4160-b73b-b0a38b94de9e.png)

该接口只有一个 `afterPropertiesSet()` 方法，当一个 Bean 实现了 `InitializingBean` 接口，那么在这个 Bean 的初始化阶段，便会自动调用 `afterPropertiesSet()` 方法，执行其初始化的逻辑。

我们跟随源码，来到 `RabbitAdmin` 实现的 `afterPropertiesSet()` 方法中，便会看到方法内有如下逻辑：

![](https://files.mdnice.com/user/19026/17a27151-04e4-4d19-811e-e771ad8a5402.png)

`this.connectionFactory.addConnectionListener` 该方法的作用是为 `ConnectionFactory` 添加连接监听器，一旦发现有连接，即会回调 Lambda 表达式内的逻辑。

进入到 `initialize()` 方法：

![](https://files.mdnice.com/user/19026/c6bb7fd5-47b7-48cd-82b0-6c21cbd99463.png)

由于 `RabbitAdmin` 实现了 `ApplicationContextAware` 接口，所以它可以获取到整个 Spring 上下文。在逻辑中，我们看到，它获取到了上下文中所有类型为 `Exchange`，`Queue`，`Binding` 的 Bean。

![](https://files.mdnice.com/user/19026/fdb02603-ede7-4b74-9191-afb932dc4d2e.png)

获取到这些 Bean 后，`RabbitAdmin` 便使用如上方式，对 Exchange，Queue，Binding 进行了声明与创建。

**总结归纳**：
1. `RabbitAdmin` 实现了 `ApplicationContextAware` 接口与 `InitializingBean` 接口
2. `RabbitAdmin` 在初始化方法 `afterPropertiesSet()` 中，首先获取到 Spring 容器中，所有类型为 `Exchange`，`Queue`，`Binding` 的 Bean，接着对其进行声明与创建；所以，我们可以通过通 Spring Boot Config 声明式创建 Exchang，Queue，Binding 。

### 3. RabbitTemplate
<hr>

<font color="orange"><b>RabbitTemplate 的基本使用方法</b></font>

在上文中，我们了解了 Spring-AMQP 的核心组件——RabbitAdmin，知道了该如何使用 RabbitAdmin 连接配置客户端，并声明交换机，消息队列与绑定关系。本小节，我将向大家继续讲解 Spring-AMQP 另一个重要的核心组件——RabbitTemplate。

RabbitTemplate 主要功能为收发消息，但是通常我们只使用其**消息发送**的功能。发送消息的方法为：

- `send`
- `convertAndSend`

先来看一下 `send` 的基本使用，示例代码如下：

*RabbitConfig*
```java
@Configuration
@Slf4j
public class RabbitConfig {

    final String QUEUE = "queue.test";
    final String EXCHANGE = "exchange.test";
    final String ROUTING_KEY = "key.test";

    /**
     * 声明队列 queue.test
     *
     * @return
     */
    @Bean
    public Queue testQueue() {
        return new Queue(QUEUE);
    }


    /**
     * 声明交换机 exchange.test
     *
     * @return
     */
    @Bean
    public Exchange testExchange() {
        return new DirectExchange(EXCHANGE);
    }

    /**
     * 声明绑定关系
     *
     * @return
     */
    @Bean
    public Binding testBinding() {
        return new Binding(QUEUE,
                Binding.DestinationType.QUEUE,
                EXCHANGE,
                ROUTING_KEY,
                null);
    }


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }
}
```
我们使用 Spring Boot Config，将 RabbitTemplate 注册为 Bean，交给 Spring 管理。

*Producer*
```java
@Service
@Slf4j
public class Producer {

    final String QUEUE = "queue.test";
    final String EXCHANGE = "exchange.test";
    final String ROUTING_KEY = "key.test";


    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage() {
        String messageToSend = "test message";

        MessageProperties messageProperties = new MessageProperties();
        //  设置单条消息 TTL 为 1 min
        messageProperties.setExpiration("60000");
        Message message = new Message(messageToSend.getBytes(), messageProperties);
        CorrelationData correlationData = new CorrelationData();
        rabbitTemplate.send(
                EXCHANGE,
                ROUTING_KEY,
                message,
                correlationData
        );
        log.info("message sent");
    }
}
```
在 `Producer` 类中，我们使用了 `rabbitTemplate.send` 方法，发送了一条消息。

该方法的第一个参数指定了交换机的名称；第二个参数为路由键名称，第三个参数为一个 `Message` 对象：
```java
Message message = new Message(messageToSend.getBytes(), messageProperties);
```

构建 `Message` 的第一个参数为消息体的 `byte` 数组，第二个参数为 `MessageProperties` 对象，该对象可以指定消息携带属性。示例代码中，我们指定了消息的 TTL，即失效时间为 1 分钟；`send` 方法的最后一个参数为一个 `CorrelationData` 对象，每一个发送的消息都要配备一个 `CorrelationData` 对象，该对象内部仅有一个 id 属性，用来表示当前消息的唯一性。

我们也可以手动指定这条消息唯一的 id，譬如：
```java
CorrelationData correlationData = new CorrelationData(user.getID().toString()));
```
真实的业务场景中，我们一般会通过某种方式（譬如写入数据库）记录下这个 id，用来做纠错与对账。如果不指定 id，那么生成的 `CorrelationData` 对象将使用 `UUID` 来作为唯一 id。 