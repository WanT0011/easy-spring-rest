#启动流程
    1：  启动注册中心
    2：  启动两个服务提供者
    3：  启动服务的消费者
#spring-cloud-example工程结构说明：
    1：  spring-cloud-api： 传输实体定义
    2：  spring-cloud-consumer：  服务的消费这
    3：  spring-cloud-provider-18001：    服务提供者，端口18001
    4：  spring-cloud-provider-18002：    服务提供者，端口18002
    5：  spring-cloud-register-center：   eureka的注册中心服务，所有的注册中心都可以使用它哦