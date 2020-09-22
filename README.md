# decoupling-quartz
通用 quartz 框架,与具体业务解耦;通过消息队列(rabbitMq)进行交互


初始化 SQL,选择对应数据库的 SQL 文件:
`quartz-2.3.1.jar!/org/quartz/impl/jdbcjobstore/tables_mysql.sql`


|  |  |
| :-----| :---- | 
| SpringBoot | 2.3.3.RELEASE | 
| lombok | 1.18.12 | 
| spring-rabbit | 2.2.10.RELEASE | 
| mapper-spring-boot-starter | 2.1.5 | 
| fastjson | 1.2.73 | 